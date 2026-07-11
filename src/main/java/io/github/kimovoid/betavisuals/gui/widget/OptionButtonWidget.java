package io.github.kimovoid.betavisuals.gui.widget;

import io.github.kimovoid.betavisuals.BVOptions;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;

public class OptionButtonWidget extends ButtonWidget {

    private final BVOptions.Option option;

    public OptionButtonWidget(int id, int x, int y, int width, int height, BVOptions.Option option) {
        super(id, x, y, width, height, "");
        this.option = option;
        this.update();
    }

    public BVOptions.Option getOption() {
        return this.option;
    }

    public int getWidth() {
        return this.width;
    }

    public void update() {
        TextRenderer textRenderer = Minecraft.INSTANCE.textRenderer;
        String str = BetaVisuals.OPTIONS.getValueString(option);
        this.message = str;

        int width = textRenderer.getWidth(str);
        if (width > this.width) {
            this.width = textRenderer.getWidth(str) + 20;
        }
    }
}
