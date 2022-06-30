package com.zeoflow.memo.demo.entities;

import com.zeoflow.memo.common.Default;
import com.zeoflow.memo.common.DefaultType;
import com.zeoflow.memo.common.EncryptEntity;
import com.zeoflow.memo.common.KeyName;
import com.zeoflow.memo.common.Listener;
import com.zeoflow.memo.common.MemoCompoundFunction;
import com.zeoflow.memo.common.MemoEntity;
import com.zeoflow.memo.common.MemoFunction;
import com.zeoflow.memo.common.Observable;

/**
 * the entity generated will be named UserProfile_MemoEntity
 * it was annotated with @MemoEntity("UserProfile")
 *
 * the entity will be encrypted using the "G15y3aV9M8d" key
 * it was annotated with @EncryptEntity("G15y3aV9M8d")
 */
@MemoEntity("UserProfile")
@EncryptEntity("G15y3aV9M8d")
public class User
{

    /**
     * the default value will be "zeoflow"
     *
     * generated field will be named username
     *
     * this field is observable
     * it was annotated with @Observable
     */
    @KeyName("username")
    @Observable
    @Default(UsernameDefault.class)
    protected final String userUsername = "zeoflow";

    /**
     * generated field name will be login - lowerCamel
     *
     * this field will have its own onChangedListener
     * it was annotated with @Listener
     */
    @Listener
    protected final boolean login = false;

    /* the default value will be 1 */
    @KeyName("views")
    protected final int viewsCount = 1;

    /* the default value will be null */
    @KeyName("userinfo")
    protected PrivateInfo privateInfo;

    /**
     * preference putter function for userUsername.
     *
     * @param userUsername function in
     *
     * @return function out
     */
    @MemoFunction("username")
    public String putUserUsernameFunction(String userUsername)
    {
        return "Hello, " + userUsername;
    }

    /**
     * preference getter function for userUsername.
     *
     * @param userUsername function in
     *
     * @return function out
     */
    @MemoFunction("username")
    public String getUserUsernameFunction(String userUsername)
    {
        return userUsername + "!!!";
    }

    /**
     * preference putter function example for visitCount's auto increment.
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

    /**
     * preference getter compound function for following fields.
     *
     * Params declared inside @MemoCompoundFunction's annotation
     * @param username function in
     * @param views function in
     *
     * @return $username $views
     */
    @MemoCompoundFunction(values = {"username", "views"})
    public String getUserViews(String username, int views)
    {
        return username + " " + views;
    }

    /**
     * preference getter compound function for following fields.
     *
     * Params declared inside @MemoCompoundFunction's annotation
     * @param userinfo function in
     *
     * @return $first_name $last_name
     */
    @MemoCompoundFunction(values = {"userinfo"})
    public String getFullName(PrivateInfo userinfo)
    {
        return userinfo.getFirstName() + " " + userinfo.getLastName();
    }

    /**
     * preference getter compound function for following fields.
     *
     * Params declared inside @MemoCompoundFunction's annotation
     * @param userinfo function in
     * @param views function in
     *
     * @return $first_name $last_name, views count $views
     */
    @MemoCompoundFunction(values = {"userinfo", "views"})
    public String getFullNameAndViews(PrivateInfo userinfo, int views)
    {
        return userinfo.getFirstName() + " " + userinfo.getLastName() + ", views count: " + views;
    }

}

class UsernameDefault implements DefaultType<String> {

    @Override
    public String value() {
        return "zeoflow";
    }
}