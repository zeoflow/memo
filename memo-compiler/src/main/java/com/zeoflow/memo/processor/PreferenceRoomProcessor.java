/*
 * Copyright (C) 2017 zeoflow
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zeoflow.memo.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.VerifyException;
import com.zeoflow.jx.file.ClassName;
import com.zeoflow.jx.file.JavaFile;
import com.zeoflow.jx.file.MethodSpec;
import com.zeoflow.jx.file.ParameterSpec;
import com.zeoflow.jx.file.TypeSpec;
import com.zeoflow.memo.MemoApplication;
import com.zeoflow.memo.annotation.DefaultMemo;
import com.zeoflow.memo.annotation.InjectPreference;
import com.zeoflow.memo.annotation.KeyName;
import com.zeoflow.memo.annotation.MemoComponent;
import com.zeoflow.memo.annotation.MemoEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static javax.tools.Diagnostic.Kind.ERROR;

@SuppressWarnings({"unused", "SimplifyStreamApiCallChains", "RedundantSuppression"})
@AutoService(Processor.class)
public class PreferenceRoomProcessor extends AbstractProcessor
{

    private Map<String, String> annotatedEntityNameMap;
    private Map<String, PreferenceEntityAnnotatedClass> annotatedEntityMap;
    private List<PreferenceComponentAnnotatedClass> annotatedComponentList;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        annotatedEntityMap = new HashMap<>();
        annotatedEntityNameMap = new HashMap<>();
        annotatedComponentList = new ArrayList<>();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(MemoComponent.class.getCanonicalName());
        supportedTypes.add(MemoEntity.class.getCanonicalName());
        supportedTypes.add(DefaultMemo.class.getCanonicalName());
        supportedTypes.add(KeyName.class.getCanonicalName());
        supportedTypes.add(InjectPreference.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (annotations.isEmpty())
        {
            return true;
        }

        roundEnv.getElementsAnnotatedWith(MemoEntity.class).stream()
                .map(annotatedType -> (TypeElement) annotatedType)
                .forEach(
                        annotatedType ->
                        {
                            try
                            {
                                checkValidEntityType(annotatedType);
                                processEntity(annotatedType);
                            } catch (IllegalAccessException e)
                            {
                                showErrorLog(e.getMessage(), annotatedType);
                            }
                        });

        roundEnv.getElementsAnnotatedWith(MemoComponent.class).stream()
                .map(annotatedType -> (TypeElement) annotatedType)
                .forEach(
                        annotatedType ->
                        {
                            try
                            {
                                checkValidComponentType(annotatedType);
                                processComponent(annotatedType);
                            } catch (IllegalAccessException e)
                            {
                                showErrorLog(e.getMessage(), annotatedType);
                            }
                        });

        roundEnv.getElementsAnnotatedWith(InjectPreference.class).stream()
                .filter(variable -> variable instanceof VariableElement)
                .map(variable -> (VariableElement) variable)
                .forEach(
                        variable ->
                        {
                            try
                            {
                                if (!variable.getModifiers().contains(Modifier.PUBLIC))
                                {
                                    throw new IllegalAccessException(
                                            "annotated with @InjectPreference field's modifier should be public");
                                }
                            } catch (IllegalAccessException e)
                            {
                                showErrorLog(e.getMessage(), variable);
                            }
                        });

        annotatedComponentList.stream().forEach(this::processInjector);

        return true;
    }

    private void processInjector(PreferenceComponentAnnotatedClass annotatedClass)
            throws VerifyException
    {
        try
        {
            annotatedClass.annotatedElement.getEnclosedElements().stream()
                    .filter(element -> element instanceof ExecutableElement)
                    .map(element -> (ExecutableElement) element)
                    .filter(method -> method.getParameters().size() == 1)
                    .forEach(
                            method ->
                            {
                                MethodSpec methodSpec = MethodSpec.overriding(method).build();
                                ParameterSpec parameterSpec = methodSpec.parameters.get(0);
                                TypeElement injectedElement =
                                        processingEnv.getElementUtils().getTypeElement(parameterSpec.type.toString());
                                generateProcessInjector(annotatedClass, injectedElement);
                            });
        } catch (VerifyException e)
        {
            showErrorLog(e.getMessage(), annotatedClass.annotatedElement);
            e.printStackTrace();
        }
    }

    private void generateProcessInjector(
            PreferenceComponentAnnotatedClass annotatedClass, TypeElement injectedElement)
    {
        try
        {
            InjectorGenerator injectorGenerator =
                    new InjectorGenerator(annotatedClass, injectedElement, processingEnv.getElementUtils());
            TypeSpec injectorSpec = injectorGenerator.generate();
            JavaFile.builder(injectorGenerator.packageName, injectorSpec)
                    .addStaticImport(ClassName.get(MemoApplication.class), "getContext")
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e)
        {
            // ignore ^v^
        }
    }

    private void processComponent(TypeElement annotatedType) throws VerifyException
    {
        try
        {
            PreferenceComponentAnnotatedClass annotatedClazz =
                    new PreferenceComponentAnnotatedClass(
                            annotatedType, processingEnv.getElementUtils(), annotatedEntityNameMap);
            checkDuplicatedPreferenceComponent(annotatedClazz);
            generateProcessComponent(annotatedClazz);
        } catch (VerifyException e)
        {
            showErrorLog(e.getMessage(), annotatedType);
            e.printStackTrace();
        }
    }

    private void generateProcessComponent(PreferenceComponentAnnotatedClass annotatedClass)
    {
        try
        {
            TypeSpec annotatedClazz =
                    (new PreferenceComponentGenerator(
                            annotatedClass, annotatedEntityMap, processingEnv.getElementUtils()))
                            .generate();
            JavaFile.builder(annotatedClass.packageName, annotatedClazz)
                    .addStaticImport(ClassName.get(MemoApplication.class), "getContext")
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e)
        {
            // ignore >.<
        }
    }

    private void processEntity(TypeElement annotatedType) throws VerifyException
    {
        try
        {
            PreferenceEntityAnnotatedClass annotatedClazz =
                    new PreferenceEntityAnnotatedClass(annotatedType, processingEnv.getElementUtils());
            checkDuplicatedPreferenceEntity(annotatedClazz);
            generateProcessEntity(annotatedClazz);
        } catch (VerifyException e)
        {
            showErrorLog(e.getMessage(), annotatedType);
            e.printStackTrace();
        }
    }

    private void generateProcessEntity(PreferenceEntityAnnotatedClass annotatedClass)
    {
        try
        {
            TypeSpec annotatedClazz = new PreferenceEntityGenerator(
                    annotatedClass,
                    processingEnv.getElementUtils()
            ).generate();
            JavaFile.builder(annotatedClass.packageName, annotatedClazz)
                    .addStaticImport(ClassName.get(MemoApplication.class), "getContext")
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException e)
        {
            // ignore ;)
        }
    }

    private void checkValidEntityType(TypeElement annotatedType) throws IllegalAccessException
    {
        if (!annotatedType.getKind().isClass())
        {
            throw new IllegalAccessException("Only classes can be annotated with @MemoStorage");
        } else if (annotatedType.getModifiers().contains(Modifier.FINAL))
        {
            showErrorLog("class modifier should not be final", annotatedType);
        } else if (annotatedType.getModifiers().contains(Modifier.PRIVATE))
        {
            showErrorLog("class modifier should not be final", annotatedType);
        }
    }

    private void checkDuplicatedPreferenceEntity(PreferenceEntityAnnotatedClass annotatedClazz)
            throws VerifyException
    {
        if (annotatedEntityMap.containsKey(annotatedClazz.entityName))
        {
            throw new VerifyException("@MemoStorage key value is duplicated.");
        } else
        {
            annotatedEntityMap.put(annotatedClazz.entityName, annotatedClazz);
            int start = annotatedClazz.typeName.toString().indexOf("<");
            int end = annotatedClazz.typeName.toString().lastIndexOf(">") + 1;
            if (start != -1)
            {
                String className = annotatedClazz.typeName.toString().substring(0, start) + annotatedClazz.typeName.toString().substring(end);
                annotatedEntityNameMap.put(className + ".class", annotatedClazz.entityName);
            } else
            {
                annotatedEntityNameMap.put(annotatedClazz.typeName + ".class", annotatedClazz.entityName);
            }
        }
    }

    private void checkDuplicatedPreferenceComponent(
            PreferenceComponentAnnotatedClass annotatedClazz)
    {
        if (annotatedComponentList.contains(annotatedClazz))
        {
            throw new VerifyException("@MemoComponent is duplicated.");
        } else
        {
            annotatedComponentList.add(annotatedClazz);
        }
    }

    private void checkValidComponentType(TypeElement annotatedType) throws IllegalAccessException
    {
        if (!annotatedType.getKind().isInterface())
        {
            throw new IllegalAccessException(
                    "Only interfaces can be annotated with @MemoComponent");
        }
    }

    private void showErrorLog(String message, Element element)
    {
        messager.printMessage(ERROR, StringUtils.getErrorMessagePrefix() + message, element);
    }

}
