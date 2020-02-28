package thelm.rslargepatterns.client.gui;

import com.google.common.primitives.Ints;
import com.raoulvdberge.refinedstorage.util.StackUtils;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.container.ContainerFluidAmount;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.PacketSetFluidStack;

public class GuiFluidAmount extends GuiAmount {

	private int containerSlot;
	private FluidStack stack;
	private int maxAmount;

	public GuiFluidAmount(GuiBase parent, InventoryPlayer playerInventory, int containerSlot, FluidStack stack, int maxAmount) {
		super(parent, new ContainerFluidAmount(playerInventory, stack));
		this.containerSlot = containerSlot;
		this.stack = stack;
		this.maxAmount = maxAmount;
	}

	@Override
	protected String getTitle() {
		return I18n.translateToLocal("gui.refinedstorage:fluid_amount");
	}

	@Override
	protected int[] getIncrements() {
		return new int[] {
				100, 500, 1000,
				-100, -500, -1000
		};
	}

	@Override
	protected int getMaxAmount() {
		return maxAmount;
	}

	@Override
	protected int getDefaultAmount() {
		return stack.amount;
	}

	@Override
	protected void onOkButtonPressed(boolean shiftDown) {
		Integer amount = Ints.tryParse(amountField.getText());
		if(amount != null) {
			PacketHandler.INSTANCE.sendToServer(new PacketSetFluidStack((short)containerSlot, StackUtils.copy(stack, amount)));
			close();
		}
	}
}
