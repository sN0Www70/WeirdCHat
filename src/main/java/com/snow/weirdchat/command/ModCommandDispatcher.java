package com.snow.weirdchat.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Ajoute tous les imports nécessaires (à adapter selon tes packages)
import com.snow.weirdchat.command.WeChatCommand;
import com.snow.weirdchat.command.WeChatToggleCommand;
import com.snow.weirdchat.command.FollowCommand;
import com.snow.weirdchat.command.UnfollowCommand;
import com.snow.weirdchat.command.InfoCommand;
import com.snow.weirdchat.command.WeChatPseudoCommand;
import com.snow.weirdchat.command.WeChatAdminCommand;
import com.snow.weirdchat.command.CreateAccountCommand;

@Mod.EventBusSubscriber
public class ModCommandDispatcher {

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        // Enregistrement des commandes WeirdChat
        WeChatCommand.register(dispatcher);
        WeChatToggleCommand.register(dispatcher);
        FollowCommand.register(dispatcher);
        UnfollowCommand.register(dispatcher);
        InfoCommand.register(dispatcher);
        WeChatPseudoCommand.register(dispatcher);
        WeChatAdminCommand.register(dispatcher);

        // Soit tu appelles comme ça si CreateAccountCommand prend dispatcher
        dispatcher.register(CreateAccountCommand.register());

        // OU si CreateAccountCommand n’a pas de paramètre register()
        // CreateAccountCommand.register();
    }
}
