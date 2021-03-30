package thelm.rslargepatterns.client.screen;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.render.FluidRenderer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.client.screen.widget.CheckboxWidget;
import thelm.rslargepatterns.slot.FalseCopyFluidSlot;
import thelm.rslargepatterns.slot.PreviewFluidSlot;

public abstract class BaseScreen<T extends Container> extends ContainerScreen<T> {

	public BaseScreen(T container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	protected abstract ResourceLocation getBackgroundTexture();

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
		for(int i = 0; i < container.inventorySlots.size(); ++i) {
			Slot slot = container.inventorySlots.get(i);
			if(slot.isEnabled() && slot instanceof FalseCopyFluidSlot) {
				FluidStack stack = ((FalseCopyFluidSlot)slot).fluidInventory.getStackInSlot(slot.getSlotIndex());
				if(!stack.isEmpty() && inBounds(slot.xPos, slot.yPos, 17, 17, mouseX-guiLeft, mouseY-guiTop)) {
					renderTooltip(matrixStack, stack.getDisplayName(), mouseX-guiLeft, mouseY-guiTop);
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		minecraft.getTextureManager().bindTexture(getBackgroundTexture());
		if(xSize > 256 || ySize > 256) {
			blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		}
		else {
			blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
		}
		for(int i = 0; i < container.inventorySlots.size(); ++i) {
			Slot slot = container.inventorySlots.get(i);
			if(slot.isEnabled() && slot instanceof FalseCopyFluidSlot) {
				FluidStack stack = ((FalseCopyFluidSlot)slot).fluidInventory.getStackInSlot(slot.getSlotIndex());
				if(!stack.isEmpty()) {
					FluidRenderer.INSTANCE.render(matrixStack, guiLeft+slot.xPos, guiTop+slot.yPos, stack);
					if(!(slot instanceof PreviewFluidSlot)) {
						renderQuantity(matrixStack, guiLeft+slot.xPos, guiTop+slot.yPos, API.instance().getQuantityFormatter().formatInBucketForm(stack.getAmount()), 0xFFFFFF);
					}
					RenderSystem.disableLighting();
				}
			}
		}
		minecraft.getTextureManager().bindTexture(getBackgroundTexture());
	}

	public boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
		return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
	}

	private int lastButtonId = 0;

	public Button addButton(int x, int y, int w, int h, ITextComponent text, Button.IPressable onPress) {
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

	public void renderQuantity(MatrixStack matrixStack, int x, int y, String qty, int color) {
		boolean large = minecraft.getForceUnicodeFont() || RS.CLIENT_CONFIG.getGrid().getLargeFont();
		matrixStack.push();
		matrixStack.translate(x, y, 300.0D);
		if(!large) {
			matrixStack.scale(0.5F, 0.5F, 1.0F);
		}
		font.drawStringWithShadow(matrixStack, qty, (large ? 16 : 30)-font.getStringWidth(qty), large ? 8F : 22F, color);
		matrixStack.pop();
	}
}
