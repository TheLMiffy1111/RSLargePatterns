package thelm.rslargepatterns.slot;

import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import thelm.rslargepatterns.inventory.FluidInventoryBase;

public class SlotFluidFalseCopy extends SlotItemHandler {

	private BooleanSupplier enableHandler = ()->true;
	public int slotIndex;
	public FluidInventoryBase fluidInventory;

	public SlotFluidFalseCopy(FluidInventoryBase fluidInventory, int index, int x, int y) {
		super(new ItemStackHandler(fluidInventory.getSlots()), index, x, y);
		slotIndex = index;
		this.fluidInventory = fluidInventory;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return false;
	}

	@Override
	public void putStack(ItemStack stack) {
		if(stack.isEmpty()) {
			fluidInventory.setStackInSlot(getSlotIndex(), null);
		}
		else {
			FluidStack fluid = FluidUtil.getFluidContained(stack);
			if(fluid != null) {
				fluidInventory.setStackInSlot(getSlotIndex(), fluid);
			}
		}
	}

	public SlotFluidFalseCopy setEnableHandler(BooleanSupplier enableHandler) {
		this.enableHandler = enableHandler;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enableHandler.getAsBoolean();
	}
}
