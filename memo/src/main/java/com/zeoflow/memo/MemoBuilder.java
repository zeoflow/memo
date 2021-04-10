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
    private Storage cryptoStorage;
    private Converter converter;
    private Parser parser;
    private Encryption encryption;
    private Serializer serializer;
    private LogInterceptor logInterceptor;

    public MemoBuilder()
    {
        this(getContext());
    }
    public MemoBuilder(Context context)
    {
        MemoUtils.checkNull("Context", context);

        this.context = context.getApplicationContext();
    }
    LogInterceptor getLogInterceptor()
    {
        if (logInterceptor == null)
        {
            //empty implementation
            logInterceptor = System.out::println;
        }
        return logInterceptor;
    }
    public MemoBuilder setLogInterceptor(LogInterceptor logInterceptor)
    {
        this.logInterceptor = logInterceptor;
        return this;
    }
    Storage getStorage()
    {
        if (cryptoStorage == null)
        {
            cryptoStorage = new SharedPreferencesStorage(context, STORAGE_TAG_DO_NOT_CHANGE);
        }
        return cryptoStorage;
    }
    public MemoBuilder setStorage(Storage storage)
    {
        this.cryptoStorage = storage;
        return this;
    }
    Converter getConverter()
    {
        if (converter == null)
        {
            converter = new MemoConverter(getParser());
        }
        return converter;
    }
    public MemoBuilder setConverter(Converter converter)
    {
        this.converter = converter;
        return this;
    }
    Parser getParser()
    {
        if (parser == null)
        {
            parser = new ZsonParser(new Zson());
        }
        return parser;
    }
    public MemoBuilder setParser(Parser parser)
    {
        this.parser = parser;
        return this;
    }
    Encryption getEncryption()
    {
        if (encryption == null)
        {
            encryption = new ConcealEncryption(context, "sfdgfgsgs");
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
    Serializer getSerializer()
    {
        if (serializer == null)
        {
            serializer = new MemoSerializer(getLogInterceptor());
        }
        return serializer;
    }
    public MemoBuilder setSerializer(Serializer serializer)
    {
        this.serializer = serializer;
        return this;
    }
    public void build()
    {
        Memo.build(this);
    }

}
