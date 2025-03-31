package com.r1nge.dogs.emperor;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DogsEmperorMod implements ModInitializer {
    public static final String MOD_ID = "dogs_emperor";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public Commands commands = new Commands();
    private static MinecraftServer server;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            DogsEmperorMod.server = server;
        });
    }

    public static MinecraftServer getServer() {
        return server;
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        DogsEmperorMod.init();
        LOGGER.info("Hello Fabric world!");
        commands.Initialize();
    }
}