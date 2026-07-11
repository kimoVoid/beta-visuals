package io.github.kimovoid.betavisuals.mixin.option;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.gui.VideoSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @WrapOperation(
            method = "buttonClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
                    ordinal = 0
            )
    )
    private void replaceVideoSettings(Minecraft instance, Screen screen, Operation<Void> original) {
        original.call(instance, new VideoSettingsScreen(this, instance.options));
    }
}
