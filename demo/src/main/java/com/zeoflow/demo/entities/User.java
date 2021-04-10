package com.zeoflow.demo.entities;

import com.zeoflow.memo.annotation.EncryptEntity;
import com.zeoflow.memo.annotation.KeyName;
import com.zeoflow.memo.annotation.Listener;
import com.zeoflow.memo.annotation.MemoCompoundFunction;
import com.zeoflow.memo.annotation.MemoEntity;
import com.zeoflow.memo.annotation.MemoFunction;
import com.zeoflow.memo.annotation.Observable;

@MemoEntity("UserProfile")
@EncryptEntity("G15y3aV9M8dHbmV4vC9EZmDxRgAoWd")
public class User
{

    @KeyName("username")
    @Observable
    protected final String userNickName = "zeoflow";

    /**
     * key value will be 'Login'. (login's camel uppercase)
     */
    @Listener
    protected final boolean login = false;

    @KeyName("views")
    protected final int viewsCount = 1;

    @KeyName("userinfo")
    protected PrivateInfo privateInfo;

    /**
     * preference putter function about userNickName.
     *
     * @param nickname function in
     *
     * @return function out
     */
    @MemoFunction("username")
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
    @MemoFunction("username")
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
    @MemoFunction("views")
    public int putVisitCountFunction(int count)
    {
        return ++count;
    }

    @MemoCompoundFunction(values = {"username", "views"})
    public String getUserFullName(String username, int views)
    {
        return username + " " + views;
    }

    @MemoCompoundFunction(values = {"userinfo"})
    public String getFullName(PrivateInfo userinfo)
    {
        return userinfo.getFirstName() + " " + userinfo.getLastName();
    }

    @MemoCompoundFunction(values = {"userinfo", "views"})
    public String getFullNameAndViews(PrivateInfo userinfo, int views)
    {
        return userinfo.getFirstName() + " " + userinfo.getLastName() + ", views count: " + views;
    }

}
