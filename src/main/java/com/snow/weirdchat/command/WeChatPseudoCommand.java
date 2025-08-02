package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.snow.weirdchat.storage.AccountStorage;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class WeChatPseudoCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("wechatpseudo")
                        .requires(source -> source.hasPermission(2)) // Niveau 2 = admin/opérateurs
                        .then(Commands.argument("oldPseudo", StringArgumentType.word())
                                .then(Commands.argument("newPseudo", StringArgumentType.word())
                                        .executes(WeChatPseudoCommand::execute)))
        );
    }

    private static int execute(CommandContext<CommandSource> context) {
        String oldPseudo = StringArgumentType.getString(context, "oldPseudo");
        String newPseudo = StringArgumentType.getString(context, "newPseudo");

        if (!AccountStorage.isPseudoUsed(oldPseudo)) {
            context.getSource().sendFailure(new StringTextComponent("§cLe pseudo §e" + oldPseudo + " §cn'existe pas."));
            return 0;
        }

        if (AccountStorage.isPseudoUsed(newPseudo)) {
            context.getSource().sendFailure(new StringTextComponent("§cLe pseudo §e" + newPseudo + " §cest déjà utilisé."));
            return 0;
        }

        boolean success = AccountStorage.changePseudo(oldPseudo, newPseudo);

        if (success) {
            context.getSource().sendSuccess(new StringTextComponent("§aLe pseudo §e" + oldPseudo + " §aa été changé en §e" + newPseudo), true);
            return 1;
        } else {
            context.getSource().sendFailure(new StringTextComponent("§cErreur lors du changement de pseudo."));
            return 0;
        }
    }
}
