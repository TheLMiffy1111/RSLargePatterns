package thelm.rslargepatterns.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import thelm.rslargepatterns.slot.PreviewSlot;

public class ItemAmountContainer extends BaseContainer {

	public ItemAmountContainer(PlayerInventory playerInventory, ItemStack stack) {
		super(null, 0, playerInventory);
		ItemStackHandler inventory = new ItemStackHandler(1);
		inventory.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(stack, 1));
		addSlot(new PreviewSlot(inventory, 0, 89, 48));
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
