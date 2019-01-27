package com.mpr.main.java.server.event;

import java.util.Map;

public class SupportedEvents {
    public static final Map<String, EventType> SUPPORTED_EVENTS = createMap();

    private static Map<String, EventType> createMap() {
        return Map.of("Course viewed", EventType.ONE_USER_ONE_ITEM,
                "Course module viewed", EventType.ONE_USER_ONE_ITEM,
                "User enrolled in course", EventType.TWO_USERS_ONE_ITEM,
                "User unenrolled from course", EventType.TWO_USERS_ONE_ITEM,
                "Course module created", EventType.ONE_USER_ONE_ITEM);
    }
}
