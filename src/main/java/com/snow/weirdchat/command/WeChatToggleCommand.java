package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.snow.weirdchat.network.NetworkHandler;
import com.snow.weirdchat.network.ToggleWeirdChatPacket;
import com.snow.weirdchat.storage.ToggleStorage;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkDirection;

public class WeChatToggleCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("wechattoggle")
                        .then(Commands.argument("toggle", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayerOrException();
                                    boolean toggle = BoolArgumentType.getBool(ctx, "toggle");

                                    // Met à jour le stockage serveur
                                    ToggleStorage.setEnabled(player, toggle);

                                    // Envoie du packet au client pour mise à jour côté client
                                    NetworkHandler.CHANNEL.sendTo(
                                            new ToggleWeirdChatPacket(player.getUUID(), toggle),
                                            player.connection.getConnection(),
                                            NetworkDirection.PLAY_TO_CLIENT
                                    );

                                    player.sendMessage(
                                            new StringTextComponent("§aWeirdChat affichage " + (toggle ? "activé" : "désactivé")),
                                            player.getUUID()
                                    );
                                    return 1;
                                })
                        )
        );
    }
}
