package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Environment(EnvType.CLIENT)
public class ExampleModClient implements ClientModInitializer {

    private static byte ticksElapsed = 0;
    private static byte tickShouldElapse = 10;

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (readFile()) {
                // Execute the command if readFile() returns true
                var serverWorld = MinecraftClient.getInstance();
                serverWorld.player.networkHandler.sendChatCommand("attack");
                // play sound
                serverWorld.player.networkHandler.sendCommand("playsound minecraft:entity.bee.death master @s");
                resetFile();
            }
        }, 0, 1, TimeUnit.SECONDS);

        ClientTickEvents.END_WORLD_TICK.register(ExampleModClient::detectMiddleClick);
    }

    private static void detectMiddleClick(ClientWorld clientWorld) {
        if (MinecraftClient.getInstance().player != null) {
            ticksElapsed--;
            if (ticksElapsed <= 0) {
                ticksElapsed = 0;
            }

            ExampleMod.LOGGER.info("Ticks elapsed: {}", ticksElapsed);
            if (MinecraftClient.getInstance().mouse.wasMiddleButtonClicked() && ticksElapsed == 0) {
                ticksElapsed = tickShouldElapse;
                var serverWorld = MinecraftClient.getInstance();

                serverWorld.player.networkHandler.sendChatCommand("attack");
                serverWorld.player.networkHandler.sendCommand("playsound minecraft:entity.bee.death master @s");
                resetFile();
            }
        }
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