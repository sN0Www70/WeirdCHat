package com.snow.weirdchat.network;

import com.snow.weirdchat.network.packets.MessagePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("weirdchat", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        // Paquet pour activer/désactiver WeirdChat côté client
        CHANNEL.registerMessage(
                packetId++,
                ToggleWeirdChatPacket.class,
                ToggleWeirdChatPacket::encode,
                ToggleWeirdChatPacket::decode,
                ToggleWeirdChatPacket::handle
        );

        // Paquet pour afficher un message WeirdChat
        CHANNEL.registerMessage(
                packetId++,
                MessagePacket.class,
                MessagePacket::encode,
                MessagePacket::decode,
                MessagePacket::handle
        );
    }
}
