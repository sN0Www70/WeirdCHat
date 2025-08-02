package com.snow.weirdchat.storage;

import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class ToggleStorage {

    // Stocke l'état (activé/désactivé) de WeirdChat pour chaque joueur
    private static final Map<UUID, Boolean> toggleMap = new HashMap<>();

    /**
     * Active ou désactive l'affichage WeirdChat pour un joueur
     */
    public static void setEnabled(ServerPlayerEntity player, boolean enabled) {
        if (player == null) return;
        toggleMap.put(player.getUUID(), enabled);
    }

    /**
     * Vérifie si WeirdChat est activé pour un joueur (par défaut : true)
     */
    public static boolean isEnabled(ServerPlayerEntity player) {
        if (player == null) return true;
        return toggleMap.getOrDefault(player.getUUID(), true);
    }

    /**
     * Vérifie si WeirdChat est activé pour un joueur via UUID
     */
    public static boolean isEnabled(UUID uuid) {
        if (uuid == null) return true;
        return toggleMap.getOrDefault(uuid, true);
    }
}
