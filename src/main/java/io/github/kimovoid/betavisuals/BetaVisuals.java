package io.github.kimovoid.betavisuals;

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BetaVisuals implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();
    public static BVOptions OPTIONS;

    @Override
    public void initClient() {
        MinecraftClientEvents.START.register(mc -> OPTIONS = new BVOptions(mc));
        MinecraftClientEvents.READY.register(mc -> {
            if (OPTIONS.fullscreen) {
                mc.toggleFullscreen();
            }
        });
    }
}
