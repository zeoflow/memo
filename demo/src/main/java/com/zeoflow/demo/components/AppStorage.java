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

package com.zeoflow.demo.components;

import com.zeoflow.demo.LoginActivity;
import com.zeoflow.demo.MainActivity;
import com.zeoflow.demo.entities.Country;
import com.zeoflow.demo.entities.User;
import com.zeoflow.memo.annotation.MemoComponent;

/**
 * Component that integrates memo entities; it must be an interface
 * and annotated with @MemoComponent. The generated class will end in
 * $_Memo (generated class for this interface will be AppStorage_Memo
 *
 * inside this Memo manager, the following MemoEntities are injected:
 * - User
 * - Country
 */
@MemoComponent(entities = {User.class, Country.class})
public interface AppStorage
{

    /**
     * declare dependency injection target MaiActivity.
     */
    void inject(MainActivity mainActivity);

    /**
     * declare dependency injection target LoginActivity.
     */
    void inject(LoginActivity loginActivity);

}
