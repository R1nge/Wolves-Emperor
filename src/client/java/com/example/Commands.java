package com.example;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class Commands {
    public void Initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("attack")
                .executes(context -> {
                            context.getSource().sendFeedback(Text.literal("The command is executed in the client!"));
                            return 1;
                        }
                )));
    }
}

