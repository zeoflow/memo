package com.zeoflow.memo.common

import com.zeoflow.memo.common.MemoStorage
import java.lang.reflect.InvocationTargetException

public object MemoStorage {
    private const val CLAZZ_PREFIX = "_Injector"
    fun inject(`object`: Any) {
        try {
            val clazz: Class<*> = `object`.javaClass
            val injector = clazz.classLoader.loadClass(clazz.name + CLAZZ_PREFIX)
            val constructor = injector.getConstructor(clazz)
            constructor.newInstance(`object`)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }
}