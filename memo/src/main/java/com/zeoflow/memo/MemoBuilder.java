package com.zeoflow.memo;

import android.content.Context;

import com.zeoflow.zson.Zson;

import static com.zeoflow.memo.MemoApplication.getContext;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class MemoBuilder
{

    /**
     * NEVER ever change STORAGE_TAG_DO_NOT_CHANGE and TAG_INFO.
     * It will break backward compatibility in terms of keeping previous data
     */
    private static final String STORAGE_TAG_DO_NOT_CHANGE = "MemoStorage";

    private final Context context;
    private IStorage cryptoStorage;
    private IConverter converter;
    private IParser parser;
    private Encryption encryption;
    private ISerializer serializer;
    private ILogInterceptor logInterceptor;

    public MemoBuilder()
    {
        this(getContext());
    }
    public MemoBuilder(Context context)
    {
        MemoUtils.checkNull("Context", context);

        this.context = context.getApplicationContext();
    }
    ILogInterceptor getLogInterceptor()
    {
        if (logInterceptor == null)
        {
            logInterceptor = message ->
            {
                //empty implementation
            };
        }
        return logInterceptor;
    }
    public MemoBuilder setLogInterceptor(ILogInterceptor logInterceptor)
    {
        this.logInterceptor = logInterceptor;
        return this;
    }
    IStorage getStorage()
    {
        if (cryptoStorage == null)
        {
            cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
        }
        return cryptoStorage;
    }
    public MemoBuilder setStorage(IStorage storage)
    {
        this.cryptoStorage = storage;
        return this;
    }
    IConverter getConverter()
    {
        if (converter == null)
        {
            converter = new MemoConverter(getParser());
        }
        return converter;
    }
    public MemoBuilder setConverter(IConverter converter)
    {
        this.converter = converter;
        return this;
    }
    IParser getParser()
    {
        if (parser == null)
        {
            parser = new ZsonParser(new Zson());
        }
        return parser;
    }
    public MemoBuilder setParser(IParser parser)
    {
        this.parser = parser;
        return this;
    }
    Encryption getEncryption()
    {
        if (encryption == null)
        {
            encryption = new ConcealEncryption("sfdgfgsgs");
            if (!encryption.init())
            {
                encryption = new NoEncryption();
            }
        }
        return encryption;
    }
    public MemoBuilder setEncryption(Encryption encryption)
    {
        this.encryption = encryption;
        return this;
    }
    ISerializer getSerializer()
    {
        if (serializer == null)
        {
            serializer = new MemoSerializer(getLogInterceptor());
        }
        return serializer;
    }
    public MemoBuilder setSerializer(ISerializer serializer)
    {
        this.serializer = serializer;
        return this;
    }
    public void build()
    {
        Memo.build(this);
    }

}
