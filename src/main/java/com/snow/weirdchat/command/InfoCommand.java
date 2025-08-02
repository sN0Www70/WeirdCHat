package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.weirdchat.storage.AccountStorage;
import com.snow.weirdchat.storage.FollowStorage;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class InfoCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("info")
                .then(Commands.argument("account", StringArgumentType.word())
                        .executes(ctx -> {
                            String targetPseudoRaw = StringArgumentType.getString(ctx, "account");
                            String targetPseudo = targetPseudoRaw.toLowerCase();

                            if (!AccountStorage.isPseudoUsed(targetPseudo)) {
                                ctx.getSource().sendFailure(new StringTextComponent("§cCe pseudo WeirdChat n'existe pas."));
                                return 0;
                            }

                            int followerCount = FollowStorage.getFollowers(targetPseudo).size();
                            ctx.getSource().sendSuccess(new StringTextComponent(
                                    "§aLe compte §e" + targetPseudoRaw + " §aa " + followerCount + " follower" + (followerCount > 1 ? "s" : "") + " sur WeirdChat."
                            ), false);
                            return 1;
                        })));
    }
}
