package com.snow.weirdchat.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageStorage {

    // Classe interne représentant un message WeirdChat
    public static class WeirdMessage {
        public final String senderPseudo;
        public final String message;
        public final MessageType type;

        public WeirdMessage(String senderPseudo, String message, MessageType type) {
            this.senderPseudo = senderPseudo;
            this.message = message;
            this.type = type;
        }
    }

    // Type de message
    public enum MessageType {
        PUBLIC,        // Message visible par tous
        PRIVATE,       // Message privé
        FOLLOWERS,     // Message envoyé aux followers
        GHOST          // Message envoyé en admin ghost (pseudo libre)
    }

    // Stockage en mémoire des messages (en liste pour exemple)
    private static final List<WeirdMessage> messages = new ArrayList<>();

    // Stocke un message public
    public static void storeMessage(String senderPseudo, String message) {
        messages.add(new WeirdMessage(senderPseudo, message, MessageType.PUBLIC));
        System.out.println("[WeirdChat][Message] " + senderPseudo + ": " + message);
    }

    // Stocke un message privé
    public static void storePrivateMessage(String senderPseudo, String targetPseudo, String message) {
        messages.add(new WeirdMessage(senderPseudo, message, MessageType.PRIVATE));
        System.out.println("[WeirdChat][Private] " + senderPseudo + " -> " + targetPseudo + ": " + message);
    }

    // Stocke un message aux followers
    public static void storeFollowersMessage(String senderPseudo, String message) {
        messages.add(new WeirdMessage(senderPseudo, message, MessageType.FOLLOWERS));
        System.out.println("[WeirdChat][Followers] " + senderPseudo + ": " + message);
    }

    // Stocke un message ghost/admin avec pseudo libre
    public static void storeGhostMessage(String senderPseudo, String message) {
        messages.add(new WeirdMessage(senderPseudo, message, MessageType.GHOST));
        System.out.println("[WeirdChat][Ghost] " + senderPseudo + ": " + message);
    }

    // Récupère une liste non modifiable de tous les messages stockés
    public static List<WeirdMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    // Vide la liste des messages (utile en debug ou reset)
    public static void clearMessages() {
        messages.clear();
    }
}
