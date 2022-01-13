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
import androidx.annotation.Nullable;

import com.zeoflow.jx.file.ClassName;
import com.zeoflow.jx.file.MethodSpec;
import com.zeoflow.jx.file.ParameterSpec;
import com.zeoflow.jx.file.ParameterizedTypeName;
import com.zeoflow.jx.file.TypeName;

import java.util.ArrayList;
import java.util.List;

import static com.zeoflow.memo.processor.PreferenceChangeListenerGenerator.getChangeListenerFieldName;
import static javax.lang.model.element.Modifier.PUBLIC;

@SuppressWarnings({"WeakerAccess", "SpellCheckingInspection"})
public class PreferenceFieldMethodGenerator
{

    private static final String SETTER_PREFIX = "put";
    private static final String GETTER_PREFIX = "get";
    private static final String KEYNAME_POSTFIX = "KeyName";
    private static final String HAS_PREFIX = "contains";
    private static final String REMOVE_PREFIX = "remove";
    private final PreferenceKeyField keyField;
    private final PreferenceEntityAnnotatedClass annotatedEntityClazz;
    private final String preference = "Memo";

    public PreferenceFieldMethodGenerator(
            PreferenceKeyField keyField,
            PreferenceEntityAnnotatedClass annotatedClass)
    {
        this.keyField = keyField;
        this.annotatedEntityClazz = annotatedClass;
    }

    public List<MethodSpec> getFieldMethods()
    {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        methodSpecs.add(generateGetter());
        methodSpecs.add(generateSetter());
        if (keyField.isObservable)
        {
            methodSpecs.add(generateObserver());
        }
        methodSpecs.add(generateObjectKeyNameSpec());
        methodSpecs.add(generateContainsSpec());
        methodSpecs.add(generateRemoveSpec());
        return methodSpecs;
    }

    private MethodSpec generateObserver()
    {
        TypeName typeName = ParameterSpec.builder(keyField.typeName, keyField.keyName.toLowerCase()).build().type;
        switch (keyField.typeStringName)
        {
            case "Boolean":
                typeName = TypeName.get(Boolean.class);
                break;
            case "Int":
                typeName = TypeName.get(Integer.class);
                break;
            case "Float":
                typeName = TypeName.get(Float.class);
                break;
            case "Long":
                typeName = TypeName.get(Long.class);
                break;
        }
        MethodSpec.Builder builder =
                MethodSpec.methodBuilder(getObserverName())
                        .addModifiers(PUBLIC)
                        .addParameter(ClassName.get("androidx.lifecycle", "LifecycleOwner"), "owner")
                        .addParameter(ParameterizedTypeName.get(
                                ClassName.get("androidx.lifecycle", "Observer"),
                                typeName
                        ), "observer");
        builder.addStatement(this.keyField.keyName + "Observable.observe(owner, observer)");
        return builder.build();
    }

    private MethodSpec generateGetter()
    {
        MethodSpec.Builder builder =
                MethodSpec.methodBuilder(getGetterPrefixName())
                        .addModifiers(PUBLIC);
        if (keyField.value != null) {
            builder.addAnnotation(NonNull.class);
            builder.addStatement(
                    "return " + getGetterStatement(),
                    "Memo",
                    keyField.keyName,
                    keyField.value
            );
        } else {
            builder.addAnnotation(Nullable.class);
            builder.addStatement(
                    "return " + getGetterStatement(),
                    "Memo",
                    keyField.keyName,
                    null
            );
        }
        builder.returns(keyField.typeName);
        return builder.build();
    }

    private MethodSpec generateSetter()
    {
        MethodSpec.Builder builder =
                MethodSpec.methodBuilder(getSetterPrefixName())
                        .addModifiers(PUBLIC)
                        .addParameter(keyField.typeName, keyField.keyName.toLowerCase());
        builder.addStatement(
                getSetterStatement(),
                keyField.keyName,
                keyField.keyName.toLowerCase()
        );
        if (keyField.isObservable)
        {
            builder.addStatement(getOnLiveDataStatement());
        }
        if (keyField.isListener)
        {
            builder.addStatement(getOnChangedStatement());
        }
        return builder.build();
    }

    private String getOnLiveDataStatement()
    {
        return keyField.keyName + "Observable.setValue(" + keyField.keyName.toLowerCase() + ")";
    }

    private MethodSpec generateObjectKeyNameSpec()
    {
        return MethodSpec.methodBuilder(getKeyNamePostfixName())
                .addModifiers(PUBLIC)
                .addAnnotation(NonNull.class)
                .returns(String.class)
                .addStatement("return $S", keyField.keyName)
                .build();
    }

    private MethodSpec generateContainsSpec()
    {
        return MethodSpec.methodBuilder(getContainsPrefixName())
                .addModifiers(PUBLIC)
                .addAnnotation(NonNull.class)
                .addStatement("return $N.contains($S)", preference, keyField.keyName)
                .returns(boolean.class)
                .build();
    }

    private MethodSpec generateRemoveSpec()
    {
        return MethodSpec.methodBuilder(getRemovePrefixName())
                .addModifiers(PUBLIC)
                .addStatement(
                        "$N.delete($S)", preference, keyField.keyName)
                .build();
    }

    private String getGetterPrefixName()
    {
        return GETTER_PREFIX + StringUtils.toUpperCamel(this.keyField.keyName);
    }

    private String getSetterPrefixName()
    {
        return SETTER_PREFIX + StringUtils.toUpperCamel(this.keyField.keyName);
    }

    private String getKeyNamePostfixName()
    {
        return this.keyField.keyName + KEYNAME_POSTFIX;
    }

    private String getContainsPrefixName()
    {
        return HAS_PREFIX + StringUtils.toUpperCamel(this.keyField.keyName);
    }

    private String getRemovePrefixName()
    {
        return REMOVE_PREFIX + StringUtils.toUpperCamel(this.keyField.keyName);
    }

    private String getGetterStatement()
    {
        if (annotatedEntityClazz.getterFunctionsList.containsKey(keyField.keyName))
        {
            String superMethodName =
                    annotatedEntityClazz.getterFunctionsList.get(keyField.keyName).getSimpleName().toString();
            if (keyField.value instanceof String)
            {
                return String.format("super.%s($N.get($S, $S))", superMethodName);
            } else if (keyField.value instanceof Float)
            {
                return String.format(
                        "super.%s($N.get($S, $Lf))", superMethodName);
            } else
            {
                return String.format(
                        "super.%s($N.get($S, $L))", superMethodName);
            }
        } else
        {
            if (keyField.value instanceof String)
            {
                return "$N.get($S, $S)";
            } else if (keyField.value instanceof Float)
            {
                return "$N.get($S, $Lf)";
            } else
            {
                return "$N.get($S, $L)";
            }
        }
    }

    private String getSetterStatement()
    {
        if (annotatedEntityClazz.setterFunctionsList.containsKey(keyField.keyName))
        {
            return String.format(
                    "Memo.put($S, super.%s($N))",
                    annotatedEntityClazz.setterFunctionsList.get(keyField.keyName).getSimpleName());
        } else
        {
            return "Memo.put($S, $N)";
        }
    }

    private String getOnChangedStatement()
    {
        String onChangeListener = getChangeListenerFieldName(keyField.keyName);
        PreferenceChangeListenerGenerator generator = new PreferenceChangeListenerGenerator(keyField);
        return "if ("
                + onChangeListener
                + " != null)\n"
                + "for ("
                + generator.getClazzName()
                + " "
                + "listener : "
                + onChangeListener
                + ") "
                + "listener."
                + PreferenceChangeListenerGenerator.CHANGED_ABSTRACT_METHOD
                + "("
                + keyField.keyName.toLowerCase()
                + ")";
    }

    private String getObserverName()
    {
        return this.keyField.keyName + "Observer";
    }

}
