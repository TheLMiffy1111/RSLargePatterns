package thelm.rslargepatterns.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PreviewSlot extends SlotItemHandler {

	public PreviewSlot(IItemHandler inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean canTakeStack(PlayerEntity playerIn) {
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
