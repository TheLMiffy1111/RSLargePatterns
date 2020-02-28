package thelm.rslargepatterns.client.gui;

import com.google.common.primitives.Ints;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.rslargepatterns.container.ContainerItemAmount;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.PacketSetItemStack;

public class GuiItemAmount extends GuiAmount {

	private int containerSlot;
	private ItemStack stack;
	private int maxAmount;

	public GuiItemAmount(GuiBase parent, InventoryPlayer playerInventory, int containerSlot, ItemStack stack, int maxAmount) {
		super(parent, new ContainerItemAmount(playerInventory, stack));
		this.containerSlot = containerSlot;
		this.stack = stack;
		this.maxAmount = maxAmount;
	}

	@Override
	protected String getTitle() {
		return I18n.translateToLocal("gui.refinedstorage:item_amount");
	}

	@Override
	protected int[] getIncrements() {
		return new int[] {
				1, 10, 64,
				-1, -10, -64
		};
	}

	@Override
	protected int getMaxAmount() {
		return maxAmount;
	}

	@Override
	protected int getDefaultAmount() {
		return stack.getCount();
	}

	@Override
	protected void onOkButtonPressed(boolean shiftDown) {
        Integer amount = Ints.tryParse(amountField.getText());
        if(amount != null) {
        	PacketHandler.INSTANCE.sendToServer(new PacketSetItemStack((short)containerSlot, ItemHandlerHelper.copyStackWithSize(stack, amount)));
            close();
        }
	}
}
