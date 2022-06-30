package com.zeoflow.memo.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zeoflow.memo.ConcealEncryption
import com.zeoflow.memo.Memo
import com.zeoflow.memo.demo.entities.CityMemo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val cityMemo = CityMemo()
        memoExample()
        cityMemo.nameObservable(this) {
            println("name: $it")
        }
    }

    private fun memoExample() {
        timeMemoInit()
        timeMemoPut()
        timeMemoGet()
        timeMemoContains()
        timeMemoCount()
        timeMemoDelete()
        timeMemoEncrypt()
        timeMemoDecrypt()
    }

    private fun timeMemoInit() {
        val startTime = System.currentTimeMillis()
        Memo.init().withEncryption(
            ConcealEncryption("2343532sdafg54")
        ).build()
        val endTime = System.currentTimeMillis()
        println("Memo.init: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoPut() {
        val startTime = System.currentTimeMillis()
        Memo.put("key", "value")
        val endTime = System.currentTimeMillis()
        println("Memo.put: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoGet() {
        val startTime = System.currentTimeMillis()
        Memo.get<Any>("key")
        val endTime = System.currentTimeMillis()
        println("Memo.get: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoCount() {
        val startTime = System.currentTimeMillis()
        Memo.count()
        val endTime = System.currentTimeMillis()
        println("Memo.count: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoContains() {
        val startTime = System.currentTimeMillis()
        Memo.contains("key")
        val endTime = System.currentTimeMillis()
        println("Memo.count: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoDelete() {
        val startTime = System.currentTimeMillis()
        Memo.delete("key")
        val endTime = System.currentTimeMillis()
        println("Memo.count: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoEncrypt() {
        val startTime = System.currentTimeMillis()
        println("e: " + Memo.encrypt(42335))
        val endTime = System.currentTimeMillis()
        println("Memo.encrypt: " + (endTime - startTime) + "ms")
    }

    private fun timeMemoDecrypt() {
        val startTime = System.currentTimeMillis()
        println("v: " + Memo.decrypt(Memo.encrypt(42335)))
        val endTime = System.currentTimeMillis()
        println("Memo.decrypt: " + (endTime - startTime) + "ms")
    }
}