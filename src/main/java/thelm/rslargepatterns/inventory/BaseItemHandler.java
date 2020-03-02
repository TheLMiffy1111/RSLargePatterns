package thelm.rslargepatterns.inventory;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class BaseItemHandler extends ItemStackHandler {

	public final TileEntity tile;

	public BaseItemHandler(TileEntity tile, int size) {
		super(size);
		this.tile = tile;
	}

	@Override
	protected void onContentsChanged(int slot) {
		tile.markDirty();
	}

	public void readFromNBT(CompoundNBT nbt) {
		stacks.clear();
		ItemStackHelper.loadAllItems(nbt, stacks);
	}

	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		return ItemStackHelper.saveAllItems(nbt, stacks);
	}
}
