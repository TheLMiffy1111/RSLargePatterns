package thelm.rslargepatterns.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotDisabled;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class ContainerItemAmount extends ContainerBase {

	public ContainerItemAmount(InventoryPlayer playerInventory, ItemStack stack) {
		super(playerInventory, null);
		ItemStackHandler inventory = new ItemStackHandler(1);
		inventory.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(stack, 1));
		addSlotToContainer(new SlotDisabled(inventory, 0, 89, 48));
	}

	@Override
	public int getPlayerInvX() {
		return 0;
	}

	@Override
	public int getPlayerInvY() {
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}
}
