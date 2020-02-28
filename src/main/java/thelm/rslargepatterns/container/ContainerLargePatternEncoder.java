package thelm.rslargepatterns.container;

import com.raoulvdberge.refinedstorage.tile.config.IType;

import cofh.core.gui.slot.SlotFalseCopy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import thelm.rslargepatterns.slot.SlotFluidFalseCopy;
import thelm.rslargepatterns.slot.SlotItemFalseCopy;
import thelm.rslargepatterns.tile.TileLargePatternEncoder;

public class ContainerLargePatternEncoder extends ContainerBase {

	public final TileLargePatternEncoder tile;
	public final IItemHandler itemHandler;

	public ContainerLargePatternEncoder(InventoryPlayer playerInventory, TileLargePatternEncoder tile) {
		super(playerInventory, tile);
		this.tile = tile;
		this.itemHandler = tile.getPatterns();
		setupSlots();
	}

	public void setupSlots() {
		inventorySlots.clear();
		inventoryItemStacks.clear();
		addSlotToContainer(new SlotItemHandler(tile.patterns, 0, 216, 38));
		addSlotToContainer(new SlotItemHandler(tile.patterns, 1, 216, 74));
		int ox = 8;
		int x = ox;
		int y = 20;
		for(int i = 0; i < 9; ++i) {
			for(int j = 0; j < 9; ++j) {
				addSlotToContainer(new SlotItemFalseCopy(tile.processingMatrix, i*9+j, 8+j*18, 20+i*18).setEnableHandler(()->tile.processingType == IType.ITEMS));
				addSlotToContainer(new SlotFluidFalseCopy(tile.processingMatrixFluids, i*9+j, 8+j*18, 20+i*18).setEnableHandler(()->tile.processingType == IType.FLUIDS));
			}
		}
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				addSlotToContainer(new SlotItemFalseCopy(tile.processingMatrix, 81+i*3+j, 198+j*18, 110+i*18).setEnableHandler(()->tile.processingType == IType.ITEMS));
				addSlotToContainer(new SlotFluidFalseCopy(tile.processingMatrixFluids, 81+i*3+j, 198+j*18, 110+i*18).setEnableHandler(()->tile.processingType == IType.FLUIDS));
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
