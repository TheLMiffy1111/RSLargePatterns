package thelm.rslargepatterns.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.tile.LargePatternEncoderTile;

public class LargePatternsItemHandler extends BaseItemHandler {

	public final LargePatternEncoderTile tile;

	public LargePatternsItemHandler(LargePatternEncoderTile tile) {
		super(tile, 2);
		this.tile = tile;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack.getItem() == LargePatternItem.INSTANCE) {
			return super.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@Override
	public void onContentsChanged(int index) {
		tile.markDirty();
		ItemStack stack = getStackInSlot(index);
		if(index == 1 && !stack.isEmpty()) {
			for(int i = 0; i < 81; ++i) {
				tile.processingMatrix.setStackInSlot(i, LargePatternItem.getInputSlot(stack, i));
				tile.processingMatrixFluids.setStackInSlot(i, LargePatternItem.getFluidInputSlot(stack, i));
			}
			for(int i = 0; i < 9; ++i) {
				tile.processingMatrix.setStackInSlot(81+i, LargePatternItem.getOutputSlot(stack, i));
				tile.processingMatrixFluids.setStackInSlot(81+i, LargePatternItem.getFluidOutputSlot(stack, i));
			}
		}
	}
}
