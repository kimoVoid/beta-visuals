package io.github.kimovoid.betavisuals.mixin.option;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow public int viewDistance;

    @Inject(method = "save", at = @At("TAIL"))
    private void saveOptions(CallbackInfo ci) {
        BetaVisuals.OPTIONS.save();
    }

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("TAIL"))
    private void nukeOptions(Minecraft minecraft, File dir, CallbackInfo ci) {
        this.viewDistance = 0;
    }
}
