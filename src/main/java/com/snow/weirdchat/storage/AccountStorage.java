package com.snow.weirdchat.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AccountStorage {

    private static MinecraftServer server;

    public static void setServer(MinecraftServer srv) {
        System.out.println("[WeirdChat] setServer() called with: " + srv);
        server = srv;
    }

    private static final Map<UUID, String> uuidToPseudo = new HashMap<>();
    private static final Map<String, UUID> pseudoToUuid = new HashMap<>();
    private static final Map<String, Set<String>> followersMap = new HashMap<>();

    // === Chemin du fichier de données ===
    private static File getSaveFile() {
        if (server == null) {
            System.err.println("[WeirdChat] Erreur: MinecraftServer est null dans getSaveFile().");
            return null;
        }

        File worldDir = server.getWorldPath(net.minecraft.world.storage.FolderName.ROOT).toFile();
        File dir = new File(worldDir, "weirdchat");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "weirdchat_accounts.json");
    }

    // === Sauvegarde JSON ===
    public static void saveToDisk() {
        try {
            File file = getSaveFile();
            if (file == null) return;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<String, Object> saveData = new HashMap<>();

            // Convert UUID keys as strings for JSON
            Map<String, String> uuidToPseudoStr = new HashMap<>();
            for (Map.Entry<UUID, String> entry : uuidToPseudo.entrySet()) {
                uuidToPseudoStr.put(entry.getKey().toString(), entry.getValue());
            }

            saveData.put("uuidToPseudo", uuidToPseudoStr);
            saveData.put("followersMap", followersMap);

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(saveData, writer);
            }

            System.out.println("[WeirdChat] Comptes sauvegardés dans " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("[WeirdChat] Erreur lors de la sauvegarde :");
            e.printStackTrace();
        }
    }

    // === Chargement JSON ===
    public static void loadFromDisk() {
        try {
            File file = getSaveFile();
            if (file == null || !file.exists()) return;

            Gson gson = new Gson();
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> saveData = gson.fromJson(reader, type);

                Map<String, String> uuidToPseudoStr = (Map<String, String>) saveData.get("uuidToPseudo");
                Map<String, List<String>> followersRaw = (Map<String, List<String>>) saveData.get("followersMap");

                uuidToPseudo.clear();
                pseudoToUuid.clear();
                followersMap.clear();

                for (Map.Entry<String, String> entry : uuidToPseudoStr.entrySet()) {
                    UUID uuid = UUID.fromString(entry.getKey());
                    String pseudo = entry.getValue();
                    uuidToPseudo.put(uuid, pseudo);
                    pseudoToUuid.put(pseudo.toLowerCase(Locale.ROOT), uuid);
                }

                for (Map.Entry<String, List<String>> entry : followersRaw.entrySet()) {
                    followersMap.put(entry.getKey(), new HashSet<>(entry.getValue()));
                }
            }

            System.out.println("[WeirdChat] Comptes chargés depuis " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("[WeirdChat] Erreur lors du chargement :");
            e.printStackTrace();
        }
    }

    // === Fonctions de base ===

    public static boolean createAccount(ServerPlayerEntity player, String pseudo) {
        if (player == null || pseudo == null) return false;

        UUID uuid = player.getUUID();
        String pseudoLower = pseudo.toLowerCase(Locale.ROOT);

        if (uuidToPseudo.containsKey(uuid) || pseudoToUuid.containsKey(pseudoLower)) return false;

        uuidToPseudo.put(uuid, pseudo);
        pseudoToUuid.put(pseudoLower, uuid);
        followersMap.put(pseudoLower, new HashSet<>());
        return true;
    }

    public static boolean hasAccount(UUID uuid) {
        return uuidToPseudo.containsKey(uuid);
    }

    public static boolean hasAccountForPlayer(ServerPlayerEntity player) {
        if (player == null) return false;
        return hasAccount(player.getUUID());
    }

    public static boolean isPseudoUsed(String pseudo) {
        if (pseudo == null) return false;
        return pseudoToUuid.containsKey(pseudo.toLowerCase(Locale.ROOT));
    }

    public static String getPseudo(UUID uuid) {
        return uuidToPseudo.get(uuid);
    }

    public static String getPseudo(ServerPlayerEntity player) {
        if (player == null) return null;
        return getPseudo(player.getUUID());
    }

    public static UUID getUUIDByPseudo(String pseudo) {
        if (pseudo == null) return null;
        return pseudoToUuid.get(pseudo.toLowerCase(Locale.ROOT));
    }

    public static boolean addFollower(String accountPseudo, String followerPseudo) {
        String lowerAccountPseudo = accountPseudo.toLowerCase(Locale.ROOT);
        String lowerFollowerPseudo = followerPseudo.toLowerCase(Locale.ROOT);

        followersMap.putIfAbsent(lowerAccountPseudo, new HashSet<>());
        return followersMap.get(lowerAccountPseudo).add(lowerFollowerPseudo);
    }

    public static boolean removeFollower(String accountPseudo, String followerPseudo) {
        Set<String> followers = followersMap.get(accountPseudo.toLowerCase(Locale.ROOT));
        if (followers == null) return false;
        return followers.remove(followerPseudo.toLowerCase(Locale.ROOT));
    }

    public static Set<String> getFollowers(String accountPseudo) {
        return Collections.unmodifiableSet(
                followersMap.getOrDefault(accountPseudo.toLowerCase(Locale.ROOT), Collections.emptySet())
        );
    }

    public static boolean changePseudo(String oldPseudo, String newPseudo) {
        if (oldPseudo == null || newPseudo == null) return false;

        String oldPseudoLower = oldPseudo.toLowerCase(Locale.ROOT);
        String newPseudoLower = newPseudo.toLowerCase(Locale.ROOT);

        if (!isPseudoUsed(oldPseudo) || isPseudoUsed(newPseudo)) return false;

        UUID uuid = getUUIDByPseudo(oldPseudo);
        if (uuid == null) return false;

        uuidToPseudo.put(uuid, newPseudo);
        pseudoToUuid.remove(oldPseudoLower);
        pseudoToUuid.put(newPseudoLower, uuid);

        Set<String> followers = followersMap.remove(oldPseudoLower);
        followersMap.put(newPseudoLower, followers == null ? new HashSet<>() : followers);

        return true;
    }

    public static List<UUID> getFollowerUUIDs(String accountPseudo) {
        Set<String> followerPseudos = followersMap.getOrDefault(accountPseudo.toLowerCase(Locale.ROOT), Collections.emptySet());
        List<UUID> result = new ArrayList<>();
        for (String pseudo : followerPseudos) {
            UUID uuid = getUUIDByPseudo(pseudo);
            if (uuid != null) result.add(uuid);
        }
        return result;
    }

    public static ServerPlayerEntity getPlayerByPseudo(String pseudo) {
        if (server == null || pseudo == null) return null;

        String pseudoLower = pseudo.toLowerCase(Locale.ROOT);

        for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
            String playerPseudo = getPseudo(player);
            if (playerPseudo != null && playerPseudo.toLowerCase(Locale.ROOT).equals(pseudoLower)) {
                return player;
            }
        }

        return null;
    }
}
