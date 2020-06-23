package thelm.rslargepatterns.client.screen;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.refinedmods.refinedstorage.util.StackUtils;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.container.FluidAmountContainer;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.SetFluidStackPacket;

public class FluidAmountScreen extends AmountScreen<FluidAmountContainer> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("refinedstorage:textures/gui/amount_specifying.png");
	public static final ResourceLocation BACKGROUND_WIDE = new ResourceLocation("refinedstorage:textures/gui/amount_specifying_wide.png");

	private int containerSlot;
	private FluidStack stack;
	private int maxAmount;
	private Function<AmountScreen, AlternativesScreen> alternativesScreenFactory;

	public FluidAmountScreen(BaseScreen<?> parent, PlayerInventory playerInventory, int containerSlot, FluidStack stack, int maxAmount, Function<AmountScreen, AlternativesScreen> alternativesScreenFactory) {
		super(parent, new FluidAmountContainer(playerInventory, stack), playerInventory, new TranslationTextComponent("gui.refinedstorage.fluid_amount"));
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
			addButton(guiLeft+114, cancelButton.y+24, getOkCancelButtonWidth(), 20, I18n.format("gui.refinedstorage.alternatives"), btn->{
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
		return stack.getAmount();
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
				100, 500, 1000,
				-100, -500, -1000
		};
	}

	@Override
	protected void onOkButtonPressed(boolean shiftDown) {
		try {
			int amount = Integer.parseInt(amountField.getText());
			PacketHandler.INSTANCE.sendToServer(new SetFluidStackPacket((short)containerSlot, StackUtils.copy(stack, amount)));
			close();
		}
		catch(NumberFormatException e) {
			// NO OP
		}
	}
}
