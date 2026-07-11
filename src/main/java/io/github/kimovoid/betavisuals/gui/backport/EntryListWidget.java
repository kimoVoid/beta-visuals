package io.github.kimovoid.betavisuals.gui.backport;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.vertex.Tesselator;

/**
 * This is lowkey just backported from release 1.7 because older versions
 * don't have all the functions that I need so why reinvent the wheel?
 */
public abstract class EntryListWidget extends ListWidget {

    public EntryListWidget(Minecraft minecraft, int width, int height, int minY, int maxY, int itemHeight) {
        super(minecraft, width, height, minY, maxY, itemHeight);
    }

    @Override
    protected void entryClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
    }

    @Override
    protected boolean isEntrySelected(int index) {
        return false;
    }

    @Override
    protected void renderBackground() {
    }

    @Override
    protected void renderEntry(int index, int x, int y, int entryHeight, Tesselator tesselator, int mouseX, int mouseY) {
        this.getEntry(index).render(index, x, y, this.getRowWidth(), entryHeight, tesselator, mouseX, mouseY, this.getEntryAt(mouseX, mouseY) == index);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseInList(mouseY)) {
            int i = this.getEntryAt(mouseX, mouseY);
            if (i >= 0) {
                int x = this.minX + this.width / 2 - this.getRowWidth() / 2 + 2;
                int y = this.minY + 4 - this.getScrollAmount() + i * this.entryHeight + this.headerHeight;
                if (this.getEntry(i).mouseClicked(i, mouseX, mouseY, button, mouseX - x, mouseY - y)) {
                    this.setScrolling(false);
                }
            }
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        for (int i = 0; i < this.size(); i++) {
            int x = this.minX + this.width / 2 - this.getRowWidth() / 2 + 2;
            int y = this.minY + 4 - this.getScrollAmount() + i * this.entryHeight + this.headerHeight;
            this.getEntry(i).mouseReleased(i, mouseX, mouseY, button, mouseX - x, mouseY - y);
        }

        this.setScrolling(true);
    }

    public abstract Entry getEntry(int index);

    @Environment(EnvType.CLIENT)
    public interface Entry {
        void render(int index, int x, int y, int width, int height, Tesselator tesselator, int mouseX, int mouseY, boolean hovered);
        boolean mouseClicked(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY);
        void mouseReleased(int index, int mouseX, int mouseY, int button, int entryMouseX, int entryMouseY);
    }
}
