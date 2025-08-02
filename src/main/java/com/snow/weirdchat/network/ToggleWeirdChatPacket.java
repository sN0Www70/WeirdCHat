package com.snow.weirdchat.network;

import com.snow.weirdchat.client.ClientConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ToggleWeirdChatPacket {

    private final UUID playerUUID;
    private final boolean enabled;

    public ToggleWeirdChatPacket(UUID playerUUID, boolean enabled) {
        this.playerUUID = playerUUID;
        this.enabled = enabled;
    }

    // Sérialisation
    public static void encode(ToggleWeirdChatPacket pkt, PacketBuffer buf) {
        buf.writeUUID(pkt.playerUUID);
        buf.writeBoolean(pkt.enabled);
    }

    // Désérialisation
    public static ToggleWeirdChatPacket decode(PacketBuffer buf) {
        UUID playerUUID = buf.readUUID();
        boolean enabled = buf.readBoolean();
        return new ToggleWeirdChatPacket(playerUUID, enabled);
    }

    // Traitement du paquet (côté client uniquement)
    public static void handle(ToggleWeirdChatPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Côté client uniquement (protection Forge)
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientConfig.setShowWeirdChat(pkt.playerUUID, pkt.enabled);
            });
        });
        ctx.get().setPacketHandled(true);
    }

    // Accesseurs
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
