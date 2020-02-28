package thelm.rslargepatterns.inventory;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.item.ItemLargePattern;
import thelm.rslargepatterns.tile.TileLargePatternEncoder;

public class ItemHandlerLargePatterns extends ItemHandlerBase {

	public final TileLargePatternEncoder tile;

	public ItemHandlerLargePatterns(TileLargePatternEncoder tile) {
		super(tile, 2);
		this.tile = tile;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(stack.getItem() == ItemLargePattern.INSTANCE) {
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
				tile.processingMatrix.setStackInSlot(i, ItemLargePattern.getInputSlot(stack, i));
				tile.processingMatrixFluids.setStackInSlot(i, ItemLargePattern.getFluidInputSlot(stack, i));
			}
			for(int i = 0; i < 9; ++i) {
				tile.processingMatrix.setStackInSlot(81+i, ItemLargePattern.getOutputSlot(stack, i));
				tile.processingMatrixFluids.setStackInSlot(81+i, ItemLargePattern.getFluidOutputSlot(stack, i));
			}
		}
	}
}
