package thelm.rslargepatterns.client.screen;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.rslargepatterns.container.ItemAmountContainer;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.SetItemStackPacket;

public class ItemAmountScreen extends AmountScreen<ItemAmountContainer> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("refinedstorage:textures/gui/amount_specifying.png");
	public static final ResourceLocation BACKGROUND_WIDE = new ResourceLocation("refinedstorage:textures/gui/amount_specifying_wide.png");

	private int containerSlot;
	private ItemStack stack;
	private int maxAmount;
	private Function<AmountScreen, AlternativesScreen> alternativesScreenFactory;

	public ItemAmountScreen(BaseScreen<?> parent, PlayerInventory playerInventory, int containerSlot, ItemStack stack, int maxAmount, Function<AmountScreen, AlternativesScreen> alternativesScreenFactory) {
		super(parent, new ItemAmountContainer(playerInventory, stack), playerInventory, new TranslationTextComponent("gui.refinedstorage.item_amount"));
		xSize = alternativesScreenFactory != null ? 194 : 172;
		ySize = 99;
		this.containerSlot = containerSlot;
		this.stack = stack;
		this.maxAmount = maxAmount;
		this.alternativesScreenFactory = alternativesScreenFactory;
	}

	@Override
	protected int getOkCancelButtonWidth() {
		return alternativesScreenFactory != null ? 75 : 50;
	}

	@Override
	public void init() {
		super.init();
		if(alternativesScreenFactory != null) {
			addButton(guiLeft+114, cancelButton.y+24, getOkCancelButtonWidth(), 20, new TranslationTextComponent("gui.refinedstorage.alternatives"), btn->{
				minecraft.displayGuiScreen(alternativesScreenFactory.apply(this));
			});
		}
	}

	@Override
	protected Pair<Integer, Integer> getOkCancelPos() {
		if(alternativesScreenFactory == null) {
			return Pair.of(114, 33);
		}
		return Pair.of(114, 22);
	}

	@Override
	protected int getDefaultAmount() {
		return stack.getCount();
	}

	@Override
	protected int getMaxAmount() {
		return maxAmount;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return alternativesScreenFactory != null ? BACKGROUND_WIDE : BACKGROUND;
	}

	@Override
	protected int[] getIncrements() {
		return new int[] {
				1, 10, 64,
				-1, -10, -64
		};
	}

	@Override
	protected void onOkButtonPressed(boolean shiftDown) {
		try {
			int amount = Integer.parseInt(amountField.getText());
			PacketHandler.INSTANCE.sendToServer(new SetItemStackPacket((short)containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));
			close();
		}
		catch(NumberFormatException e) {
			// NO OP
		}
	}
}
