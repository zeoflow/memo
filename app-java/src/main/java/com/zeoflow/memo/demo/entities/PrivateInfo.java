
package com.zeoflow.memo.demo.entities;

import androidx.annotation.NonNull;

public class PrivateInfo
{

    private final String firstName;
    private final String lastName;

    public PrivateInfo(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public String getLastName()
    {
        return lastName;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "firstName=" + firstName + ", " + "lastName=" + lastName;
    }

}
