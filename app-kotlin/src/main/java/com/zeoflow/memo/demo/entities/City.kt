package com.zeoflow.memo.demo.entities

import com.zeoflow.memo.common.*

/**
 * the entity generated will be named UserProfile_MemoEntity
 * it was annotated with @MemoEntity("UserProfile")
 *
 * the entity will be encrypted using the "G15y3aV9M8d" key
 * it was annotated with @EncryptEntity("G15y3aV9M8d")
 */
@MemoEntity
@EncryptEntity("G15y3aV9M8d")
@Hilt
data class City(
    @KeyName("name")
    @Default(NameDefault::class)
    @Observable
    val name: String = "Brasov",

    @KeyName("country")
    @Default(CountryDefault::class)
    @Observable
    val country: String,

    @Observable
    @Default(PopulationDefault::class)
    val population: Int,
)

class NameDefault : DefaultType<String> {
    override fun value(): String {
        return "Brasov"
    }
}

class CountryDefault : DefaultType<String> {
    override fun value(): String {
        return "Romania"
    }
}

class PopulationDefault : DefaultType<Int> {
    override fun value(): Int {
        return 2134357846
    }
}

