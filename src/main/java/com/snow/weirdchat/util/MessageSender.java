package com.snow.weirdchat.util;

import com.snow.weirdchat.client.ClientChatBoxOverlay;
import com.snow.weirdchat.network.NetworkHandler;
import com.snow.weirdchat.network.packets.MessagePacket;
import com.snow.weirdchat.storage.AccountStorage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class MessageSender {

    /**
     * Envoie un message WeirdChat à un joueur spécifique.
     */
    public static void sendMessageToPlayer(ServerPlayerEntity player, String senderPseudo, String message, boolean isGhost) {
        if (player == null || senderPseudo == null || message == null) return;

        MessagePacket packet = new MessagePacket(senderPseudo, message, isGhost);
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /**
     * Affiche un message WeirdChat côté client (simulation locale).
     */
    public static void showClientSide(String pseudo, String message) {
        if (pseudo == null || message == null) return;
        ClientChatBoxOverlay.addMessage(pseudo, message);
    }

    /**
     * Diffuse un message WeirdChat à tous les joueurs connectés.
     */
    public static void broadcastMessageFromPseudo(String pseudo, String message, boolean isGhost) {
        if (pseudo == null || message == null) return;

        MessagePacket packet = new MessagePacket(pseudo, message, isGhost);
        NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
        DiscordWebhookSender.sendToDiscord(pseudo, message);
    }

    /**
     * Envoie un message WeirdChat à tous les abonnés du joueur.
     *
     * @param sender       Le joueur qui envoie le message
     * @param senderPseudo Le pseudo WeirdChat du joueur
     * @param message      Le message à envoyer
     * @param isGhost      True si l’émetteur utilise un compte fantôme
     */
    public static void sendMessageToFollowers(ServerPlayerEntity sender, String senderPseudo, String message, boolean isGhost) {
        if (sender == null || senderPseudo == null || message == null) return;

        MinecraftServer server = sender.getServer();
        if (server == null) return;

        PlayerList playerList = server.getPlayerList();
        List<UUID> followerUUIDs = AccountStorage.getFollowerUUIDs(senderPseudo);

        MessagePacket packet = new MessagePacket(senderPseudo, message, isGhost);

        for (UUID uuid : followerUUIDs) {
            ServerPlayerEntity follower = playerList.getPlayer(uuid);
            if (follower != null) {
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> follower), packet);
            }
        }


        System.out.println("[WeirdChat] Followers count for " + senderPseudo + ": " + followerUUIDs.size());
        for (UUID uuid : followerUUIDs) {
            ServerPlayerEntity follower = playerList.getPlayer(uuid);
            if (follower != null) {
                System.out.println("[WeirdChat] Envoi message au follower connecté: " + follower.getName().getString());
            } else {
                System.out.println("[WeirdChat] Follower UUID " + uuid + " non connecté ou introuvable");
            }
        }

    }
}
