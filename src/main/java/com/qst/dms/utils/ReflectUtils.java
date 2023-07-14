package com.qst.dms.utils;

public class ReflectUtils {
    public static boolean isPrimitive(Object o) {
        return o instanceof Integer || o instanceof Double ||
                o instanceof Boolean || o instanceof Character ||
                o instanceof Byte || o instanceof Short ||
                o instanceof Long || o instanceof Float ||
                o instanceof String;
    }
}
