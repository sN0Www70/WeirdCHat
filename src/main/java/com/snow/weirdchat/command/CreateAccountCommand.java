package com.snow.weirdchat.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.snow.weirdchat.storage.AccountStorage;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CreateAccountCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("createaccount")
                .then(Commands.argument("pseudo", StringArgumentType.word())
                        .executes(CreateAccountCommand::createAccount));
    }

    private static int createAccount(CommandContext<CommandSource> ctx) {
        ServerPlayerEntity player;
        try {
            player = ctx.getSource().getPlayerOrException();
        } catch (Exception e) {
            ctx.getSource().sendFailure(new StringTextComponent("Commande uniquement exécutable par un joueur."));
            return 0;
        }

        String pseudo = StringArgumentType.getString(ctx, "pseudo");

        if (AccountStorage.isPseudoUsed(pseudo)) {
            player.sendMessage(new StringTextComponent("§cLe pseudo WeirdChat '" + pseudo + "' est déjà pris."), player.getUUID());
            return 0;
        }

        if (AccountStorage.hasAccountForPlayer(player)) {
            player.sendMessage(new StringTextComponent("§cVous avez déjà un compte WeirdChat."), player.getUUID());
            return 0;
        }

        AccountStorage.createAccount(player, pseudo);
        player.sendMessage(new StringTextComponent("§aCompte WeirdChat créé avec le pseudo : " + pseudo), player.getUUID());

        return 1;
    }
}
