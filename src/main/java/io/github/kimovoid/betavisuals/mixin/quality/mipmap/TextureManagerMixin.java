package io.github.kimovoid.betavisuals.mixin.quality.mipmap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import io.github.kimovoid.betavisuals.BetaVisuals;
import io.github.kimovoid.betavisuals.compat.MCPatcherCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.platform.MemoryTracker;
import net.minecraft.client.render.texture.DynamicTexture;
import net.minecraft.client.render.texture.TextureManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

    @Shadow public static boolean MIPMAP;
    @Shadow private ByteBuffer imageBuffer;
    @Unique private int itemsTextureId = -1;
    @Unique private ByteBuffer[] mipmapBuffer;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/texture/TextureManager;texturePacks:Lnet/minecraft/client/resource/pack/TexturePacks;",
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void onInit(CallbackInfo ci) {
        this.allocateBuffer();
    }

    @Inject(
            method = "load(Ljava/lang/String;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/texture/TextureManager;load(Ljava/awt/image/BufferedImage;I)V",
                    ordinal = 4
            )
    )
    private void getItemsTextureId(String path, CallbackInfoReturnable<Integer> cir, @Local int i) {
        if (path.equals("/gui/items.png")) {
            this.itemsTextureId = i;
        }
    }

    @Inject(
            method = "load(Ljava/awt/image/BufferedImage;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glBindTexture(II)V",
                    remap = false
            )
    )
    private void setMipmap(BufferedImage image, int id, CallbackInfo ci) {
        MIPMAP = BetaVisuals.OPTIONS.mipmapLevel > 0 && id != this.itemsTextureId;
    }

    @WrapOperation(
            method = "load(Ljava/awt/image/BufferedImage;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTexParameteri(III)V",
                    ordinal = 0,
                    remap = false
            )
    )
    private void setMipmapType(int target, int pname, int param, Operation<Void> original) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, BetaVisuals.OPTIONS.mipmapType == 0 ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_NEAREST_MIPMAP_LINEAR);
    }

    @Inject(
            method = "load(Ljava/awt/image/BufferedImage;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTexParameteri(III)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void doMipmap(BufferedImage image, int id, CallbackInfo ci) {
        if (GLContext.getCapabilities().OpenGL12) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            int mipmapLevel = BetaVisuals.OPTIONS.mipmapLevel;
            if (mipmapLevel >= 4) {
                int ai = Math.min(image.getWidth(), image.getHeight());
                mipmapLevel = this.getMaxMipmapLevel(ai) - 4;
            }
            if (mipmapLevel < 0) {
                mipmapLevel = 0;
            }

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmapLevel);
        }
    }

    /**
     * We turn the background of transparent blocks into a darker gray.
     * Notably fixes tall grass and leaves having black pixels.
     */
    @Inject(method = "load(Ljava/awt/image/BufferedImage;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;anaglyph:Z", opcode = Opcodes.GETFIELD))
    private void fixFoliageAlpha(CallbackInfo ci, @Local(index = 8) int alpha, @Local(index = 9) LocalIntRef red, @Local(index = 10) LocalIntRef green, @Local(index = 11) LocalIntRef blue) {
        if (alpha == 0) {
            red.set(100);
            green.set(100);
            blue.set(100);
        }
    }

    @Inject(
            method = "load(Ljava/awt/image/BufferedImage;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTexImage2D(IIIIIIIILjava/nio/ByteBuffer;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER,
                    remap = false
            ),
            cancellable = true
    )
    private void makeMipmaps(BufferedImage image, int id, CallbackInfo ci) {
        if (MIPMAP) {
            this.generateMipmaps(this.imageBuffer, image.getWidth(), image.getHeight(), this.mipmapBuffer);
            ci.cancel();
        }
    }

    @WrapOperation(method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/texture/TextureManager;MIPMAP:Z",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private boolean fixDynamicMipmaps(Operation<Boolean> original) {
        return false;
    }

    @Inject(method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTexSubImage2D(IIIIIIIILjava/nio/ByteBuffer;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void doFastDynamicMipmaps(CallbackInfo ci, @Local DynamicTexture dynamicTexture2, @Local(index = 3) int j, @Local(index = 4) int l) {
        if (MIPMAP && j == 0 && l == 0) {
            int tileSize = FabricLoader.getInstance().isModLoaded("mcpatcherfabric") ? MCPatcherCompat.getTileSize() : 16;
            int xOffset = dynamicTexture2.sprite % 16 * tileSize;
            int yOffset = dynamicTexture2.sprite / 16 * tileSize;
            this.generateDynamicMipmaps(xOffset, yOffset, tileSize, tileSize, this.imageBuffer, dynamicTexture2.replicate);
        }
    }

    @Unique
    private void allocateBuffer() {
        ArrayList<ByteBuffer> list = new ArrayList<>();
        for (int mipWidth = 2048 / 2; mipWidth > 0; mipWidth /= 2) {
            int mipLen = mipWidth * mipWidth * 4;
            ByteBuffer buf = MemoryTracker.createByteBuffer(mipLen);
            list.add(buf);
        }
        this.mipmapBuffer = list.toArray(new ByteBuffer[0]);
    }

    @Unique
    public void generateMipmaps(ByteBuffer data, int width, int height, ByteBuffer[] mipImageDatas) {
        ByteBuffer parMipData = data;

        for (int level = 1; level <= 16; ++level) {
            int parWidth = width >> level - 1;
            int mipWidth = width >> level;
            int mipHeight = height >> level;
            if (mipWidth <= 0 || mipHeight <= 0) {
                break;
            }

            ByteBuffer mipData = mipImageDatas[level - 1];

            for (int mipX = 0; mipX < mipWidth; ++mipX) {
                for (int mipY = 0; mipY < mipHeight; ++mipY) {
                    int p1 = parMipData.getInt((mipX * 2 + (mipY * 2) * parWidth) * 4);
                    int p2 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2) * parWidth) * 4);
                    int p3 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2 + 1) * parWidth) * 4);
                    int p4 = parMipData.getInt((mipX * 2 + (mipY * 2 + 1) * parWidth) * 4);
                    if (BetaVisuals.OPTIONS.mipmapType == 1) {
                        p3 = parMipData.getInt((mipX * 2 + (mipY * 2 + 1) * parWidth) * 4);
                        p4 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2 + 1) * parWidth) << 2);
                    }
                    int pixel = this.weightedAverageColor(p1, p2, p3, p4);
                    mipData.putInt((mipX + mipY * mipWidth) * 4, BetaVisuals.OPTIONS.mipmapType == 1 && level == 1 ? p4 : pixel);
                }
            }

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, mipWidth, mipHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mipData);
            parMipData = mipData;
        }
    }

    @Unique
    public int weightedAverageColor(int c1, int c2, int c3, int c4) {
        int cx1 = this.weightedAverageColor(c1, c2);
        int cx2 = this.weightedAverageColor(c3, c4);
        return this.weightedAverageColor(cx1, cx2);
    }

    @Unique
    public int weightedAverageColor(int c1, int c2) {
        int a1 = (c1 & -16777216) >> 24 & 255;
        int a2 = (c2 & -16777216) >> 24 & 255;
        int ax = (a1 + a2) / 2;
        if(a1 == 0 && a2 == 0) {
            a1 = 1;
            a2 = 1;
        } else {
            if(a1 == 0) {
                c1 = c2;
                ax /= 2;
            }

            if(a2 == 0) {
                c2 = c1;
                ax /= 2;
            }
        }

        int r1 = (c1 >> 16 & 255) * a1;
        int g1 = (c1 >> 8 & 255) * a1;
        int b1 = (c1 & 255) * a1;
        int r2 = (c2 >> 16 & 255) * a2;
        int g2 = (c2 >> 8 & 255) * a2;
        int b2 = (c2 & 255) * a2;
        int rx = (r1 + r2) / (a1 + a2);
        int gx = (g1 + g2) / (a1 + a2);
        int bx = (b1 + b2) / (a1 + a2);
        return ax << 24 | rx << 16 | gx << 8 | bx;
    }

    @Unique
    public int getMaxMipmapLevel(int size) {
        int level;
        for(level = 0; size > 0; ++level) {
            size /= 2;
        }
        return level - 1;
    }

    @Unique
    private int averageColor(int i, int j) {
        int k = (i & -16777216) >> 24 & 255;
        int l = (j & -16777216) >> 24 & 255;
        return (k + l >> 1 << 24) + ((i & 16711422) + (j & 16711422) >> 1);
    }

    @Unique
    private void generateDynamicMipmaps(int xOffset, int yOffset, int width, int height, ByteBuffer data, int numTiles) {
        ByteBuffer parMipData = data;

        for (int level = 1; level <= 16; ++level) {
            int parWidth = width >> level - 1;
            int mipWidth = width >> level;
            int mipHeight = height >> level;
            int xMipOffset = xOffset >> level;
            int yMipOffset = yOffset >> level;
            if (mipWidth == 0 || mipHeight == 0) {
                break;
            }

            ByteBuffer mipData = this.mipmapBuffer[level - 1];

            for (int ix = 0; ix < mipWidth; ++ix) {
                for (int iy = 0; iy < mipHeight; ++iy) {
                    int p1 = parMipData.getInt((ix * 2 + (iy * 2) * parWidth) * 4);
                    int p2 = parMipData.getInt((ix * 2 + 1 + (iy * 2) * parWidth) * 4);
                    int p3 = parMipData.getInt((ix * 2 + 1 + (iy * 2 + 1) * parWidth) * 4);
                    int p4 = parMipData.getInt((ix * 2 + (iy * 2 + 1) * parWidth) * 4);
                    if (BetaVisuals.OPTIONS.mipmapType == 1) {
                        p3 = parMipData.getInt((ix * 2 + (iy * 2 + 1) * parWidth) * 4);
                        p4 = parMipData.getInt((ix * 2 + 1 + (iy * 2 + 1) * parWidth) << 2);
                    }
                    int pixel = this.averageColor(this.averageColor(p1, p2), this.averageColor(p3, p4));
                    mipData.putInt((ix + iy * mipWidth) * 4, BetaVisuals.OPTIONS.mipmapType == 1 && level == 1 ? p4 : pixel);
                }
            }

            for (int ix = 0; ix < numTiles; ++ix) {
                for (int iy = 0; iy < numTiles; ++iy) {
                    int xOff = xMipOffset + (ix * mipWidth);
                    int yOff = yMipOffset + (iy * mipHeight);
                    GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOff, yOff, mipWidth, mipHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mipData);
                    GL11.glGetError();
                }
            }

            parMipData = mipData;
        }
    }
}
