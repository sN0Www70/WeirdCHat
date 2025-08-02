package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.weirdchat.storage.AccountStorage;
import com.snow.weirdchat.storage.MessageStorage;
import com.snow.weirdchat.util.MessageSender;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class WeChatAdminCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("wechatadmin")
                .requires(source -> source.hasPermission(3)) // restriction admin
                .then(Commands.argument("account", StringArgumentType.word())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    CommandSource source = ctx.getSource();
                                    String pseudo = StringArgumentType.getString(ctx, "account");
                                    String message = StringArgumentType.getString(ctx, "message");

                                    boolean pseudoExiste = AccountStorage.isPseudoUsed(pseudo);

                                    if (!pseudoExiste) {
                                        MessageStorage.storeGhostMessage(pseudo, message);
                                    } else {
                                        MessageStorage.storeMessage(pseudo, message);
                                    }

                                    // Utilisation corrigée avec le paramètre isGhost
                                    MessageSender.broadcastMessageFromPseudo(pseudo, message, !pseudoExiste);

                                    source.sendSuccess(
                                            new StringTextComponent("§aMessage envoyé en tant que §e" + pseudo + (pseudoExiste ? "" : " (ghost)")),
                                            false
                                    );
                                    return 1;
                                }))));
    }
}
