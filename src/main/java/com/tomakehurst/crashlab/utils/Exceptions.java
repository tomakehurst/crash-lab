package com.tomakehurst.crashlab.utils;


public class Exceptions {

    public static <T> T throwUnchecked(final Throwable ex, final Class<T> returnType) {
        Exceptions.<RuntimeException>throwsUnchecked(ex);
        throw new AssertionError("This code should be unreachable. Something went terribly wrong here!");
    }

    public static void throwUnchecked(final Throwable ex) {
        throwUnchecked(ex, null);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwsUnchecked(Throwable toThrow) throws T {
        throw (T) toThrow;
    }
}
