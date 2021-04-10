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

package com.zeoflow.demo.entities;

import com.zeoflow.memo.annotation.KeyName;
import com.zeoflow.memo.annotation.MemoEntity;
import com.zeoflow.memo.annotation.MemoFunction;

@MemoEntity("Country")
public class Country
{

    @KeyName("countryCode")
    public final String countryCode = null;

    @KeyName("country")
    public final String countryName = "United Kingdom";

    /**
     * preference putter function example toUpperCase().
     *
     * @param country function in
     *
     * @return function out
     */
    @MemoFunction("country")
    public String putCountryFunction(String country)
    {
        return country.toUpperCase();
    }

    /**
     * preference putter function example toLowerCase().
     *
     * @param country function in
     *
     * @return function out
     */
    @MemoFunction("country")
    public String getCountryFunction(String country)
    {
        return country.toLowerCase();
    }

}
