package com.snow.weirdchat.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FollowStorage {

    // Map<pseudoCibleMinuscules, Set<pseudoFollowerMinuscules>>
    private static final Map<String, Set<String>> followersMap = new HashMap<>();
    private static MinecraftServer server;

    public static void setServer(MinecraftServer srv) {
        server = srv;
    }

    public static void follow(String follower, String target) {
        String followerLower = follower.toLowerCase(Locale.ROOT);
        String targetLower = target.toLowerCase(Locale.ROOT);
        followersMap.computeIfAbsent(targetLower, k -> new HashSet<>()).add(followerLower);
    }

    public static void unfollow(String follower, String target) {
        String followerLower = follower.toLowerCase(Locale.ROOT);
        String targetLower = target.toLowerCase(Locale.ROOT);

        Set<String> followers = followersMap.get(targetLower);
        if (followers != null) {
            followers.remove(followerLower);
            if (followers.isEmpty()) {
                followersMap.remove(targetLower);
            }
        }
    }

    public static boolean isFollowing(String follower, String target) {
        String followerLower = follower.toLowerCase(Locale.ROOT);
        String targetLower = target.toLowerCase(Locale.ROOT);

        Set<String> followers = followersMap.get(targetLower);
        return followers != null && followers.contains(followerLower);
    }

    public static Set<String> getFollowers(String account) {
        return Collections.unmodifiableSet(
                followersMap.getOrDefault(account.toLowerCase(Locale.ROOT), Collections.emptySet())
        );
    }

    public static void sendToFollowers(ServerPlayerEntity sender, String message, String senderPseudo) {
        if (sender == null || message == null || senderPseudo == null) return;

        MinecraftServer server = sender.getServer();
        if (server == null) return;

        Set<String> followers = getFollowers(senderPseudo);

        for (String followerPseudoLower : followers) {
            ServerPlayerEntity followerPlayer = server.getPlayerList().getPlayers().stream()
                    .filter(p -> p.getName().getString().equalsIgnoreCase(followerPseudoLower))
                    .findFirst()
                    .orElse(null);

            if (followerPlayer != null && followerPlayer.isAlive()) {
                followerPlayer.sendMessage(new StringTextComponent("§7[" + senderPseudo + " → vous] " + message), followerPlayer.getUUID());
            }
        }
    }

    private static File getSaveFile() {
        if (server == null) return null;
        File dir = new File(server.getServerDirectory(), "weirdchat");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "followers.json");
    }

    public static void saveToDisk() {
        try {
            File file = getSaveFile();
            if (file == null) return;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(followersMap, writer);
            }

            System.out.println("[WeirdChat] Followers sauvegardés dans " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[WeirdChat] Erreur lors de la sauvegarde des followers :");
            e.printStackTrace();
        }
    }

    public static void loadFromDisk() {
        try {
            File file = getSaveFile();
            if (file == null || !file.exists()) return;

            Gson gson = new Gson();
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Set<String>>>() {}.getType();
                Map<String, Set<String>> loaded = gson.fromJson(reader, type);

                followersMap.clear();
                followersMap.putAll(loaded);
            }

            System.out.println("[WeirdChat] Followers chargés depuis " + file.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[WeirdChat] Erreur lors du chargement des followers :");
            e.printStackTrace();
        }
    }
}
