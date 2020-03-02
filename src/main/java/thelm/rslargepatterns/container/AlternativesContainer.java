package thelm.rslargepatterns.container;

import net.minecraft.entity.player.PlayerInventory;

public class AlternativesContainer extends BaseContainer {

	public AlternativesContainer(PlayerInventory playerInventory) {
		super(null, 0, playerInventory);
	}

	@Override
	public int getPlayerInvY() {
		return 0;
	}

	@Override
	public int getPlayerInvX() {
		return 0;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}
}
