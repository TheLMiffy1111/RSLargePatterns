package thelm.rslargepatterns.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.inventory.FluidInventoryBase;
import thelm.rslargepatterns.slot.SlotFluidPreview;

public class ContainerFluidAmount extends ContainerBase {

	public ContainerFluidAmount(InventoryPlayer playerInventory, FluidStack stack) {
		super(playerInventory, null);
		FluidInventoryBase inventory = new FluidInventoryBase(null, 1);
		inventory.setStackInSlot(0, stack);
		addSlotToContainer(new SlotFluidPreview(inventory, 0, 89, 48));
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
