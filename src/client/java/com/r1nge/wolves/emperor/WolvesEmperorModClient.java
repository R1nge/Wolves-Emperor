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
    private static RandomAccessFile pipe = null;


    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        initializePipe();
        
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

    private void initializePipe() {
        try {
            pipe = new RandomAccessFile("\\\\.\\pipe\\minecraft\\wolvesEmperor", "rw");
            System.out.println("Pipe connection established");
        } catch (Exception e) {
            System.out.println("Failed to initialize pipe: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean readFromPipe() {
        try {
            if (pipe == null) {
                return false;
            }

            // Read response from the already open pipe
            String echoResponse = pipe.readLine();
            if (echoResponse != null && echoResponse.contains("true")) {
                return true;
            }
            if (echoResponse != null) {
                System.out.println("Response: " + echoResponse);
            }
            return false;
        } catch (Exception e) {
            // If there's an error, try to re-establish the connection
            try {
                pipe = new RandomAccessFile("\\\\.\\pipe\\minecraft\\wolvesEmperor", "rw");
            } catch (Exception re) {
                re.printStackTrace();
            }
            e.printStackTrace();
        }
        return false;
    }
}