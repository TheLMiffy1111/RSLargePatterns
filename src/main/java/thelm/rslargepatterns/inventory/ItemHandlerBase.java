package thelm.rslargepatterns.inventory;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerBase extends ItemStackHandler {

	public final TileEntity tile;

	public ItemHandlerBase(TileEntity tile, int size) {
		super(size);
		this.tile = tile;
	}

	@Override
	protected void onContentsChanged(int slot) {
		tile.markDirty();
	}

	public void readFromNBT(NBTTagCompound nbt) {
		stacks.clear();
		ItemStackHelper.loadAllItems(nbt, stacks);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return ItemStackHelper.saveAllItems(nbt, stacks);
	}
}
