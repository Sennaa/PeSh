/*
 * Decompiled with CFR 0_118.
 */
package org.sqlite.date;

public class ExceptionUtils {
    public static <R> R rethrow(Throwable throwable) {
        return ExceptionUtils.typeErasure(throwable);
    }

    private static <R, T extends Throwable> R typeErasure(Throwable throwable) throws Throwable {
        throw throwable;
    }
}

