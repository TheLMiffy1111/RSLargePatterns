package thelm.rslargepatterns.slot;

import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import thelm.rslargepatterns.inventory.BaseFluidInventory;

public class FalseCopyFluidSlot extends SlotItemHandler {

	private BooleanSupplier enableHandler = ()->true;
	public int slotIndex;
	public BaseFluidInventory fluidInventory;
	private boolean allowAlternatives;

	public FalseCopyFluidSlot(BaseFluidInventory fluidInventory, int index, int x, int y, boolean allowAlternatives) {
		super(new ItemStackHandler(fluidInventory.getSlots()), index, x, y);
		slotIndex = index;
		this.fluidInventory = fluidInventory;
		this.allowAlternatives = allowAlternatives;
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		return false;
	}

	@Override
	public void putStack(ItemStack stack) {
		if(stack.isEmpty()) {
			fluidInventory.setStackInSlot(getSlotIndex(), FluidStack.EMPTY);
		}
		else {
			FluidStack fluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
			if(!fluid.isEmpty()) {
				fluidInventory.setStackInSlot(getSlotIndex(), fluid);
			}
		}
	}

	public FalseCopyFluidSlot setEnableHandler(BooleanSupplier enableHandler) {
		this.enableHandler = enableHandler;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enableHandler.getAsBoolean();
	}

	public boolean allowAlternatives() {
		return allowAlternatives;
	}
}
