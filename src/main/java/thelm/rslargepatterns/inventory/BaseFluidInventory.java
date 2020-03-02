package thelm.rslargepatterns.inventory;

import java.util.Arrays;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

public class BaseFluidInventory {

	public final FluidStack[] stacks;
	public final TileEntity tile;

	public BaseFluidInventory(TileEntity tile, int size) {
		this.tile = tile;
		this.stacks = new FluidStack[size];
		Arrays.fill(stacks, FluidStack.EMPTY);
	}

	public int getSlots() {
		return stacks.length;
	}

	public boolean isEmpty() {
		for(FluidStack stack : stacks) {
			if(!stack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public FluidStack getStackInSlot(int index) {
		return index >= 0 && index < stacks.length ? stacks[index] : FluidStack.EMPTY;
	}

	public void setStackInSlot(int index, FluidStack stack) {
		if(index >= 0 && index < stacks.length) {
			if(stack.isEmpty()) {
				stack = FluidStack.EMPTY;
			}
			stacks[index] = stack;
		}
		markDirty();
	}

	public void markDirty() {
		if(tile != null) {
			tile.markDirty();
		}
	}

	public void read(CompoundNBT nbt) {
		Arrays.fill(stacks, FluidStack.EMPTY);
		ListNBT tagList = nbt.getList("Items", 10);
		for(int i = 0; i < tagList.size(); ++i) {
			CompoundNBT tag = tagList.getCompound(i);
			int j = tag.getByte("Slot") & 255;
			if(j >= 0 && j < stacks.length) {
				stacks[j] = FluidStack.loadFluidStackFromNBT(tag);
			}
		}
	}

	public CompoundNBT write(CompoundNBT nbt) {
		ListNBT tagList = new ListNBT();
		for(int i = 0; i < stacks.length; ++i) {
			FluidStack stack = stacks[i];
			if(!stack.isEmpty()) {
				CompoundNBT tag = new CompoundNBT();
				tag.putByte("Slot", (byte)i);
				stack.writeToNBT(tag);
				tagList.add(tag);
			}
		}
		nbt.put("Items", tagList);
		return nbt;
	}
}
