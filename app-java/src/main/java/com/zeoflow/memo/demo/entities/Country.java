
package com.zeoflow.memo.demo.entities;

import android.database.Observable;

import com.zeoflow.memo.common.Hilt;
import com.zeoflow.memo.common.KeyName;
import com.zeoflow.memo.common.MemoEntity;
import com.zeoflow.memo.common.MemoFunction;

@Hilt
@MemoEntity("Country")
public class Country<T>
{

    @KeyName("countryCode")
    public final String countryCode = null;

    @KeyName("country")
    public final String countryName = "United Kingdom";

    public Observable<T> test;

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
