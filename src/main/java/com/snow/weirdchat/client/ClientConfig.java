package com.snow.weirdchat.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientConfig {

    // Map qui stocke si WeirdChat est activé pour chaque joueur (clé UUID)
    private static final Map<UUID, Boolean> showWeirdChatMap = new HashMap<>();

    /**
     * Définit l'état d'affichage WeirdChat pour un joueur.
     * @param playerUUID UUID du joueur
     * @param enabled true pour activer l'affichage, false pour désactiver
     */
    public static void setShowWeirdChat(UUID playerUUID, boolean enabled) {
        showWeirdChatMap.put(playerUUID, enabled);
        System.out.println("[WeirdChat] Set chat visible for " + playerUUID + " = " + enabled);
    }

    /**
     * Récupère l'état d'affichage WeirdChat pour un joueur.
     * @param playerUUID UUID du joueur
     * @return true si activé ou si aucune info trouvée (valeur par défaut)
     */
    public static boolean getShowWeirdChat(UUID playerUUID) {
        return showWeirdChatMap.getOrDefault(playerUUID, true);
    }

    /**
     * Bascule l'état WeirdChat pour un joueur.
     * @param playerUUID UUID du joueur
     * @return Nouvelle valeur après bascule
     */
    public static boolean toggleWeirdChat(UUID playerUUID) {
        boolean current = getShowWeirdChat(playerUUID);
        boolean updated = !current;
        setShowWeirdChat(playerUUID, updated);
        return updated;
    }
}
