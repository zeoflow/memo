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

package com.zeoflow.demo.converters;

import com.zeoflow.memo.annotation.PreferenceTypeConverter;
import com.zeoflow.zson.Zson;

public class BaseZsonConverter<T> extends PreferenceTypeConverter<T>
{

    private final Zson zson;

    /**
     * default constructor will be called by PreferenceRoom
     */
    public BaseZsonConverter(Class<T> clazz)
    {
        super(clazz);
        this.zson = new Zson();
    }

    @Override
    public String convertObject(T object)
    {
        return zson.toJson(object);
    }

    @Override
    public T convertType(String string)
    {
        return zson.fromJson(string, clazz);
    }

}
