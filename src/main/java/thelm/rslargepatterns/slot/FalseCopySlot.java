package thelm.rslargepatterns.slot;

import java.util.function.BooleanSupplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class FalseCopySlot extends SlotItemHandler {

	private BooleanSupplier enableHandler = ()->true;
	public int slotIndex;
	private boolean allowAlternatives = false;

	public FalseCopySlot(IItemHandler itemHandler, int index, int x, int y, boolean allowAlternatives) {
		super(itemHandler, index, x, y);
		slotIndex = index;
		this.allowAlternatives = allowAlternatives;
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		return false;
	}

	public FalseCopySlot setEnableHandler(BooleanSupplier enableHandler) {
		this.enableHandler = enableHandler;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enableHandler.getAsBoolean();
	}

	public boolean allowAlternatives() {
		return allowAlternatives;
	}
}
