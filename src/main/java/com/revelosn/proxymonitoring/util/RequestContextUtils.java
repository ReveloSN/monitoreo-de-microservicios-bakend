package com.revelosn.proxymonitoring.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class RequestContextUtils {

    private static final String REQUEST_ID_ATTRIBUTE = "requestId";

    private RequestContextUtils() {
    }

    public static String currentPath() {
        ServletRequestAttributes attributes = currentAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.getRequest().getRequestURI();
    }

    public static void storeRequestId(String requestId) {
        ServletRequestAttributes attributes = currentAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
    }

    public static String currentRequestId() {
        ServletRequestAttributes attributes = currentAttributes();
        if (attributes == null) {
            return null;
        }
        Object value = attributes.getAttribute(REQUEST_ID_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return value == null ? null : String.valueOf(value);
    }

    private static ServletRequestAttributes currentAttributes() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes;
        }
        return null;
    }
}

