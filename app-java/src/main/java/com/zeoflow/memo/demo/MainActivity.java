package com.zeoflow.memo.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zeoflow.memo.ConcealEncryption;
import com.zeoflow.memo.Memo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memoExample();
    }

    private void memoExample() {
        timeMemoInit();
        timeMemoPut();
        timeMemoGet();
        timeMemoContains();
        timeMemoCount();
        timeMemoDelete();
        timeMemoEncrypt();
        timeMemoDecrypt();
    }

    private void timeMemoInit() {
        long startTime = System.currentTimeMillis();

        Memo.Companion.init().withEncryption(
                new ConcealEncryption("dgdffhghdfhfgh")
        ).build();

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.init: " + (endTime - startTime) + "ms");
    }

    private void timeMemoPut() {
        long startTime = System.currentTimeMillis();

        Memo.Companion.put("key", "value");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.put: " + (endTime - startTime) + "ms");
    }

    private void timeMemoGet() {
        long startTime = System.currentTimeMillis();

        Memo.Companion.get("key");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.get: " + (endTime - startTime) + "ms");
    }

    private void timeMemoCount() {
        long startTime = System.currentTimeMillis();

        Memo.Companion.count();

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.count: " + (endTime - startTime) + "ms");
    }

    private void timeMemoContains() {
        long startTime = System.currentTimeMillis();

        Memo.Companion.contains("key");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.count: " + (endTime - startTime) + "ms");
    }

    private void timeMemoDelete() {
        long startTime = System.currentTimeMillis();

        Memo.Companion.delete("key");

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.count: " + (endTime - startTime) + "ms");
    }

    private void timeMemoEncrypt() {
        long startTime = System.currentTimeMillis();

        System.out.println("e: " + Memo.Companion.encrypt(42335));

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.encrypt: " + (endTime - startTime) + "ms");
    }

    private void timeMemoDecrypt() {
        long startTime = System.currentTimeMillis();

        System.out.println("v: " + Memo.Companion.decrypt(Memo.Companion.encrypt(42335)));

        long endTime = System.currentTimeMillis();
        System.out.println("Memo.decrypt: " + (endTime - startTime) + "ms");
    }
}