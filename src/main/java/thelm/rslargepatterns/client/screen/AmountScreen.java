package thelm.rslargepatterns.client.screen;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;

import com.raoulvdberge.refinedstorage.render.RenderSettings;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public abstract class AmountScreen<T extends Container> extends BaseScreen<T> {

	private BaseScreen<?> parent;

	protected TextFieldWidget amountField;
	protected Button okButton;
	protected Button cancelButton;

	public AmountScreen(BaseScreen<?> parent, T container, PlayerInventory inventory, ITextComponent title) {
		super(container, inventory, title);
		this.parent = parent;
	}

	protected abstract int[] getIncrements();

	protected abstract int getMaxAmount();

	protected abstract int getDefaultAmount();

	protected Pair<Integer, Integer> getAmountPos() {
		return Pair.of(9, 51);
	}

	protected abstract Pair<Integer, Integer> getOkCancelPos();

	protected abstract int getOkCancelButtonWidth();

	@Override
	public void init() {
		super.init();

		Pair<Integer, Integer> pos = getOkCancelPos();

		okButton = addButton(guiLeft+pos.getLeft(), guiTop+pos.getRight(), getOkCancelButtonWidth(), 20, I18n.format("misc.refinedstorage.set"), btn->onOkButtonPressed(hasShiftDown()));
		cancelButton = addButton(guiLeft+pos.getLeft(), guiTop+pos.getRight()+24, getOkCancelButtonWidth(), 20, I18n.format("gui.cancel"), btn->close());

		amountField = new TextFieldWidget(font, guiLeft+getAmountPos().getLeft(), guiTop+getAmountPos().getRight(), 69 - 6, font.FONT_HEIGHT, "");
		amountField.setEnableBackgroundDrawing(false);
		amountField.setVisible(true);
		amountField.setText(String.valueOf(getDefaultAmount()));
		amountField.setTextColor(RenderSettings.INSTANCE.getSecondaryColor());
		amountField.setCanLoseFocus(false);
		amountField.changeFocus(true);

		addButton(amountField);

		setFocused(amountField);

		int[] increments = getIncrements();

		int xx = 7;
		int width = 30;
		for(int i = 0; i < 3; ++i) {
			int increment = increments[i];
			String text = "+" + increment;
			if(text.equals("+1000")) {
				text = "+1B";
			}
			addButton(guiLeft+xx, guiTop+20, width, 20, text, btn->onIncrementButtonClicked(increment));
			xx += width+3;
		}

		xx = 7;
		for(int i = 0; i < 3; ++i) {
			int increment = increments[i];
			String text = "-" + increment;
			if(text.equals("-1000")) {
				text = "-1B";
			}
			addButton(guiLeft+xx, guiTop+ySize - 20 - 7, width, 20, text, btn->onIncrementButtonClicked(-increment));
			xx += width+3;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		amountField.renderButton(0, 0, 0F);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		font.drawString(getTitle().getFormattedText(), 7, 7, 0x404040);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if(key == GLFW.GLFW_KEY_ESCAPE) {
			close();
			return true;
		}
		if((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) && amountField.isFocused()) {
			onOkButtonPressed(hasShiftDown());
			return true;
		}
		if(amountField.keyPressed(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	private void onIncrementButtonClicked(int increment) {
		int oldAmount = 0;
		try {
			oldAmount = Integer.parseInt(amountField.getText());
		}
		catch(NumberFormatException e) {
			// NO OP
		}
		int newAmount = MathHelper.clamp(oldAmount+increment, 0, getMaxAmount());
		amountField.setText(String.valueOf(newAmount));
	}

	protected abstract void onOkButtonPressed(boolean shiftDown);

	public void close() {
		minecraft.displayGuiScreen(parent);
	}

	public BaseScreen<?> getParent() {
		return parent;
	}
}
