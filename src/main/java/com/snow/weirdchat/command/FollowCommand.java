package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.snow.weirdchat.storage.AccountStorage;
import com.snow.weirdchat.storage.FollowStorage;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class FollowCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("follow")
                .then(Commands.argument("account", StringArgumentType.word())
                        .executes(ctx -> {
                            CommandSource source = ctx.getSource();

                            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                                source.sendFailure(new StringTextComponent("§cCette commande ne peut être utilisée que par un joueur."));
                                return 0;
                            }

                            ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
                            String targetPseudo = StringArgumentType.getString(ctx, "account");

                            String myPseudo = AccountStorage.getPseudo(player.getUUID());
                            if (myPseudo == null) {
                                player.displayClientMessage(new StringTextComponent("§cTu dois créer un compte WeirdChat avec /createaccount avant de suivre quelqu’un."), false);
                                return 0;
                            }

                            if (!AccountStorage.isPseudoUsed(targetPseudo)) {
                                player.displayClientMessage(new StringTextComponent("§cCe pseudo WeirdChat n'existe pas."), false);
                                return 0;
                            }

                            if (myPseudo.equalsIgnoreCase(targetPseudo)) {
                                player.displayClientMessage(new StringTextComponent("§cTu ne peux pas te suivre toi-même..."), false);
                                return 0;
                            }

                            if (FollowStorage.isFollowing(myPseudo, targetPseudo)) {
                                player.displayClientMessage(new StringTextComponent("§eTu suis déjà §6" + targetPseudo), false);
                                return 1;
                            }

                            FollowStorage.follow(myPseudo, targetPseudo);
                            AccountStorage.addFollower(targetPseudo, myPseudo); // <- Ajout du follower côté WeirdChat

                            player.displayClientMessage(new StringTextComponent("§aTu suis maintenant §e" + targetPseudo + " §asur WeirdChat."), false);
                            return 1;
                        })
                )
        );
    }
}
