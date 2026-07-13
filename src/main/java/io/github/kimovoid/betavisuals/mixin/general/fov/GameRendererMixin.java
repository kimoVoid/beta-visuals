package io.github.kimovoid.betavisuals.mixin.general.fov;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow private Minecraft minecraft;
    @Shadow private float renderDistance;
    @Shadow protected abstract float getFov(float tickDelta);
    @Unique private boolean hand = false;

    @ModifyConstant(method = "getFov", constant = @Constant(floatValue = 70.0F))
    private float setFov(float constant) {
        return hand ? constant : BetaVisuals.OPTIONS.fov;
    }

    @ModifyConstant(method = "getFov", constant = @Constant(floatValue = 60.0F))
    private float setWaterFov(float constant) {
        return hand ? constant : BetaVisuals.OPTIONS.fov - 10.0F;
    }

    @Inject(method = "renderItemInHand", at = @At(value = "HEAD"))
    public void adjustHandFov(float tickDelta, int anaglyphRenderPass, CallbackInfo ci) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        this.hand = true;
        GLU.gluPerspective(this.getFov(tickDelta), (float) this.minecraft.width / (float) this.minecraft.height, 0.05F, this.renderDistance * 2.0F);
        this.hand = false;
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
}
