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
import com.zeoflow.jx.file.TypeName;
import com.zeoflow.memo.annotation.KeyName;
import com.zeoflow.memo.annotation.Listener;
import com.zeoflow.memo.annotation.Observable;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

@SuppressWarnings({"WeakerAccess"})
public class PreferenceKeyField
{

    public final VariableElement variableElement;
    public final String packageName;
    public final TypeName typeName;
    public final String clazzName;
    public String typeStringName;
    public String variableName;
    public String keyName;
    public Object value;
    public boolean isFinal;

    public boolean isObjectField = false;
    public boolean isObservable = false;
    public boolean isListener = false;

    public PreferenceKeyField(
            @NonNull VariableElement variableElement, @NonNull Elements elementUtils)
            throws IllegalAccessException
    {
        KeyName annotation_keyName = variableElement.getAnnotation(KeyName.class);
        this.variableElement = variableElement;
        PackageElement packageElement = elementUtils.getPackageOf(variableElement);
        this.packageName =
                packageElement.isUnnamed() ? null : packageElement.getQualifiedName().toString();
        this.typeName = TypeName.get(variableElement.asType());
        this.clazzName = variableElement.getSimpleName().toString();
        this.value = variableElement.getConstantValue();
        this.variableName = variableElement.getSimpleName().toString();
        this.isFinal = variableElement.getModifiers().contains(Modifier.FINAL);
        setTypeStringName();

        if (annotation_keyName != null)
        {
            this.keyName =
                    Strings.isNullOrEmpty(annotation_keyName.value())
                            ? StringUtils.toLowerCamel(this.clazzName)
                            : annotation_keyName.value();
        } else
        {
            this.keyName = StringUtils.toLowerCamel(this.clazzName);
        }
        if (variableElement.getAnnotation(Observable.class) != null)
        {
            isObservable = true;
        }
        if (variableElement.getAnnotation(Listener.class) != null)
        {
            isListener = true;
        }

        if (variableElement.getModifiers().contains(Modifier.PRIVATE))
        {
            throw new IllegalAccessException(
                    String.format("Field '%s' should not be private.", variableElement.getSimpleName()));
        } else if (!this.isObjectField && !variableElement.getModifiers().contains(Modifier.FINAL))
        {
            throw new IllegalAccessException(
                    String.format("Field '%s' should be final.", variableElement.getSimpleName()));
        }
    }

    private void setTypeStringName()
    {
        if (this.typeName.equals(TypeName.BOOLEAN))
        {
            this.typeStringName = "Boolean";
        } else if (this.typeName.equals(TypeName.INT))
        {
            this.typeStringName = "Int";
        } else if (this.typeName.equals(TypeName.FLOAT))
        {
            this.typeStringName = "Float";
        } else if (this.typeName.equals(TypeName.LONG))
        {
            this.typeStringName = "Long";
        } else if (this.typeName.equals(TypeName.get(String.class)))
        {
            this.typeStringName = "String";
        } else
        {
            this.typeStringName = "Object";
            this.isObjectField = true;
        }
    }

}
