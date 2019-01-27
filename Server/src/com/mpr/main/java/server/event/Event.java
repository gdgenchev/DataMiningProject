package com.mpr.main.java.server.event;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Event {
    private String context;
    private String name;
    private String userIP;
    static Map<String, String> eventContextToId = new HashMap<>();
    static Map<String, String> userIpToUserId = new HashMap<>();

    Event() {
        context = null;
        name = null;
        userIP = null;
    }

    Event(String context, String name, String userIP) {
        this.context = context;
        this.name = name;
        this.userIP = userIP;
    }

    public abstract void writeToFile(String folderName, PrintWriter pw);

    /**
     * This function decodes the output of the algorithm
     *
     * @param algoLine a line from the output file of the algorithm
     * @param eventName the event name
     * @return the string we want to send to the client
     */
    public abstract String decode(String algoLine, String eventName);

    /**
     * This function parses the ids from the description
     *
     * @param description The raw description
     * @return the parsed ids from the description
     */
    List<String> parseDescription(String description) {
        Pattern pattern = Pattern.compile("'(\\d+?)'", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(description);
        List<String> ids = new ArrayList<>();
        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        return ids;
    }
}
