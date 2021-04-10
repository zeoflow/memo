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

package com.zeoflow.demo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zeoflow.app.Activity;
import com.zeoflow.demo.components.AppStorage_Memo;
import com.zeoflow.demo.entities.PrivateInfo;
import com.zeoflow.demo.entities.User;
import com.zeoflow.demo.entities.UserProfile_MemoEntity;
import com.zeoflow.memo.annotation.InjectPreference;
import com.zeoflow.utils.string.StringCreator;

public class LoginActivity extends Activity
{

    /**
     * UserProfile entity. {@link User}
     */
    @InjectPreference
    public AppStorage_Memo component;
    @InjectPreference
    public UserProfile_MemoEntity userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppStorage_Memo.getInstance().inject(this);

        final EditText editText_username = findViewById(R.id.login_editText_username);
        final EditText editText_firstName = findViewById(R.id.login_editText_firstName);
        final EditText editText_lastName = findViewById(R.id.login_editText_lastName);
        final Button button = findViewById(R.id.login_button);
        button.setOnClickListener(view ->
        {
            String inputUsername = editText_username.getText().toString();
            String inputFirstName = editText_firstName.getText().toString();
            String inputLastName = editText_lastName.getText().toString();
            if (!inputUsername.equals("") && !inputFirstName.equals("") && !inputLastName.equals(""))
            {
                userProfile.putLogin(true);
                userProfile.putUsername(inputUsername);
                userProfile.putUserinfo(new PrivateInfo(inputFirstName, inputLastName));
                finish();
            } else
            {
                Toast.makeText(getBaseContext(), "please fill all inputs", Toast.LENGTH_SHORT).show();
            }
        });
        userProfile.addLoginListeners(login ->
        {
            String content = StringCreator.creator()
                    .add("User profile updated! Welcome, $N!", userProfile.getUsername())
                    .asString();
            Toast.makeText(getBaseContext(), content, Toast.LENGTH_SHORT).show();
        });
    }

}
