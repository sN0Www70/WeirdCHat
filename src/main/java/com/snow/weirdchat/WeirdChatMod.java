package com.yourname.weirdchat;

import com.snow.weirdchat.network.NetworkHandler;
import com.snow.weirdchat.storage.AccountStorage;
import com.snow.weirdchat.storage.FollowStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(WeirdChatMod.MODID)
public class WeirdChatMod {
    public static final String MODID = "weirdchat";

    public WeirdChatMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::register);
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        AccountStorage.setServer(event.getServer());
        AccountStorage.loadFromDisk();

        FollowStorage.setServer(event.getServer()); // 🔥 AJOUT
        FollowStorage.loadFromDisk();               // 🔥 AJOUT
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        AccountStorage.saveToDisk();

        FollowStorage.saveToDisk();                // 🔥 AJOUT
    }
}
