package thelm.rslargepatterns.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.inventory.BaseFluidInventory;
import thelm.rslargepatterns.slot.PreviewFluidSlot;

public class FluidAmountContainer extends BaseContainer {

	public FluidAmountContainer(PlayerInventory playerInventory, FluidStack stack) {
		super(null, 0, playerInventory);
		BaseFluidInventory inventory = new BaseFluidInventory(null, 1);
		inventory.setStackInSlot(0, stack);
		addSlot(new PreviewFluidSlot(inventory, 0, 89, 48));
	}

	@Override
	public int getPlayerInvX() {
		return 0;
	}

	@Override
	public int getPlayerInvY() {
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}
}
