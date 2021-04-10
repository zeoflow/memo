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

import androidx.annotation.NonNull;

import com.google.common.base.Strings;
import com.google.common.base.VerifyException;
import com.zeoflow.jx.file.MethodSpec;
import com.zeoflow.jx.file.TypeName;
import com.zeoflow.memo.annotation.DefaultMemo;
import com.zeoflow.memo.annotation.EncryptEntity;
import com.zeoflow.memo.annotation.MemoEntity;
import com.zeoflow.memo.annotation.MemoFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@SuppressWarnings("WeakerAccess")
public class PreferenceEntityAnnotatedClass
{

    private static final String SETTER_PREFIX = "put";
    private static final String GETTER_PREFIX = "get";
    private static final String HAS_PREFIX = "contains";
    private static final String REMOVE_PREFIX = "remove";
    public final String packageName;
    public final TypeElement annotatedElement;
    public final TypeName typeName;
    public final String clazzName;
    public final String entityName;
    public final List<PreferenceKeyField> keyFields;
    public final List<String> keyNameFields;
    public final Map<String, PreferenceKeyField> keyFieldMap;
    public final Map<String, Element> setterFunctionsList;
    public final Map<String, Element> getterFunctionsList;
    public boolean isDefaultPreference = false;
    public boolean isEncryption = false;
    public String encryptionKey = null;

    public PreferenceEntityAnnotatedClass(
            @NonNull TypeElement annotatedElement, @NonNull Elements elementUtils)
            throws VerifyException
    {
        MemoEntity memoEntity = annotatedElement.getAnnotation(MemoEntity.class);
        DefaultMemo defaultMemo = annotatedElement.getAnnotation(DefaultMemo.class);
        EncryptEntity encryptEntity = annotatedElement.getAnnotation(EncryptEntity.class);
        PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
        this.packageName =
                packageElement.isUnnamed() ? null : packageElement.getQualifiedName().toString();
        this.annotatedElement = annotatedElement;
        this.typeName = TypeName.get(annotatedElement.asType());
        this.clazzName = annotatedElement.getSimpleName().toString();
        this.entityName =
                Strings.isNullOrEmpty(memoEntity.value())
                        ? StringUtils.toUpperCamel(this.clazzName)
                        : memoEntity.value();
        this.keyFields = new ArrayList<>();
        this.keyNameFields = new ArrayList<>();
        this.keyFieldMap = new HashMap<>();
        this.setterFunctionsList = new HashMap<>();
        this.getterFunctionsList = new HashMap<>();

        if (defaultMemo != null) isDefaultPreference = true;
        if (encryptEntity != null && !encryptEntity.value().isEmpty())
        {
            isEncryption = true;
            encryptionKey = encryptEntity.value();
        }

        if (Strings.isNullOrEmpty(entityName))
        {
            throw new VerifyException("You should entity MemoStorage class value.");
        }

        Map<String, String> checkMap = new HashMap<>();
        annotatedElement.getEnclosedElements().stream()
                .filter(variable -> variable instanceof VariableElement)
                .map(variable -> (VariableElement) variable)
                .forEach(
                        variable ->
                        {
                            try
                            {
                                PreferenceKeyField keyField = new PreferenceKeyField(variable, elementUtils);

                                if (checkMap.get(keyField.keyName) != null)
                                {
                                    throw new VerifyException(
                                            String.format("'%s' key is already used in class.", keyField.keyName));
                                }

                                checkMap.put(keyField.keyName, keyField.clazzName);
                                keyFields.add(keyField);
                                keyNameFields.add(keyField.keyName);
                                keyFieldMap.put(keyField.keyName, keyField);
                            } catch (IllegalAccessException e)
                            {
                                throw new VerifyException(e.getMessage());
                            }
                        });

        checkOverrideMethods();

        annotatedElement.getEnclosedElements().stream()
                .filter(
                        function ->
                                !function.getKind().isField()
                                        && function.getModifiers().contains(Modifier.PUBLIC)
                                        && function.getAnnotation(MemoFunction.class) != null)
                .forEach(
                        function ->
                        {
                            MemoFunction annotation = function.getAnnotation(MemoFunction.class);
                            String keyName = annotation.value();
                            if (keyNameFields.contains(keyName))
                            {
                                if (function.getSimpleName().toString().startsWith(SETTER_PREFIX))
                                {
                                    setterFunctionsList.put(keyName, function);
                                } else if (function.getSimpleName().toString().startsWith(GETTER_PREFIX))
                                {
                                    getterFunctionsList.put(keyName, function);
                                } else
                                {
                                    throw new VerifyException(
                                            String.format(
                                                    "MemoFunction's prefix should startWith 'get' or 'put' : %s",
                                                    function.getSimpleName()));
                                }
                            } else
                            {
                                throw new VerifyException(
                                        String.format("keyName '%s' is not exist in entity.", keyName));
                            }

                            MethodSpec methodSpec = MethodSpec.overriding((ExecutableElement) function).build();
                            if (methodSpec.parameters.size() != 1)
                            {
                                throw new VerifyException("MemoFunction should has one parameter");
                            } else if (!methodSpec
                                    .parameters
                                    .get(0)
                                    .type
                                    .equals(keyFieldMap.get(keyName).typeName))
                            {
                                throw new VerifyException(
                                        String.format(
                                                "parameter '%s''s type should be %s.",
                                                methodSpec.parameters.get(0).name, keyFieldMap.get(keyName).typeName));
                            } else if (!methodSpec.returnType.equals(keyFieldMap.get(keyName).typeName))
                            {
                                throw new VerifyException(
                                        String.format(
                                                "method '%s''s return type should be %s.",
                                                methodSpec.name, keyFieldMap.get(keyName).typeName));
                            }
                        });
    }

    private void checkOverrideMethods()
    {
        annotatedElement.getEnclosedElements().stream()
                .filter(element -> element instanceof ExecutableElement)
                .map(element -> (ExecutableElement) element)
                .forEach(
                        method ->
                        {
                            if (keyNameFields.contains(
                                    method.getSimpleName().toString().replace(SETTER_PREFIX, "")))
                            {
                                throw new VerifyException(
                                        getMethodNameVerifyErrorMessage(method.getSimpleName().toString()));
                            } else if (keyNameFields.contains(
                                    method.getSimpleName().toString().replace(GETTER_PREFIX, "")))
                            {
                                throw new VerifyException(
                                        getMethodNameVerifyErrorMessage(method.getSimpleName().toString()));
                            } else if (keyNameFields.contains(
                                    method.getSimpleName().toString().replace(HAS_PREFIX, "")))
                            {
                                throw new VerifyException(
                                        getMethodNameVerifyErrorMessage(method.getSimpleName().toString()));
                            } else if (keyNameFields.contains(
                                    method.getSimpleName().toString().replace(REMOVE_PREFIX, "")))
                            {
                                throw new VerifyException(
                                        getMethodNameVerifyErrorMessage(method.getSimpleName().toString()));
                            }
                        });
    }

    private String getMethodNameVerifyErrorMessage(String methodName)
    {
        return String.format("can not use method value '%s'. Use an another one.", methodName);
    }

}
