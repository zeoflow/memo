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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zeoflow.app.Activity;
import com.zeoflow.demo.components.AppComponent_Memo;
import com.zeoflow.demo.entities.UserProfile_MemoEntity;
import com.zeoflow.demo.models.PrivateInfo;
import com.zeoflow.memo.annotation.InjectPreference;

public class LoginActivity extends Activity
{

    /**
     * UserProfile entity. {@link com.zeoflow.demo.entities.Profile}
     */
    @InjectPreference
    public AppComponent_Memo component;
    @InjectPreference
    public UserProfile_MemoEntity userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppComponent_Memo.getInstance().inject(this);

        final EditText editText_nick = findViewById(R.id.login_editText_nick);
        final EditText editText_age = findViewById(R.id.login_editText_age);
        final Button button = findViewById(R.id.login_button);
        button.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        String inputNick = editText_nick.getText().toString();
                        String inputAge = editText_age.getText().toString();
                        if (!inputNick.equals("") && !inputAge.equals(""))
                        {
                            userProfile.putLogin(true);
                            userProfile.putNickname(inputNick);
                            userProfile.putUserinfo(new PrivateInfo(inputNick, Integer.parseInt(inputAge)));
                            finish();
                        } else
                        {
                            Toast.makeText(getBaseContext(), "please fill all inputs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        userProfile.addNicknameListeners(new UserProfile_MemoEntity.NicknameIOnChangedListener()
        {
            @Override
            public void onChanged(String nickname)
            {
                Toast.makeText(getBaseContext(), "onChanged :" + nickname, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
