package io.github.kimovoid.betavisuals.mixin.general.renderdistance;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.GameOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/options/GameOptions;set(Lnet/minecraft/client/options/GameOptions$Option;I)V"
            )
    )
    private void setRenderDistance(GameOptions instance, GameOptions.Option option, int value, Operation<Void> original) {
        boolean inverted = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        switch (BetaVisuals.OPTIONS.renderDistance) {
            case 2, 3 -> BetaVisuals.OPTIONS.renderDistance = inverted ? 12 : 4;
            case 4, 5, 6, 7 -> BetaVisuals.OPTIONS.renderDistance = inverted ? 2 : 8;
            case 8, 9, 10, 11 -> BetaVisuals.OPTIONS.renderDistance = inverted ? 4 : 12;
            default -> BetaVisuals.OPTIONS.renderDistance = inverted ? 8 : 2;
        }
    }
}
