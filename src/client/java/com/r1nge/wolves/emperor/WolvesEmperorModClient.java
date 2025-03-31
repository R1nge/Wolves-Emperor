package com.r1nge.wolves.emperor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
            if (readFile()) {
                sendCommand();
                playSound();
                resetFile();
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
                resetFile();
            }
        }
    }

    private static void sendCommand() {
        MinecraftClient.getInstance().player.networkHandler.sendChatCommand("attack");
    }

    private static void playSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F));
    }


    private static boolean readFile() {
        try {
            File myObj = new File("E:\\MyMods\\TEST.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                if (data.contains("true")) {
                    return true;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return false;
    }

    private static void resetFile() {
        try {
            File myObj = new File("E:\\MyMods\\TEST.txt");
            FileWriter myWriter = new FileWriter("E:\\MyMods\\TEST.txt");
            myWriter.write("false");
            myWriter.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}