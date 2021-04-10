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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.zeoflow.app.Activity;
import com.zeoflow.demo.components.AppStorage;
import com.zeoflow.demo.components.AppStorage_Memo;
import com.zeoflow.demo.utils.ItemProfile;
import com.zeoflow.demo.utils.ListViewAdapter;
import com.zeoflow.memo.ConcealEncryption;
import com.zeoflow.memo.Memo;
import com.zeoflow.memo.annotation.InjectPreference;

public class MainActivity extends Activity
{

    /**
     * UserProfile Component. {@link AppStorage}
     */
    @InjectPreference
    public AppStorage_Memo component;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppStorage_Memo.getInstance()
                .inject(this);

        initializeUI();

        component.UserProfile().usernameObserver(this, nickname -> initializeUI());

//        memoExample();
    }

    private void memoExample()
    {
        timeMemoInit();
        timeMemoPut();
        timeMemoGet();
        timeMemoContains();
        timeMemoCount();
        timeMemoDelete();
        timeMemoEncrypt();
        timeMemoDecrypt();
    }

    private void timeMemoInit()
    {
        long startTime = System.currentTimeMillis();

        Memo.init()
                .setEncryption(new ConcealEncryption("dgdffhghdfhfgh"))
                .build();

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.init: " + (endTime - startTime) + "ms");
    }

    private void timeMemoPut()
    {
        long startTime = System.currentTimeMillis();

        Memo.put("key", "value");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.put: " + (endTime - startTime) + "ms");
    }

    private void timeMemoGet()
    {
        long startTime = System.currentTimeMillis();

        Memo.get("key");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.get: " + (endTime - startTime) + "ms");
    }

    private void timeMemoCount()
    {
        long startTime = System.currentTimeMillis();

        Memo.count();

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.count: " + (endTime - startTime) + "ms");
    }

    private void timeMemoContains()
    {
        long startTime = System.currentTimeMillis();

        Memo.contains("key");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.count: " + (endTime - startTime) + "ms");
    }

    private void timeMemoDelete()
    {
        long startTime = System.currentTimeMillis();

        Memo.delete("key");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.count: " + (endTime - startTime) + "ms");
    }

    private void timeMemoEncrypt()
    {
        long startTime = System.currentTimeMillis();

        System.out.println("e: " + Memo.encrypt(42335));

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.encrypt: " + (endTime - startTime) + "ms");
    }

    private void timeMemoDecrypt()
    {
        long startTime = System.currentTimeMillis();

        System.out.println("v: " + Memo.decrypt(Memo.encrypt(42335)));

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.decrypt: " + (endTime - startTime) + "ms");
    }

    private void initializeUI()
    {
        if (!component.UserProfile().getLogin())
        {
            Toast.makeText(zContext, "No user registered. Please register one.", Toast.LENGTH_SHORT).show();
            configureNewActivity(LoginActivity.class)
                    .start();
        } else
        {
            Button needLoginView = findViewById(R.id.content_button);
            needLoginView.setOnClickListener(view ->
            {
                configureNewActivity(LoginActivity.class)
                        .start();
            });
            ListViewAdapter adapter = new ListViewAdapter(this, R.layout.item_profile);

            ListView listView = findViewById(R.id.content_listView);
            ViewCompat.setNestedScrollingEnabled(listView, true);
            listView.setAdapter(adapter);

            adapter.addItem(new ItemProfile("Message", component.UserProfile().getUsername()));
            adapter.addItem(new ItemProfile("Full Name", component.UserProfile().getFullName()));
            adapter.addItem(new ItemProfile("First Name", component.UserProfile().getUserinfo().getFirstName()));
            adapter.addItem(new ItemProfile("Last Name", component.UserProfile().getUserinfo().getLastName()));
            adapter.addItem(new ItemProfile("Views", component.UserProfile().getViews() + ""));
            adapter.addItem(new ItemProfile("Details", component.UserProfile().getFullNameAndViews()));
            component.UserProfile().putViews(component.UserProfile().getViews());

            if (component.Country().getCountryCode() == null)
            {
                component.Country().putCountry("Romania");
                component.Country().putCountryCode("RO");
            }
            adapter.addItem(new ItemProfile("country", component.Country().getCountry()));
            adapter.addItem(new ItemProfile("country code", component.Country().getCountryCode()));
        }
    }

}
