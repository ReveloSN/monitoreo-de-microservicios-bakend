package com.revelosn.proxymonitoring.util;

public final class StackTraceUtils {

    private StackTraceUtils() {
    }

    public static String summarize(Throwable throwable) {
        StringBuilder builder = new StringBuilder(throwable.getClass().getSimpleName())
                .append(": ")
                .append(throwable.getMessage());

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        int limit = Math.min(stackTrace.length, 5);
        for (int index = 0; index < limit; index++) {
            builder.append(System.lineSeparator())
                    .append("at ")
                    .append(stackTrace[index]);
        }
        return builder.toString();
    }
}
