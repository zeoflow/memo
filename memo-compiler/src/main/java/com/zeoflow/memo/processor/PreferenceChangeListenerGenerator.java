/*
 * Copyright (C) 2022 ZeoFlow SRL
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

import static javax.lang.model.element.Modifier.PUBLIC;

import androidx.annotation.NonNull;

import com.zeoflow.jx.file.ClassName;
import com.zeoflow.jx.file.FieldSpec;
import com.zeoflow.jx.file.MethodSpec;
import com.zeoflow.jx.file.ParameterSpec;
import com.zeoflow.jx.file.ParameterizedTypeName;
import com.zeoflow.jx.file.TypeSpec;
import com.zeoflow.memo.annotation.IMemoChangedListener;

import java.util.List;

import javax.lang.model.element.Modifier;

@SuppressWarnings("WeakerAccess")
public class PreferenceChangeListenerGenerator {

    public static final String CHANGED_LISTENER_POSTFIX = "IOnChangedListener";
    public static final String CHANGED_ABSTRACT_METHOD = "onChanged";
    public static final String CHANGED_LISTENER_PREFIX = "Listeners";
    private final PreferenceKeyField keyField;

    public PreferenceChangeListenerGenerator(PreferenceKeyField keyField) {
        this.keyField = keyField;
    }

    public static String getChangeListenerFieldName(String keyName) {
        return StringUtils.toLowerCamel(keyName) + CHANGED_LISTENER_PREFIX;
    }

    public TypeSpec generateInterface() {
        TypeSpec.Builder builder =
                TypeSpec.interfaceBuilder(getClazzName())
                        .addModifiers(PUBLIC)
                        .addSuperinterface(IMemoChangedListener.class)
                        .addMethod(getOnChangedSpec());
        return builder.build();
    }

    public FieldSpec generateField(String className) {
        return FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(List.class), getInterfaceType(className)),
                getFieldName(),
                Modifier.PRIVATE,
                Modifier.FINAL
        ).addAnnotation(NonNull.class)
                .initializer("new ArrayList()")
                .build();
    }

    private MethodSpec getOnChangedSpec() {
        return MethodSpec.methodBuilder(CHANGED_ABSTRACT_METHOD)
                .addParameter(
                        ParameterSpec.builder(keyField.typeName, keyField.keyName.toLowerCase()).build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .build();
    }

    public String getClazzName() {
        return StringUtils.toUpperCamel(keyField.keyName) + CHANGED_LISTENER_POSTFIX;
    }

    private String getFieldName() {
        return StringUtils.toLowerCamel(keyField.keyName) + CHANGED_LISTENER_PREFIX;
    }

    public ClassName getInterfaceType(String className) {
        return ClassName.get(keyField.packageName + "." + className, getClazzName());
    }

}
