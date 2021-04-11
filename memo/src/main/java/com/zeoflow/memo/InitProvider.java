package com.zeoflow.memo;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

public class InitProvider extends ContentProvider
{

    /**
     * Should match the {@link InitProvider} authority if $androidId is empty.
     */
    @VisibleForTesting
    static final String EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY = "com.zeoflow.memo";

    /**
     * Check that the content provider's authority does not use firebase-common's package name. If it
     * does, crash in order to alert the developer of the problem before they distribute the app.
     */
    private static void checkContentProviderAuthority(@NonNull ProviderInfo info)
    {
        if (info == null)
        {
            return;
        }
        if (EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY.equals(info.authority))
        {
            throw new IllegalStateException(
                    "Incorrect provider authority in manifest. Most likely due to a missing "
                            + "applicationId variable in application's build.gradle.");
        }
    }

    @Override
    public void attachInfo(@NonNull Context context, @NonNull ProviderInfo info)
    {
        // super.attachInfo calls onCreate. Fail as early as possible.
        checkContentProviderAuthority(info);
        super.attachInfo(context, info);
    }

    /**
     * Called before {@link Application#onCreate()}.
     */
    @Override
    public boolean onCreate()
    {
        assert getContext() != null;
        if (MemoApplication.initializeApp(getContext()) == null)
        {
            throw new IllegalStateException("MemoApplication initialization unsuccessful");
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder)
    {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        return null;
    }

    @Override
    public int delete(
            @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(
            @NonNull Uri uri,
            @Nullable ContentValues values,
            @Nullable String selection,
            @Nullable String[] selectionArgs)
    {
        return 0;
    }

}
