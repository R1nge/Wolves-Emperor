package com.r1nge.wolves.emperor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Environment(EnvType.CLIENT)
public class WolvesEmperorModClient implements ClientModInitializer {

    private static byte ticksElapsed = 0;
    private static byte tickShouldElapse = 10;

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (readFromPipe()) {
                sendCommand();
                playSound();
            }
        }, 0, 1, TimeUnit.SECONDS);

        ClientTickEvents.END_WORLD_TICK.register(WolvesEmperorModClient::detectMiddleClick);
    }

    private static void detectMiddleClick(ClientWorld clientWorld) {
        if (MinecraftClient.getInstance().player != null) {
            ticksElapsed--;
            if (ticksElapsed <= 0) {
                ticksElapsed = 0;
            }

            WolvesEmperorMod.LOGGER.info("Ticks elapsed: {}", ticksElapsed);
            if (MinecraftClient.getInstance().mouse.wasMiddleButtonClicked() && ticksElapsed == 0) {
                ticksElapsed = tickShouldElapse;
                sendCommand();
                playSound();
            }
        }
    }

    private static void sendCommand() {
        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("attack");
    }

    private static void playSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F));
    }

    private static boolean readFromPipe() {
        try {
            // Connect to the pipe
            RandomAccessFile pipe = new RandomAccessFile("\\\\.\\pipe\\minecraft\\wolvesEmperor", "rw");
            //String echoText = "Hello word\n";
            // write to pipe
            //pipe.write(echoText.getBytes());
            // read response
            String echoResponse = pipe.readLine();
            if (echoResponse.contains("true")) {
                pipe.close();
                return true;
            }
            System.out.println("Response: " + echoResponse);
            pipe.close();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}