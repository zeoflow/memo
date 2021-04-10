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

import com.zeoflow.demo.models.Pet;
import com.zeoflow.demo.models.PrivateInfo;
import com.zeoflow.memo.annotation.KeyName;
import com.zeoflow.memo.annotation.Listener;
import com.zeoflow.memo.annotation.Observable;
import com.zeoflow.memo.annotation.PreferenceEntity;
import com.zeoflow.memo.annotation.PreferenceFunction;

@PreferenceEntity("UserProfile")
public class Profile
{

    @KeyName("nickname")
    @Observable
    @Listener
    protected final String userNickName = "zeoflow";

    /**
     * key value will be 'Login'. (login's camel uppercase)
     */
    protected final boolean login = false;

    @KeyName("visits")
    protected final int visitCount = 1;

    @KeyName("userinfo")
    protected PrivateInfo privateInfo;

    /**
     * value used with zson.
     */
    @KeyName("userPet")
    protected Pet userPetInfo;

    /**
     * preference putter function about userNickName.
     *
     * @param nickname function in
     *
     * @return function out
     */
    @PreferenceFunction("nickname")
    public String putUserNickFunction(String nickname)
    {
        return "Hello, " + nickname;
    }

    /**
     * preference getter function about userNickName.
     *
     * @param nickname function in
     *
     * @return function out
     */
    @PreferenceFunction("nickname")
    public String getUserNickFunction(String nickname)
    {
        return nickname + "!!!";
    }

    /**
     * preference putter function example about visitCount's auto increment.
     *
     * @param count function in
     *
     * @return function out
     */
    @PreferenceFunction("visits")
    public int putVisitCountFunction(int count)
    {
        return ++count;
    }

}
