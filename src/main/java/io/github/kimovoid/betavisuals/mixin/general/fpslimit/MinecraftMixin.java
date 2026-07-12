package io.github.kimovoid.betavisuals.mixin.general.fpslimit;

import io.github.kimovoid.betavisuals.BVOptions;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.options.GameOptions;
import net.minecraft.world.World;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.OpenGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow public GameOptions options;
    @Shadow public Screen screen;
    @Shadow public World world;

    @Inject(method = "init", at = @At("TAIL"))
    private void initVsync(CallbackInfo ci) {
        try {
            Display.setVSyncEnabled(BetaVisuals.OPTIONS.vsync);
        } catch (OpenGLException openGLException) {
            BetaVisuals.OPTIONS.vsync = false;
            this.options.save();
        }
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", shift = At.Shift.AFTER))
    private void setFullscreenVsync(CallbackInfo ci) {
        Display.setVSyncEnabled(BetaVisuals.OPTIONS.vsync);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;logGlError(Ljava/lang/String;)V", ordinal = 1))
    public void sync(CallbackInfo ci) {
        if (this.isFramerateValid()) {
            Display.sync(this.getMaxFramerate());
        }
    }

    @Unique
    public int getMaxFramerate() {
        return !Display.isActive() ? BetaVisuals.OPTIONS.unfocusedFps : this.world == null && this.screen != null ? BetaVisuals.OPTIONS.menuFps : BetaVisuals.OPTIONS.fpsLimit;
    }

    @Unique
    public boolean isFramerateValid() {
        return this.getMaxFramerate() < BVOptions.Option.FRAMERATE_LIMIT.getMax();
    }
}
