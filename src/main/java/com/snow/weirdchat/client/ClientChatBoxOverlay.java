package com.snow.weirdchat.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedList;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientChatBoxOverlay {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final LinkedList<FadingMessage> displayedMessages = new LinkedList<>();
    private static boolean showOverlay = true;

    private static final int FADE_DURATION_MS = 5000; // 5 secondes
    private static final int MAX_MESSAGES = 3;

    public static void toggleOverlay() {
        showOverlay = !showOverlay;
    }

    public static void addMessage(String pseudo, String message) {
        String formatted = pseudo + " : " + message;
        if (displayedMessages.size() >= MAX_MESSAGES) {
            displayedMessages.removeFirst();
        }
        displayedMessages.addLast(new FadingMessage(new StringTextComponent(formatted), System.currentTimeMillis()));
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!showOverlay) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (displayedMessages.isEmpty()) return;

        MatrixStack matrixStack = event.getMatrixStack();
        IngameGui gui = mc.gui;
        FontRenderer fontRenderer = mc.font;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int x = screenWidth - 10;
        int y = 10;
        int lineHeight = fontRenderer.lineHeight + 2;

        long now = System.currentTimeMillis();

        // Parcours et affichage des messages
        for (int i = 0; i < displayedMessages.size(); i++) {
            FadingMessage fading = displayedMessages.get(i);
            long age = now - fading.timestamp;

            if (age >= FADE_DURATION_MS) {
                displayedMessages.remove(i--); // supprime le message et corrige l'index
                continue;
            }

            float alphaFactor = 1.0f - (float) age / FADE_DURATION_MS;
            int alpha = (int) (255 * alphaFactor);
            int color = (alpha << 24) | 0xFFFFFF; // Alpha + blanc

            StringTextComponent msg = fading.message;
            int msgWidth = fontRenderer.width(msg);
            gui.drawString(matrixStack, fontRenderer, msg, x - msgWidth, y + i * lineHeight, color);
        }
    }

    private static class FadingMessage {
        public final StringTextComponent message;
        public final long timestamp;

        public FadingMessage(StringTextComponent message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
