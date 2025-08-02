package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.weirdchat.storage.AccountStorage;
import com.snow.weirdchat.storage.FollowStorage;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class UnfollowCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("unfollow")
                .then(Commands.argument("account", StringArgumentType.word())
                        .executes(ctx -> {
                            // Récupération du joueur ou erreur si console
                            ServerPlayerEntity player;
                            try {
                                player = ctx.getSource().getPlayerOrException();
                            } catch (Exception e) {
                                ctx.getSource().sendFailure(new StringTextComponent("§cCommande utilisable uniquement par un joueur."));
                                return 0;
                            }

                            String targetPseudo = StringArgumentType.getString(ctx, "account");

                            String myPseudo = AccountStorage.getPseudo(player.getUUID());
                            if (myPseudo == null) {
                                player.displayClientMessage(new StringTextComponent(
                                        "§cTu dois créer un compte WeirdChat avec /createaccount avant de gérer tes abonnements."
                                ), false);
                                return 0;
                            }

                            if (!AccountStorage.isPseudoUsed(targetPseudo)) {
                                player.displayClientMessage(new StringTextComponent("§cCe pseudo WeirdChat n'existe pas."), false);
                                return 0;
                            }

                            if (!FollowStorage.isFollowing(myPseudo, targetPseudo)) {
                                player.displayClientMessage(new StringTextComponent("§eTu ne suis pas §6" + targetPseudo), false);
                                return 0;
                            }

                            FollowStorage.unfollow(myPseudo, targetPseudo);
                            AccountStorage.removeFollower(targetPseudo, myPseudo); // <- Suppression du follower côté WeirdChat

                            player.displayClientMessage(new StringTextComponent(
                                    "§aTu ne suis plus §e" + targetPseudo + " §asur WeirdChat."
                            ), false);
                            return 1;
                        })
                )
        );
    }
}
