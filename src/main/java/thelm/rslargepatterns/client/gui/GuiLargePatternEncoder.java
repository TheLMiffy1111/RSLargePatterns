package thelm.rslargepatterns.client.gui;

import java.io.IOException;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.Loader;
import thelm.rslargepatterns.container.ContainerLargePatternEncoder;
import thelm.rslargepatterns.integration.jei.RSLargePatternsJEIPlugin;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.PacketClearPattern;
import thelm.rslargepatterns.network.packet.PacketCreatePattern;
import thelm.rslargepatterns.network.packet.PacketSetOredictPattern;
import thelm.rslargepatterns.network.packet.PacketSetProcessingType;
import thelm.rslargepatterns.slot.SlotFluidFalseCopy;
import thelm.rslargepatterns.slot.SlotItemFalseCopy;

public class GuiLargePatternEncoder extends GuiBase {

	public static final RenderUtils.FluidRenderer FLUID_RENDERER = new RenderUtils.FluidRenderer(-1, 16, 16);
	public static final ResourceLocation BACKGROUND = new ResourceLocation("rslargepatterns:textures/gui/large_pattern_encoder.png");
	public static final ResourceLocation RS_ICONS = new ResourceLocation("refinedstorage:textures/icons.png");
	public final ContainerLargePatternEncoder container;
	private String hoveringFluid = null;
	private GuiButtonProcessingType processingType; 
	private GuiCheckBox oredictPattern;

	public GuiLargePatternEncoder(ContainerLargePatternEncoder container) {
		super(container);
		this.container = container;
		xSize = 258;
		ySize = 277;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	private boolean isOverClear(int mouseX, int mouseY) {
		return inBounds(172, 19, 7, 7, mouseX, mouseY);
	}

	private boolean isOverCreatePattern(int mouseX, int mouseY) {
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
		drawModalRectWithCustomSizedTexture(guiLeft+216, guiTop+56, 258, 16*ty, 16, 16, 512, 512);
		hoveringFluid = null;
		for(int i = 0; i < inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = inventorySlots.inventorySlots.get(i);
			if(slot.isEnabled() && slot instanceof SlotFluidFalseCopy) {
				FluidStack stack = ((SlotFluidFalseCopy)slot).fluidInventory.getStackInSlot(slot.getSlotIndex());
				if(stack != null) {
					FLUID_RENDERER.draw(mc, guiLeft+slot.xPos, guiTop+slot.yPos, stack);
					drawQuantity(guiLeft+slot.xPos, guiTop+slot.yPos, API.instance().getQuantityFormatter().formatInBucketForm(stack.amount));
					GlStateManager.disableLighting();
					if(inBounds(guiLeft+slot.xPos, guiTop+slot.yPos, 16, 16, mouseX, mouseY)) {
						hoveringFluid = stack.getLocalizedName();
					}
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String s = I18n.translateToLocal("tile.rslargepatterns.large_pattern_encoder.name");
		fontRenderer.drawString(s, xSize/2 - fontRenderer.getStringWidth(s)/2, 6, 0x404040);
		fontRenderer.drawString(container.playerInventory.getDisplayName().getUnformattedText(), container.getPlayerInvX(), container.getPlayerInvY()-11, 0x404040);
		if(hoveringFluid != null) {
			drawHoveringText(hoveringFluid, mouseX-guiLeft, mouseY-guiTop);
		}
		for(GuiButton guibutton : buttonList) {
			if(guibutton.isMouseOver()) {
				guibutton.drawButtonForegroundLayer(mouseX-guiLeft, mouseY-guiTop);
				break;
			}
		}
		if(isOverClear(mouseX-guiLeft, mouseY-guiTop)) {
			drawHoveringText(I18n.translateToLocal("misc.refinedstorage:clear"), mouseX-guiLeft, mouseY-guiTop);
		}
		if(isOverCreatePattern(mouseX-guiLeft, mouseY-guiTop)) {
			drawHoveringText(I18n.translateToLocal("gui.refinedstorage:grid.pattern_create"), mouseX-guiLeft, mouseY-guiTop);
		}
	}

	@Override
	protected void handleMouseClick(Slot slot, int slotId, int mouseButton, ClickType type) {
		boolean valid = type != ClickType.QUICK_MOVE && Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty();
		if(valid && slot != null && slot.isEnabled()) {
			if(slot instanceof SlotItemFalseCopy) {
				if(!slot.getStack().isEmpty()) {
					FMLClientHandler.instance().showGuiScreen(new GuiItemAmount(
							(GuiBase)Minecraft.getMinecraft().currentScreen,
							Minecraft.getMinecraft().player.inventory,
							slot.slotNumber,
							slot.getStack(),
							slot.getSlotStackLimit()
							));
				}
				return;
			}
			else if(slot instanceof SlotFluidFalseCopy) {
				FluidStack stack = ((SlotFluidFalseCopy)slot).fluidInventory.getStackInSlot(slot.getSlotIndex());
				if(stack != null) {
					FMLClientHandler.instance().showGuiScreen(new GuiFluidAmount(
							(GuiBase)Minecraft.getMinecraft().currentScreen,
							Minecraft.getMinecraft().player.inventory,
							slot.slotNumber,
							stack,
							64000
							));
					return;
				}
			}
		}
		super.handleMouseClick(slot, slotId, mouseButton, type);
	}

	@Override
	public void initGui() {
		buttonList.clear();
		super.initGui();
		processingType = addButton(new GuiButtonProcessingType(0, guiLeft-20, guiTop));
		oredictPattern = addCheckBox(guiLeft+171, guiTop+168, I18n.translateToLocal("misc.refinedstorage:oredict"), container.tile.oredictPattern);
		if(Loader.isModLoaded("jei")) {
			addButton(new GuiButtonShowRecipesJEI(0, guiLeft+172, guiTop+128));
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if(button == processingType) {
			int value = container.tile.processingType == 0 ? 1 : 0;
			PacketHandler.INSTANCE.sendToServer(new PacketSetProcessingType((byte)value));
			container.tile.processingType = value;
		}
		if(button == oredictPattern) {
			PacketHandler.INSTANCE.sendToServer(new PacketSetOredictPattern(oredictPattern.isChecked()));
			container.tile.oredictPattern = oredictPattern.isChecked();
		}
		if(Loader.isModLoaded("jei") && button instanceof GuiButtonShowRecipesJEI) {
			RSLargePatternsJEIPlugin.showAllCategories();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		boolean clickedClear = mouseButton == 0 && isOverClear(mouseX-guiLeft, mouseY-guiTop);
		boolean clickedCreatePattern = mouseButton == 0 && isOverCreatePattern(mouseX-guiLeft, mouseY-guiTop);

		if(clickedCreatePattern) {
			PacketHandler.INSTANCE.sendToServer(new PacketCreatePattern());
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
		if(clickedClear) {
			PacketHandler.INSTANCE.sendToServer(new PacketClearPattern());
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		}
	}

	public void drawQuantity(int x, int y, String qty) {
		boolean large = fontRenderer.getUnicodeFlag() || RS.INSTANCE.config.largeFont;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 1);
		if(!large) {
			GlStateManager.scale(0.5F, 0.5F, 1);
		}
		GlStateManager.disableLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableDepth();
		fontRenderer.drawStringWithShadow(qty, (large ? 16 : 30) - fontRenderer.getStringWidth(qty), large ? 8 : 22, 0xFFFFFF);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.depthMask(true);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	class GuiButtonProcessingType extends GuiButton {

		public GuiButtonProcessingType(int buttonId, int x, int y) {
			super(buttonId, x, y, 18, 18, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableAlpha();
			hovered = inBounds(x, y, width, height, mouseX, mouseY);
			mc.getTextureManager().bindTexture(RS_ICONS);
			drawTexturedModalRect(x, y, 238, hovered ? 35 : 16, 18, 18);
			drawTexturedModalRect(x+1, y+1, container.tile.processingType*16, 128, 16, 16);
			if(hovered) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1, 1, 1, 0.5F);
				drawTexturedModalRect(x, y, 238, 54, 18, 18);
				GlStateManager.disableBlend();
			}
		}

		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {
			drawHoveringText(Arrays.asList(I18n.translateToLocal("sidebutton.refinedstorage:type"), TextFormatting.GRAY+I18n.translateToLocal("sidebutton.refinedstorage:type."+container.tile.processingType)), mouseX, mouseY);
		}
	}

	class GuiButtonShowRecipesJEI extends GuiButton {

		GuiButtonShowRecipesJEI(int buttonId, int x, int y) {
			super(buttonId, x, y, 22, 16, "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if(visible) {
				hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
				mouseDragged(mc, mouseX, mouseY);
			}
		}

		@Override
		public void drawButtonForegroundLayer(int mouseX, int mouseY) {
			drawHoveringText(I18n.translateToLocal("jei.tooltip.show.recipes"), mouseX, mouseY);
		}
	}
}
