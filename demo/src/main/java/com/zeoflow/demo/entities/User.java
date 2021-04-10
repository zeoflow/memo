package com.zeoflow.demo.entities;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zeoflow.parcelled.Default;
import com.zeoflow.parcelled.Parcelled;
import com.zeoflow.parcelled.ParcelledVersion;

import org.jetbrains.annotations.NotNull;

@Parcelled(version = 1)
public abstract class User implements Parcelable
{

    @Default(code = "0")
    public int id;

    @Nullable
    @Default(code = "null")
    public String firstName;

    @ParcelledVersion(after = 1, before = 2)
    @Nullable
    public String lastName;
    public static User create(
            int id,
            @NonNull String firstName,
            @NonNull String lastName
    )
    {
        return new Parcelled_User(id, firstName, lastName);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (!(other instanceof User))
        {
            return false;
        }

        User o = (User) other;

        return id == o.id;
    }

    @NotNull
    @Override
    public String toString()
    {
        return "User{$ID}" + id;
    }

}
