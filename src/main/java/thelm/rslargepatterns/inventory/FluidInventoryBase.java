package thelm.rslargepatterns.inventory;

import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

public class FluidInventoryBase {

	public final FluidStack[] stacks;
	public final TileEntity tile;

	public FluidInventoryBase(TileEntity tile, int size) {
		this.tile = tile;
		this.stacks = new FluidStack[size];
	}

	public int getSlots() {
		return stacks.length;
	}

	public boolean isEmpty() {
		for(FluidStack stack : stacks) {
			if(stack != null && stack.amount != 0) {
				return false;
			}
		}
		return true;
	}

	public FluidStack getStackInSlot(int index) {
		return index >= 0 && index < stacks.length ? stacks[index] : null;
	}

	public void setStackInSlot(int index, FluidStack stack) {
		if(index >= 0 && index < stacks.length) {
			if(stack == null || stack.amount == 0) {
				stack = null;
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

	public void readFromNBT(NBTTagCompound nbt) {
		Arrays.fill(stacks, null);
		NBTTagList tagList = nbt.getTagList("Items", 10);
		for(int i = 0; i < tagList.tagCount(); ++i) {
			NBTTagCompound tag = tagList.getCompoundTagAt(i);
			int j = tag.getByte("Slot") & 255;
			if(j >= 0 && j < stacks.length) {
				stacks[j] = FluidStack.loadFluidStackFromNBT(tag);
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList tagList = new NBTTagList();
		for(int i = 0; i < stacks.length; ++i) {
			FluidStack stack = stacks[i];
			if(stack != null && stack.amount != 0) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)i);
				stack.writeToNBT(tag);
				tagList.appendTag(tag);
			}
		}
		nbt.setTag("Items", tagList);
		return nbt;
	}
}
