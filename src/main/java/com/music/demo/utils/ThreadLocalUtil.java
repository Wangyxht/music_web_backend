package com.music.demo.utils;

public class ThreadLocalUtil {

    // 创建一个泛型 ThreadLocal 用于存储线程上下文数据
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程的变量
     * @param value 要设置的值
     */
    public static void set(Object value) {
        THREAD_LOCAL.set(value);
    }

    /**
     * 获取当前线程的变量
     * @return 当前线程保存的变量值
     */
    public static Object get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 移除当前线程的变量，防止内存泄漏
     */
    public static void remove() {
        THREAD_LOCAL.remove();
    }
}