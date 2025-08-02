package com.snow.weirdchat.network.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessagePacket {
    private final String pseudo;
    private final String message;
    private final boolean isGhost;

    public MessagePacket(String pseudo, String message, boolean isGhost) {
        this.pseudo = pseudo;
        this.message = message;
        this.isGhost = isGhost;
    }

    // Encoder le packet dans le buffer
    public static void encode(MessagePacket pkt, PacketBuffer buf) {
        buf.writeUtf(pkt.pseudo);
        buf.writeUtf(pkt.message);
        buf.writeBoolean(pkt.isGhost);
    }

    // Décoder le packet depuis le buffer
    public static MessagePacket decode(PacketBuffer buf) {
        String pseudo = buf.readUtf(32767); // taille max raisonnable
        String message = buf.readUtf(32767);
        boolean isGhost = buf.readBoolean();
        return new MessagePacket(pseudo, message, isGhost);
    }

    // Handler côté client
    public static void handle(MessagePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Ajouter le message à l'affichage client
            com.snow.weirdchat.client.ClientChatBoxOverlay.addMessage(
                    pkt.pseudo, pkt.message
            );
        });
        ctx.get().setPacketHandled(true);
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMessage() {
        return message;
    }

    public boolean isGhost() {
        return isGhost;
    }
}
