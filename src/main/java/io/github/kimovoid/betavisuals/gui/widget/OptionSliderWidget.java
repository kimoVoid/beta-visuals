package io.github.kimovoid.betavisuals.gui.widget;

import io.github.kimovoid.betavisuals.BVOptions;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.lwjgl.opengl.GL11;

public class OptionSliderWidget extends ButtonWidget {

   private final BVOptions.Option option;
   private float value;
   public boolean dragging;

   public OptionSliderWidget(int id, int x, int y, BVOptions.Option option) {
      super(id, x, y, 100, 20, "");
      this.option = option;
      this.value = option.normalize(BetaVisuals.OPTIONS.getFloat(option));
      this.message = BetaVisuals.OPTIONS.getValueString(option);
   }

   @Override
   protected int getYImage(boolean hovered) {
      return 0;
   }

   @Override
   protected void renderBackground(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.visible) {
         if (this.dragging) {
            this.value = (float)(mouseX - (this.x + 4)) / (this.width - 8);
            if (this.value < 0.0F) {
               this.value = 0.0F;
            }

            if (this.value > 1.0F) {
               this.value = 1.0F;
            }

            float f = this.option.denormalize(this.value);
            BetaVisuals.OPTIONS.set(this.option, f, false);
            this.value = this.option.normalize(f);
            this.message = BetaVisuals.OPTIONS.getValueString(this.option);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawTexture(this.x + (int)(this.value * (this.width - 8)), this.y, 0, 66, 4, 20);
         this.drawTexture(this.x + (int)(this.value * (this.width - 8)) + 4, this.y, 196, 66, 4, 20);
      }
   }

   @Override
   public boolean mouseClicked(Minecraft minecraft, int mouseX, int mouseY) {
      if (super.mouseClicked(minecraft, mouseX, mouseY)) {
         this.value = (float)(mouseX - (this.x + 4)) / (this.width - 8);
         if (this.value < 0.0F) {
            this.value = 0.0F;
         }

         if (this.value > 1.0F) {
            this.value = 1.0F;
         }

         float f = this.option.denormalize(this.value);
         BetaVisuals.OPTIONS.set(this.option, f, true);
         this.value = this.option.normalize(f);
         this.message = BetaVisuals.OPTIONS.getValueString(this.option);
         this.dragging = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY) {
      if (this.dragging) {
         float f = this.option.denormalize(this.value);
         BetaVisuals.OPTIONS.set(this.option, f, true);
         this.message = BetaVisuals.OPTIONS.getValueString(this.option);
      }
      this.dragging = false;
   }

   public int getWidth() {
      return this.width;
   }
}
