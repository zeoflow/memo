package com.zeoflow.memo;

import android.text.TextUtils;

import com.zeoflow.zson.JsonSyntaxException;
import com.zeoflow.zson.Zson;

import java.lang.reflect.Type;

public final class ZsonParser implements Parser
{

    private final Zson zson;

    public ZsonParser(Zson zson)
    {
        this.zson = zson;
    }

    @Override
    public <T> T fromJson(String content, Type type) throws JsonSyntaxException
    {
        if (TextUtils.isEmpty(content))
        {
            return null;
        }
        return zson.fromJson(content, type);
    }

    @Override
    public String toJson(Object body)
    {
        return zson.toJson(body);
    }

}
