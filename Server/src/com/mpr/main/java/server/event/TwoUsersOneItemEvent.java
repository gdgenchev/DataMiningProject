package com.mpr.main.java.server.event;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TwoUsersOneItemEvent extends Event {
    private String user1Id;
    private String user2Id;
    private String contextId;

    public TwoUsersOneItemEvent() {
        super();
    }
    public TwoUsersOneItemEvent(String context, String name, String userIP, String description) {
        super(context, name, userIP);
        fillIdsWithParsedDescription(description);
        eventContextToId.put(contextId, context);
        userIpToUserId.put(user1Id, userIP);
    }

    private void fillIdsWithParsedDescription(String description) {
        List<String> ids = parseDescription(description);
        this.user1Id = ids.get(0);
        this.user2Id = ids.get(1);
        this.contextId = ids.get(2);
    }

    @Override
    public void writeToFile(String folderName, PrintWriter pw) {
        pw.println(user1Id + " " + user2Id + " " + contextId);
    }

    @Override
    public String decode(String algoLine, String eventName) {
        String[] parsed = algoLine.split("\\s+");
        if (parsed.length == 5) {
            String times = parsed[4];
            List<String> users = new ArrayList<>();
            String context = "";
            for (int i = 0; i <= 2; i++) {
                String cur;
                if ((cur = userIpToUserId.get(parsed[i])) != null) {
                    users.add(cur);
                } else if ((cur = eventContextToId.get(parsed[i])) != null) {
                    context = cur;
                } else {
                    users.add(parsed[i]);
                }
            }

            return "Users: " + users.get(0) + ", " + users.get(1) + " performed action "
                        + eventName + " on " + context + " " + times + " times.";
        }
        return null;
    }
}
