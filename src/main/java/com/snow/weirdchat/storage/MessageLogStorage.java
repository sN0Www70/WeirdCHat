package com.snow.weirdchat.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessageLogStorage {

    public static class Message {
        public final String sender;
        public final String content;
        public final String timestamp;

        public Message(String sender, String content) {
            this.sender = sender;
            this.content = content;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + sender + ": " + content;
        }
    }

    private static final List<Message> messageLog = new ArrayList<>();

    public static void logMessage(String sender, String content) {
        messageLog.add(new Message(sender, content));
    }

    public static List<Message> getAllMessages() {
        return new ArrayList<>(messageLog);
    }

    public static void clearLog() {
        messageLog.clear();
    }

    public static int getTotalMessageCount() {
        return messageLog.size();
    }
}
