package thelm.rslargepatterns.slot;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.raoulvdberge.refinedstorage.container.slot.SlotBase;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotItemFalseCopy extends SlotItemHandler {

    private BooleanSupplier enableHandler = ()->true;
	public int slotIndex;

	public SlotItemFalseCopy(IItemHandler itemHandler, int index, int x, int y) {
		super(itemHandler, index, x, y);
		slotIndex = index;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return false;
	}

    public SlotItemFalseCopy setEnableHandler(BooleanSupplier enableHandler) {
        this.enableHandler = enableHandler;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enableHandler.getAsBoolean();
    }
}
