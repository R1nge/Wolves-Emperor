package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExampleModClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if (readFile()) {
                // Execute the command if readFile() returns true
                var serverWorld = MinecraftClient.getInstance();
                serverWorld.player.networkHandler.sendChatCommand("attack");
            }
        }, 0, 1, TimeUnit.SECONDS);

    }


    private boolean readFile() {
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
}