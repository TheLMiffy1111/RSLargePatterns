package thelm.rslargepatterns.client.gui;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import com.google.common.primitives.Ints;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;

public abstract class GuiAmount extends GuiBase {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("refinedstorage:textures/gui/crafting_settings.png");

	protected GuiTextField amountField;

	private GuiBase parent;

	protected GuiButton okButton;
	private GuiButton cancelButton;

	private GuiButton[] incrementButtons = new GuiButton[6];

	public GuiAmount(GuiBase parent, Container container) {
		super(container);
		xSize = 172;
		ySize = 99;
		this.parent = parent;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	protected abstract String getTitle();

	protected abstract int[] getIncrements();

	protected abstract int getMaxAmount();

	protected abstract int getDefaultAmount();

	protected Pair<Integer, Integer> getAmountPos() {
		return Pair.of(9, 51);
	}

	protected Pair<Integer, Integer> getOkCancelPos() {
		return Pair.of(114, 33);
	}

	@Override
	public void initGui() {
		super.initGui();
		Pair<Integer, Integer> pos = getOkCancelPos();

		okButton = addButton(guiLeft+pos.getLeft(), guiTop+pos.getRight(), 50, 20, I18n.translateToLocal("misc.refinedstorage:set"));
		cancelButton = addButton(guiLeft+pos.getLeft(), guiTop+pos.getRight() + 24, 50, 20, I18n.translateToLocal("gui.cancel"));

		amountField = new GuiTextField(0, fontRenderer, guiLeft+getAmountPos().getLeft(), guiTop+getAmountPos().getRight(), 69-6, fontRenderer.FONT_HEIGHT);
		amountField.setEnableBackgroundDrawing(false);
		amountField.setVisible(true);
		amountField.setText(String.valueOf(getDefaultAmount()));
		amountField.setTextColor(0xFFFFFF);
		amountField.setCanLoseFocus(false);
		amountField.setFocused(true);

		int[] increments = getIncrements();

		int xx = 7;
		int width = 30;
		for(int i = 0; i < 3; ++i) {
			String text = "+"+increments[i];
			if(text.equals("+1000")) {
				text = "+1B";
			}
			incrementButtons[i] = addButton(guiLeft+xx, guiTop+20, width, 20, text);
			xx += width+3;
		}

		xx = 7;
		for(int i = 0; i < 3; ++i) {
			String text = "-"+increments[i];
			if(text.equals("-1000")) {
				text = "-1B";
			}
			incrementButtons[3+i] = addButton(guiLeft+xx, guiTop+ySize-20-7, width, 20, text);
			xx += width+3;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		amountField.drawTextBox();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.disableLighting();
		fontRenderer.drawString(getTitle(), 7, 7, 0x404040);
		GlStateManager.enableLighting();
	}

	@Override
	protected void keyTyped(char character, int keyCode) throws IOException {
		if(!checkHotbarKeys(keyCode) && amountField.textboxKeyTyped(character, keyCode)) {
			// NO OP
		}
		else {
			if(keyCode == Keyboard.KEY_RETURN) {
				onOkButtonPressed(isShiftKeyDown());
			}
			else if(keyCode == Keyboard.KEY_ESCAPE) {
				close();
			}
			else {
				super.keyTyped(character, keyCode);
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button.id == okButton.id) {
			onOkButtonPressed(isShiftKeyDown());
		}
		else if(button.id == cancelButton.id) {
			close();
		}
		else {
			for(GuiButton incrementButton : incrementButtons) {
				if(incrementButton.id == button.id) {
					Integer oldAmount = Ints.tryParse(amountField.getText());
					if(oldAmount == null) {
						oldAmount = 0;
					}
					String incrementButtonText = incrementButton.displayString;
					if(incrementButtonText.equals("+1B")) {
						incrementButtonText = "1000";
					}
					else if(incrementButtonText.equals("-1B")) {
						incrementButtonText = "-1000";
					}
					int newAmount = Integer.parseInt(incrementButtonText);
					newAmount = MathHelper.clamp(oldAmount+newAmount, 0, getMaxAmount());
					amountField.setText(String.valueOf(newAmount));
					break;
				}
			}
		}
	}

	protected abstract void onOkButtonPressed(boolean shiftDown);

	public void close() {
		FMLClientHandler.instance().showGuiScreen(parent);
	}

	public GuiBase getParent() {
		return parent;
	}
}
