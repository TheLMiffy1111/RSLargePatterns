package thelm.rslargepatterns.client.screen;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import thelm.rslargepatterns.client.screen.widget.CheckboxWidget;

public abstract class BaseScreen<T extends Container> extends ContainerScreen<T> {

	public BaseScreen(T container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	protected abstract ResourceLocation getBackgroundTexture();

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bindTexture(getBackgroundTexture());
		if(xSize > 256 || ySize > 256) {
			blit(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		}
		else {
			blit(guiLeft, guiTop, 0, 0, xSize, ySize);
		}
	}

	public boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
		return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
	}

	private int lastButtonId = 0;

	public Button addButton(int x, int y, int w, int h, String text, Button.IPressable onPress) {
		Button button = new Button(x, y, w, h, text, onPress);
		buttons.add(button);
		children.add(button);
		return button;
	}

	public CheckboxWidget addCheckBox(int x, int y, String text, boolean checked, Consumer<CheckboxButton> onPress) {
		CheckboxWidget checkBox = new CheckboxWidget(x, y, text, checked, onPress);
		addButton(checkBox);
		children.add(checkBox);
		return checkBox;
	}
}
