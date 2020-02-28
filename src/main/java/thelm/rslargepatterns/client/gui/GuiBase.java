package thelm.rslargepatterns.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public abstract class GuiBase extends GuiContainer {

	public final Container container;

	public GuiBase(Container container) {
		super(container);
		this.container = container;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	protected abstract ResourceLocation getBackgroundTexture();

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(getBackgroundTexture());
		if(xSize > 256 || ySize > 256) {
			drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		}
		else {
			drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}

	public boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
		return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
	}

	private int lastButtonId = 0;

	public GuiButton addButton(int x, int y, int w, int h, String text) {
		GuiButton button = new GuiButton(lastButtonId++, x, y, w, h, text);
		buttonList.add(button);
		return button;
	}

	public GuiCheckBox addCheckBox(int x, int y, String text, boolean checked) {
		GuiCheckBox checkBox = new GuiCheckBox(lastButtonId++, x, y, text, checked);
		buttonList.add(checkBox);
		return checkBox;
	}
}
