package com.zeoflow.memo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

public class MemoApplication
{

    @GuardedBy("LOCK")
    static final Map<String, MemoApplication> INSTANCES = new ArrayMap<>();
    private static final @NonNull
    String DEFAULT_APP_NAME = "[DEFAULT]";
    private static final Object LOCK = new Object();
    private final static AtomicBoolean deleted = new AtomicBoolean();
    private final Context applicationContext;
    private final String name;

    @SuppressLint("RestrictedApi")
    protected MemoApplication(Context applicationContext, String name)
    {
        this.applicationContext = requireNonNull(applicationContext);
        this.name = name;
    }

    @NonNull
    public static MemoApplication getInstance()
    {
        synchronized (LOCK)
        {
            MemoApplication defaultApp = INSTANCES.get(DEFAULT_APP_NAME);
            if (defaultApp == null)
            {
                throw new IllegalStateException(
                        "Default MemoApplication is not initialized in this "
                                + "process "
                                + ". Make sure to call "
                                + "MemoApplication.initializeApp(Context) first.");
            }
            return defaultApp;
        }
    }

    @NonNull
    public static Context getContext()
    {
        synchronized (LOCK)
        {
            MemoApplication defaultApp = INSTANCES.get(DEFAULT_APP_NAME);
            if (defaultApp == null)
            {
                throw new IllegalStateException(
                        "Default MemoApplication is not initialized in this "
                                + "process "
                                + ". Make sure to call "
                                + "MemoApplication.initializeApp(Context) first.");
            }
            checkNotDeleted();
            return defaultApp.applicationContext;
        }
    }

    @NonNull
    public static MemoApplication getInstance(@NonNull String name)
    {
        synchronized (LOCK)
        {
            MemoApplication zeoflowApp = INSTANCES.get(normalize(name));
            if (zeoflowApp != null)
            {
                return zeoflowApp;
            }

            List<String> availableAppNames = getAllAppNames();
            String availableAppNamesMessage;
            if (availableAppNames.isEmpty())
            {
                availableAppNamesMessage = "";
            } else
            {
                availableAppNamesMessage =
                        "Available app names: " + TextUtils.join(", ", availableAppNames);
            }
            String errorMessage =
                    String.format(
                            "MemoApplication with name %s doesn't exist. %s", name, availableAppNamesMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    public static MemoApplication initializeApp(@NonNull Context context)
    {
        String normalizedName = normalize(DEFAULT_APP_NAME);
        Context applicationContext;
        if (context.getApplicationContext() == null)
        {
            applicationContext = context;
        } else
        {
            applicationContext = context.getApplicationContext();
        }

        MemoApplication zeoflowApp;
        synchronized (LOCK)
        {
            INSTANCES.remove(normalizedName);
            requireNonNull(applicationContext, "Application context cannot be null.");
            zeoflowApp = new MemoApplication(applicationContext, normalizedName);
            INSTANCES.put(normalizedName, zeoflowApp);
        }
        INSTANCES.put(normalizedName, zeoflowApp);
        return zeoflowApp;
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    public static MemoApplication initializeApp(@NonNull Context context, @NonNull String name)
    {
        String normalizedName = normalize(name);
        Context applicationContext;
        if (context.getApplicationContext() == null)
        {
            applicationContext = context;
        } else
        {
            applicationContext = context.getApplicationContext();
        }

        MemoApplication zeoflowApp;
        synchronized (LOCK)
        {
            INSTANCES.remove(normalizedName);
            requireNonNull(applicationContext, "Application context cannot be null.");
            zeoflowApp = new MemoApplication(applicationContext, normalizedName);
            INSTANCES.put(normalizedName, zeoflowApp);
        }

        zeoflowApp.initializeAllApis();
        return zeoflowApp;
    }

    @SuppressLint("RestrictedApi")
    private static void checkNotDeleted()
    {
        if (!deleted.get())
        {
            // deleted
        }
    }

    private static List<String> getAllAppNames()
    {
        List<String> allAppNames = new ArrayList<>();
        synchronized (LOCK)
        {
            for (MemoApplication app : INSTANCES.values())
            {
                allAppNames.add(app.getName());
            }
        }
        Collections.sort(allAppNames);
        return allAppNames;
    }

    private static String normalize(@NonNull String name)
    {
        return name.trim();
    }

    @NonNull
    public Context getApplicationContext()
    {
        checkNotDeleted();
        return applicationContext;
    }

    @NonNull
    public String getName()
    {
        checkNotDeleted();
        return name;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MemoApplication))
        {
            return false;
        }
        return name.equals(((MemoApplication) o).getName());
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    public void delete()
    {
        boolean valueChanged = deleted.compareAndSet(false /* expected */, true);
        if (!valueChanged)
        {
            return;
        }

        synchronized (LOCK)
        {
            INSTANCES.remove(this.name);
        }
    }

    private void initializeAllApis()
    {
        boolean inDirectBoot = !isUserUnlocked(applicationContext);
        if (inDirectBoot)
        {
            // Ensure that all APIs are initialized once the user unlocks the phone.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                UserUnlockReceiver.ensureReceiverRegistered(applicationContext);
            }
        }
    }

    public boolean isUserUnlocked(@NonNull Context context)
    {
        if (Build.VERSION.SDK_INT >= 24)
        {
            return context.getSystemService(UserManager.class).isUserUnlocked();
        } else
        {
            return true;
        }
    }

    private static class UserUnlockReceiver extends BroadcastReceiver
    {

        private static final AtomicReference<UserUnlockReceiver> INSTANCE = new AtomicReference<>();
        private final Context applicationContext;

        public UserUnlockReceiver(Context applicationContext)
        {
            this.applicationContext = applicationContext;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private static void ensureReceiverRegistered(Context applicationContext)
        {
            if (INSTANCE.get() == null)
            {
                UserUnlockReceiver receiver = new UserUnlockReceiver(applicationContext);
                if (INSTANCE.compareAndSet(null /* expected */, receiver))
                {
                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_USER_UNLOCKED);
                    applicationContext.registerReceiver(receiver, intentFilter);
                }
            }
        }

        @Override
        public void onReceive(Context context, Intent intent)
        {
            // API initialization is idempotent.
            synchronized (LOCK)
            {
                for (MemoApplication app : INSTANCES.values())
                {
                    app.initializeAllApis();
                }
            }
            unregister();
        }

        public void unregister()
        {
            applicationContext.unregisterReceiver(this);
        }

    }

}
