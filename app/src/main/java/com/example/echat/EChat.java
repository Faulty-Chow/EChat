package com.example.echat;

import java.util.Date;
import java.util.List;

public class EChat {
    public static class User {
        public final String userId;
        public String nickName;
        public User(String userId, String nickName) {
            this.userId = userId;
            this.nickName = nickName;
        }
    }

    public static class Session{
        public final String sessionId;
        public String sessionName;
        public String ownerId;
        public List<String> memberIds;
        public boolean newMsg;

        public Session(String sessionId, String sessionName,String owner, List<String> members) {
            this.sessionId = sessionId;
            this.sessionName = sessionName;
            this.ownerId = owner;
            this.memberIds = members;
            newMsg = false;
        }
    }

    public static class Message{
        public final String fromId;
        public final String toId;
        public final String content;
        public final Date time;

        public Message(String fromId, String toId, String content, Date time) {
            this.fromId = fromId;
            this.toId = toId;
            this.content = content;
            this.time = time;
        }
    }

    public static class Circles{
        public final String circleId;
        public final String ownerId;

        public Circles(String circleId, String circleName,String owner, List<String> members) {
            this.circleId = circleId;
            this.ownerId = owner;
        }
    }
}
