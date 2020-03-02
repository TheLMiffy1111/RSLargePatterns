package thelm.rslargepatterns.client.screen;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;
import thelm.rslargepatterns.integration.jei.RSLargePatternsJEIPlugin;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.ClearPatternPacket;
import thelm.rslargepatterns.network.packet.CreatePatternPacket;
import thelm.rslargepatterns.network.packet.SetProcessingTypePacket;
import thelm.rslargepatterns.slot.FalseCopyFluidSlot;
import thelm.rslargepatterns.slot.FalseCopySlot;

public class LargePatternEncoderScreen extends BaseScreen<LargePatternEncoderContainer> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("rslargepatterns:textures/gui/large_pattern_encoder.png");
	public static final ResourceLocation RS_ICONS = new ResourceLocation("refinedstorage:textures/icons.png");

	public LargePatternEncoderScreen(LargePatternEncoderContainer container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
		xSize = 258;
		ySize = 277;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	private boolean isOverClear(double mouseX, double mouseY) {
		return inBounds(172, 19, 7, 7, mouseX, mouseY);
	}

	private boolean isOverCreatePattern(double mouseX, double mouseY) {
		return inBounds(216, 56, 16, 16, mouseX, mouseY) && container.tile.canCreatePattern();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		int ty = 0;
		if(isOverCreatePattern(mouseX-guiLeft, mouseY-guiTop)) {
			ty = 1;
		}
		if(!container.tile.canCreatePattern()) {
			ty = 2;
		}
		blit(guiLeft+216, guiTop+56, 258, 16*ty, 16, 16, 512, 512);
		for(int i = 0; i < container.inventorySlots.size(); ++i) {
			Slot slot = container.inventorySlots.get(i);
			if(slot.isEnabled() && slot instanceof FalseCopyFluidSlot) {
				FluidStack stack = ((FalseCopyFluidSlot)slot).fluidInventory.getStackInSlot(slot.getSlotIndex());
				if(!stack.isEmpty()) {
					FluidRenderer.INSTANCE.render(guiLeft+slot.xPos, guiTop+slot.yPos, stack);
					renderQuantity(guiLeft+slot.xPos, guiTop+slot.yPos, API.instance().getQuantityFormatter().formatInBucketForm(stack.getAmount()), 0xFFFFFF);
					RenderSystem.disableLighting();
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		font.drawString(title.getFormattedText(), xSize/2-font.getStringWidth(title.getFormattedText())/2, 6, 0x404040);
		font.drawString(container.playerInventory.getDisplayName().getFormattedText(), container.getPlayerInvX(), container.getPlayerInvY()-11, 0x404040);
		for(int i = 0; i < container.inventorySlots.size(); ++i) {
			Slot slot = container.inventorySlots.get(i);
			if(slot.isEnabled() && slot instanceof FalseCopyFluidSlot) {
				FluidStack stack = ((FalseCopyFluidSlot)slot).fluidInventory.getStackInSlot(slot.getSlotIndex());
				if(!stack.isEmpty() && inBounds(slot.xPos, slot.yPos, 17, 17, mouseX-guiLeft, mouseY-guiTop)) {
					renderTooltip(stack.getDisplayName().getFormattedText(), mouseX-guiLeft, mouseY-guiTop);
				}
			}
		}
		for(Widget widget : buttons) {
			if(widget.isMouseOver(mouseX, mouseY)) {
				widget.renderToolTip(mouseX-guiLeft, mouseY-guiTop);
				break;
			}
		}
		if(isOverClear(mouseX-guiLeft, mouseY-guiTop)) {
			renderTooltip(I18n.format("misc.refinedstorage.clear"), mouseX-guiLeft, mouseY-guiTop);
		}
		if(isOverCreatePattern(mouseX-guiLeft, mouseY-guiTop)) {
			renderTooltip(I18n.format("gui.refinedstorage.grid.pattern_create"), mouseX-guiLeft, mouseY-guiTop);
		}
	}

	@Override
	protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
		boolean valid = type != ClickType.QUICK_MOVE && minecraft.player.inventory.getItemStack().isEmpty();
		if(valid && slot != null && slot.isEnabled()) {
			if(slot instanceof FalseCopySlot) {
				FalseCopySlot copySlot = (FalseCopySlot)slot;
				if(!copySlot.getStack().isEmpty()) {
					if(copySlot.allowAlternatives() && hasControlDown()) {
						minecraft.displayGuiScreen(new AlternativesScreen(
								this,
								minecraft.player.inventory,
								new TranslationTextComponent("gui.refinedstorage.alternatives"),
								container.tile,
								copySlot.getStack(),
								copySlot.getSlotIndex()
								));
					}
					else {
						minecraft.displayGuiScreen(new ItemAmountScreen(
								this,
								minecraft.player.inventory,
								copySlot.slotNumber,
								copySlot.getStack(),
								copySlot.getSlotStackLimit(),
								copySlot.allowAlternatives() ? parent->new AlternativesScreen(
										parent,
										minecraft.player.inventory,
										new TranslationTextComponent("gui.refinedstorage.alternatives"),
										container.tile,
										copySlot.getStack(),
										copySlot.getSlotIndex()
										) : null
								));
					}
				}
				return;
			}
			else if(slot instanceof FalseCopyFluidSlot) {
				FalseCopyFluidSlot copySlot = (FalseCopyFluidSlot)slot;
				FluidStack stack = copySlot.fluidInventory.getStackInSlot(copySlot.getSlotIndex());
				if(!stack.isEmpty()) {
					if(copySlot.allowAlternatives() && hasControlDown()) {
						minecraft.displayGuiScreen(new AlternativesScreen(
								this,
								minecraft.player.inventory,
								new TranslationTextComponent("gui.refinedstorage.alternatives"),
								container.tile,
								stack,
								copySlot.getSlotIndex()
								));
					}
					else {
						minecraft.displayGuiScreen(new FluidAmountScreen(
								(BaseScreen)minecraft.currentScreen,
								minecraft.player.inventory,
								copySlot.slotNumber,
								stack,
								64000,
								copySlot.allowAlternatives() ? parent->new AlternativesScreen(
										parent,
										minecraft.player.inventory,
										new TranslationTextComponent("gui.refinedstorage.alternatives"),
										container.tile,
										stack,
										copySlot.getSlotIndex()
										) : null
								));
					}
				}
				return;
			}
		}
		super.handleMouseClick(slot, slotId, mouseButton, type);
	}

	@Override
	public void init() {
		buttons.clear();
		super.init();
		addButton(new ProcessingTypeButton(guiLeft-20, guiTop, button->{
			int value = container.tile.processingType == 0 ? 1 : 0;
			PacketHandler.INSTANCE.sendToServer(new SetProcessingTypePacket((byte)0));
			container.tile.processingType = value;
		}));
		if(ModList.get().isLoaded("jei")) {
			addButton(new ShowJEIRecipesButton(guiLeft+172, guiTop+128, button->{
				RSLargePatternsJEIPlugin.showAllCategories();
			}));
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean clickedClear = mouseButton == 0 && isOverClear(mouseX-guiLeft, mouseY-guiTop);
		boolean clickedCreatePattern = mouseButton == 0 && isOverCreatePattern(mouseX-guiLeft, mouseY-guiTop);
		if(clickedCreatePattern) {
			PacketHandler.INSTANCE.sendToServer(new CreatePatternPacket());
			minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		}
		if(clickedClear) {
			PacketHandler.INSTANCE.sendToServer(new ClearPatternPacket());
			minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public void renderQuantity(int x, int y, String qty, int color) {
		boolean large = minecraft.getForceUnicodeFont() || RS.CLIENT_CONFIG.getGrid().getLargeFont();
		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y, 300F);
		if(!large) {
			RenderSystem.scalef(0.5F, 0.5F, 1.0F);
		}
		font.drawStringWithShadow(qty, (large ? 16 : 30)-font.getStringWidth(qty), large ? 8F : 22F, color);
		RenderSystem.popMatrix();
	}

	class ProcessingTypeButton extends Button {

		public ProcessingTypeButton(int x, int y, Button.IPressable onPress) {
			super(x, y, 18, 18, "", onPress);
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float partialTicks) {
			RenderSystem.color4f(1, 1, 1, 1);
			RenderSystem.enableAlphaTest();
			isHovered = inBounds(x, y, width, height, mouseX, mouseY);
			minecraft.getTextureManager().bindTexture(RS_ICONS);
			blit(x, y, 238, isHovered ? 35 : 16, 18, 18);
			blit(x+1, y+1, container.tile.processingType*16, 128, 16, 16);
			if(isHovered) {
				RenderSystem.enableBlend();
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				RenderSystem.color4f(1, 1, 1, 0.5F);
				blit(x, y, 238, 54, 18, 18);
				RenderSystem.disableBlend();
			}
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			renderTooltip(Arrays.asList(I18n.format("sidebutton.refinedstorage.type"), TextFormatting.GRAY+I18n.format("sidebutton.refinedstorage.type."+container.tile.processingType)), mouseX, mouseY);
		}
	}

	class ShowJEIRecipesButton extends Button {

		ShowJEIRecipesButton(int x, int y, Button.IPressable onPress) {
			super(x, y, 22, 16, "", onPress);
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float partialTicks) {
			if(visible) {
				isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			}
		}

		@Override
		public void renderToolTip(int mouseX, int mouseY) {
			renderTooltip(I18n.format("jei.tooltip.show.recipes"), mouseX, mouseY);
		}
	}
}
