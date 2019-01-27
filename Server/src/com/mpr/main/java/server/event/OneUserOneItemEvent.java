package com.mpr.main.java.server.event;

import java.io.*;
import java.util.List;

public class OneUserOneItemEvent extends Event {
    private String userId;
    private String contextId;

    public OneUserOneItemEvent(String context, String name, String userIP, String description) {
        super(context, name, userIP);
        fillIdsWithParsedDescription(description);
        eventContextToId.put(contextId, context);
        userIpToUserId.put(userId, userIP);
    }

    public OneUserOneItemEvent(){
        super();
    }

    private void fillIdsWithParsedDescription(String description){
        List<String> ids = parseDescription(description);
        this.userId = ids.get(0);
        this.contextId = ids.get(1);
    }

    @Override
    public void writeToFile(String folderName, PrintWriter pw) {
        pw.println(userId + " " + contextId);
    }

    //algoLine: 1 3 #SUPP 421
    //1 #SUPP 421
    @Override
    public String decode(String algoLine, String eventName) {
        String[] parsed = algoLine.split("\\s+");
        if (parsed.length == 3) {
            String value;
            if ((value = userIpToUserId.get(parsed[0])) != null) {
                return "User: " + value + " performed actions " + parsed[2] + " times.";
            } else {
                value = eventContextToId.get(parsed[0]);
                return parsed[2] + " actions were performed on context " + value + ".";
            }
        } else {
            String times = parsed[3];
            String user;
            String context;
            String value;
            if ((value = eventContextToId.get(parsed[0])) != null) {
                context = value;
                if ((value = userIpToUserId.get(parsed[1])) != null) {
                    user = value;
                } else {
                    user = parsed[1];
                }
            } else {
                context = eventContextToId.get(parsed[1]);
                if ((value = userIpToUserId.get(parsed[0])) != null) {
                    user = value;
                } else {
                    user = parsed[0];
                }
            }
            return "User: " + user + " performed action " + eventName + " on " + context + " " + times + " times.";
        }
    }
}

