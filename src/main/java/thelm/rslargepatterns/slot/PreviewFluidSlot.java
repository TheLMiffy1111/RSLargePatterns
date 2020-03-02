package thelm.rslargepatterns.slot;

import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.inventory.BaseFluidInventory;

public class PreviewFluidSlot extends FalseCopyFluidSlot {

	public PreviewFluidSlot(BaseFluidInventory fluidInventory, int index, int x, int y) {
		super(fluidInventory, index, x, y, false);
	}

	@Override
	public void putStack(ItemStack stack) {

	}
}
