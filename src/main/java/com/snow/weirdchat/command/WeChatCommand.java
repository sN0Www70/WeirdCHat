package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.snow.weirdchat.storage.AccountStorage;
import com.snow.weirdchat.storage.MessageStorage;
import com.snow.weirdchat.util.MessageSender;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class WeChatCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("wechat")
                .then(Commands.literal("all")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(WeChatCommand::sendToAll)))
                .then(Commands.literal("followers")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(WeChatCommand::sendToFollowers)))
                .then(Commands.argument("target", StringArgumentType.word())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(WeChatCommand::sendToPlayer)))
        );
    }

    private static int sendToAll(CommandContext<CommandSource> ctx) {
        try {
            ServerPlayerEntity sender = ctx.getSource().getPlayerOrException();
            String senderPseudo = AccountStorage.getPseudo(sender.getUUID());
            String message = StringArgumentType.getString(ctx, "message");

            if (senderPseudo == null) {
                ctx.getSource().sendFailure(new StringTextComponent("§cAucun compte WeirdChat lié à votre profil."));
                return 0;
            }

            boolean isGhost = !AccountStorage.isPseudoUsed(senderPseudo);
            if (isGhost) MessageStorage.storeGhostMessage(senderPseudo, message);
            else MessageStorage.storeMessage(senderPseudo, message);

            MessageSender.broadcastMessageFromPseudo(senderPseudo, message, isGhost);
            ctx.getSource().sendSuccess(new StringTextComponent("§aMessage envoyé à tous."), false);
            return 1;
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(new StringTextComponent("§cErreur : commande uniquement exécutable par un joueur."));
            return 0;
        }
    }


    private static int sendToFollowers(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity sender;
        try {
            sender = ctx.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(new StringTextComponent("§cErreur : commande uniquement exécutable par un joueur."));
            return 0;
        }

        String senderPseudo = AccountStorage.getPseudo(sender.getUUID());
        String message = StringArgumentType.getString(ctx, "message");

        if (senderPseudo == null) {
            ctx.getSource().sendFailure(new StringTextComponent("§cAucun compte WeirdChat lié à votre profil."));
            return 0;
        }

        boolean isGhost = !AccountStorage.isPseudoUsed(senderPseudo);
        if (isGhost) MessageStorage.storeGhostMessage(senderPseudo, message);
        else MessageStorage.storeMessage(senderPseudo, message);

        MessageSender.sendMessageToFollowers(sender, senderPseudo, message, isGhost);

        ctx.getSource().sendSuccess(new StringTextComponent("§aMessage envoyé à vos abonnés."), false);
        return 1;
    }

    private static int sendToPlayer(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity sender;
        try {
            sender = ctx.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(new StringTextComponent("§cErreur : commande uniquement exécutable par un joueur."));
            return 0;
        }

        String senderPseudo = AccountStorage.getPseudo(sender.getUUID());
        String message = StringArgumentType.getString(ctx, "message");
        String targetName = StringArgumentType.getString(ctx, "target");

        if (senderPseudo == null) {
            ctx.getSource().sendFailure(new StringTextComponent("§cAucun compte WeirdChat lié à votre profil."));
            return 0;
        }

        ServerPlayerEntity targetPlayer = ctx.getSource().getServer().getPlayerList().getPlayerByName(targetName);
        if (targetPlayer == null) {
            ctx.getSource().sendFailure(new StringTextComponent("§cJoueur introuvable : " + targetName));
            return 0;
        }

        boolean isGhost = !AccountStorage.isPseudoUsed(senderPseudo);
        if (isGhost) MessageStorage.storeGhostMessage(senderPseudo, message);
        else MessageStorage.storeMessage(senderPseudo, message);

        MessageSender.sendMessageToPlayer(targetPlayer, senderPseudo, message, isGhost);
        ctx.getSource().sendSuccess(new StringTextComponent("§aMessage envoyé à §e" + targetName), false);
        return 1;
    }
}
