package thelm.rslargepatterns.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotPreview extends SlotItemHandler {

	public SlotPreview(IItemHandler inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
