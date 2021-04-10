package com.zeoflow.memo;

public interface MemoFacade
{

    <T> boolean put(String key, T value);

    <T> T get(String key);

    <T> T get(String key, T defaultValue);

    <T> String encrypt(T value);

    <T> T decrypt(String value);

    long count();

    boolean deleteAll();

    boolean delete(String key);

    boolean contains(String key);

    boolean isBuilt();

    void destroy();

    class EmptyMemoFacade implements MemoFacade
    {

        @Override
        public <T> boolean put(String key, T value)
        {
            throwValidation();
            return false;
        }

        @Override
        public <T> T get(String key)
        {
            throwValidation();
            return null;
        }

        @Override
        public <T> T get(String key, T defaultValue)
        {
            throwValidation();
            return null;
        }

        @Override
        public <T> String encrypt(T value)
        {
            throwValidation();
            return null;
        }

        @Override
        public <T> T decrypt(String value)
        {
            throwValidation();
            return null;
        }

        @Override
        public long count()
        {
            throwValidation();
            return 0;
        }

        @Override
        public boolean deleteAll()
        {
            throwValidation();
            return false;
        }

        @Override
        public boolean delete(String key)
        {
            throwValidation();
            return false;
        }

        @Override
        public boolean contains(String key)
        {
            throwValidation();
            return false;
        }

        @Override
        public boolean isBuilt()
        {
            return false;
        }

        @Override
        public void destroy()
        {
            throwValidation();
        }

        private void throwValidation()
        {
            throw new IllegalStateException("Memo is not built. " +
                    "Please call build() and wait until the initialisation finishes.");
        }

    }

}
