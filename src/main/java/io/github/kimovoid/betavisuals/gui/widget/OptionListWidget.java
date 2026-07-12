package io.github.kimovoid.betavisuals.gui.widget;

import io.github.kimovoid.betavisuals.BVOptions;
import io.github.kimovoid.betavisuals.BetaVisuals;
import io.github.kimovoid.betavisuals.gui.backport.EntryListWidget;
import io.github.kimovoid.betavisuals.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.vertex.Tesselator;
import net.minecraft.locale.Language;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class OptionListWidget extends EntryListWidget {

    private final static int MARGIN = 5;
    private final static int TOOLTIP_DELAY = 10;

    private final List<EntryListWidget.Entry> entries = new ArrayList<>();
    private BVOptions.Option hovered;
    private int hoveredTicks = 0;

    public OptionListWidget(Minecraft minecraft, int width, int height, int minY, int maxY, int itemHeight, BVOptions.Category category) {
        super(minecraft, width, height, minY, maxY, itemHeight);
        this.centerAlongY = false;

        this.minX = 120 + MARGIN;
        this.maxX = width - MARGIN;

        if (category.getOptions() == null || category.getOptions().length < 1) {
            return;
        }

        for (BVOptions.Option option : category.getOptions()) {
            /*
            if (!option.getSection().isEmpty()) {
                this.entries.add(new SectionEntry(option.getSection()));
            }
             */
            this.entries.add(new Entry(option, this.getButton(option)));
        }
    }

    private ButtonWidget getButton(BVOptions.Option option) {
        int id = option.getId();
        return option.isFloat() ? new OptionSliderWidget(id, 0, 0, option) : new OptionButtonWidget(id, 0, 0, 100, 20, option);
    }

    @Override
    public EntryListWidget.Entry getEntry(int i) {
        return this.entries.get(i);
    }

    @Override
    protected int size() {
        return this.entries.size();
    }

    @Override
    public int getRowWidth() {
        return this.maxX - this.minX - (MARGIN * 2);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 6 - MARGIN;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        Minecraft mc = Minecraft.INSTANCE;
        Window window = new Window(mc.options, mc.width, mc.height);
        double scale = window.scale;

        GL11.glScissor(
                (int) (this.minX * scale),
                (int) (mc.height - this.maxY * scale),
                (int) ((this.maxX - this.minX) * scale),
                (int) ((this.maxY - this.minY) * scale)
        );
        GL11.glGetError();

        super.render(mouseX, mouseY, tickDelta);
        if (this.hoveredTicks == TOOLTIP_DELAY && this.hovered != null && !this.hovered.getTooltip().isEmpty()) {
            this.renderTooltip(mouseY);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void tick() {
        if (this.hoveredTicks < TOOLTIP_DELAY) {
            this.hoveredTicks++;
        }
    }

    private void renderTooltip(int mouseY) {
        int height = Math.min(100, (this.maxY - this.minY) / 2);
        int minY = this.maxY - height;
        int maxY = this.maxY;
        int maxX = this.maxX - (OptionListWidget.this.getMaxScroll() > 0 ? 6 : 0);
        if (mouseY > minY) {
            minY = this.minY + 2;
            maxY = minY + height;
        }

        // border
        GuiUtil.fill(this.minX + 1, minY - 1, maxX - 1, minY, 1694498815);
        GuiUtil.fill(maxX - 1, minY, maxX - 2, maxY - 2, 1694498815);
        GuiUtil.fill(this.minX + 1, maxY - 1, maxX - 1, maxY - 2, 1694498815);
        GuiUtil.fill(this.minX + 1, minY, this.minX + 2, maxY - 2, 1694498815);

        // fill
        GuiUtil.fill(this.minX + 2, minY, maxX - 2, maxY - 2, -352321536);

        // title
        this.minecraft.textRenderer.splitAndDraw(
                Language.getInstance().translate(this.hovered.getName()),
                this.minX + 8,
                minY + 8,
                maxX - this.minX - 8,
                16777120
        );

        // tooltip
        this.minecraft.textRenderer.splitAndDraw(
                this.hovered.getTooltip(),
                this.minX + 8,
                minY + 20,
                this.maxX - this.minX - 8,
                -3618616
        );
    }

    public class Entry implements EntryListWidget.Entry {

        private final Minecraft minecraft = Minecraft.INSTANCE;
        private final BVOptions.Option option;
        private final ButtonWidget btn;

        public Entry(BVOptions.Option option, ButtonWidget btn) {
            this.option = option;
            this.btn = btn;
        }

        @Override
        public void render(int index, int x, int y, int width, int height, Tesselator tesselator, int mouseX, int mouseY, boolean hovered) {
            boolean hoveringText = this.isHoveringText(mouseX, mouseY, x, y, height);
            if (hoveringText) {
                if (OptionListWidget.this.hovered != this.option) {
                    OptionListWidget.this.hoveredTicks = 0;
                }
                OptionListWidget.this.hovered = this.option;
            }

            if (OptionListWidget.this.hovered == this.option && !hoveringText) {
                OptionListWidget.this.hoveredTicks = 0;
            }

            TextRenderer textRenderer = this.minecraft.textRenderer;
            String name = BetaVisuals.OPTIONS.getName(option);
            int nameWidth = textRenderer.getWidth(name);
            int color = hovered ? 16777120 : 14737632;

            textRenderer.drawWithShadow(name, x + 5, y + (height - 8) / 2, color);

            if (this.btn != null) {
                int buttonWidth = this.btn instanceof OptionButtonWidget
                        ? ((OptionButtonWidget)this.btn).getWidth()
                        : ((OptionSliderWidget)this.btn).getWidth();

                this.btn.x = x + width - buttonWidth - (OptionListWidget.this.getMaxScroll() > 0 ? 6 : 0);
                this.btn.y = y;
                this.btn.render(this.minecraft, mouseX, mouseY);

                GuiUtil.fill(x + nameWidth + 10, y + (height / 2), btn.x - MARGIN, y + (height / 2) + 1, 855638015);
            }
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            if (this.btn.mouseClicked(this.minecraft, mouseX, mouseY)) {
                this.minecraft.soundEngine.play("random.click", 1.0F, 1.0F);

                if (this.btn instanceof OptionButtonWidget) {
                    BetaVisuals.OPTIONS.set(((OptionButtonWidget) this.btn).getOption(), 1);
                    ((OptionButtonWidget) this.btn).update();
                }

                this.minecraft.options.save();
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            if (this.btn != null) {
                this.btn.mouseReleased(mouseX, mouseY);
            }
        }

        private boolean isHoveringText(int mouseX, int mouseY, int x, int y, int height) {
            String name = BetaVisuals.OPTIONS.getName(option);
            int width = this.minecraft.textRenderer.getWidth(name);
            return  mouseX >= x && mouseY >= y && mouseX < x + width + 10 && mouseY < y + height;
        }
    }

    public class SectionEntry implements EntryListWidget.Entry {
        private final String name;
        private final int nameWidth;

        public SectionEntry(String key) {
            this.name = Language.getInstance().translate(key);
            this.nameWidth = OptionListWidget.this.minecraft.textRenderer.getWidth(this.name);
        }

        @Override
        public void render(int index, int x, int y, int width, int height, Tesselator tesselator, int mouseX, int mouseY, boolean hovered) {
            OptionListWidget.this.minecraft
                    .textRenderer
                    .draw(
                            this.name,
                            OptionListWidget.this.minecraft.screen.width / 2 - this.nameWidth / 2,
                            y + 8,
                            -8882056
                    );
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY) {
        }
    }
}
