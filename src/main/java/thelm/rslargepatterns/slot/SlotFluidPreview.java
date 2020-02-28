package thelm.rslargepatterns.slot;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.inventory.FluidInventoryBase;

public class SlotFluidPreview extends SlotFluidFalseCopy {

	public SlotFluidPreview(FluidInventoryBase fluidInventory, int index, int x, int y) {
		super(fluidInventory, index, x, y);
	}

	@Override
	public void putStack(ItemStack stack) {

	}
}
