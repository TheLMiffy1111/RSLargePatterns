package thelm.rslargepatterns.container;

import com.refinedmods.refinedstorage.tile.config.IType;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import thelm.rslargepatterns.container.factory.PositionalTileContainerFactory;
import thelm.rslargepatterns.slot.FalseCopyFluidSlot;
import thelm.rslargepatterns.slot.FalseCopySlot;
import thelm.rslargepatterns.tile.LargePatternEncoderTile;

public class LargePatternEncoderContainer extends BaseContainer {

	public static final ContainerType<LargePatternEncoderContainer> TYPE_INSTANCE = (ContainerType<LargePatternEncoderContainer>)IForgeContainerType.
			create(new PositionalTileContainerFactory<>(LargePatternEncoderContainer::new)).
			setRegistryName("rslargepatterns:large_pattern_encoder");

	public final LargePatternEncoderTile tile;
	public final IItemHandler itemHandler;

	public LargePatternEncoderContainer(int windowId, PlayerInventory playerInventory, LargePatternEncoderTile tile) {
		super(TYPE_INSTANCE, windowId, playerInventory);
		this.tile = tile;
		this.itemHandler = tile.getPatterns();
		setupSlots();
	}

	public void setupSlots() {
		inventorySlots.clear();
		addSlot(new SlotItemHandler(tile.patterns, 0, 216, 38));
		addSlot(new SlotItemHandler(tile.patterns, 1, 216, 74));
		int ox = 8;
		int x = ox;
		int y = 20;
		for(int i = 0; i < 9; ++i) {
			for(int j = 0; j < 9; ++j) {
				addSlot(new FalseCopySlot(tile.processingMatrix, i*9+j, 8+j*18, 20+i*18, true).setEnableHandler(()->tile.processingType == IType.ITEMS));
				addSlot(new FalseCopyFluidSlot(tile.processingMatrixFluids, i*9+j, 8+j*18, 20+i*18, true).setEnableHandler(()->tile.processingType == IType.FLUIDS));
			}
		}
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				addSlot(new FalseCopySlot(tile.processingMatrix, 81+i*3+j, 198+j*18, 110+i*18, false).setEnableHandler(()->tile.processingType == IType.ITEMS));
				addSlot(new FalseCopyFluidSlot(tile.processingMatrixFluids, 81+i*3+j, 198+j*18, 110+i*18, false).setEnableHandler(()->tile.processingType == IType.FLUIDS));
			}
		}
		setupPlayerInventory();
	}

	public int getPlayerInvY() {
		return 195;
	}

	public int getPlayerInvX() {
		return 49;
	}

	public int getSizeInventory() {
		return itemHandler.getSlots();
	}

	public static ItemStack cloneStack(ItemStack stack, int stackSize) {
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack retStack = stack.copy();
		retStack.setCount(stackSize);
		return retStack;
	}
}
