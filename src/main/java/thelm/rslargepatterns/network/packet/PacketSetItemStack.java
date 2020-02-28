package thelm.rslargepatterns.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemStackHandler;
import thelm.rslargepatterns.network.ISelfHandleMessage;
import thelm.rslargepatterns.slot.SlotItemFalseCopy;

public class PacketSetItemStack implements ISelfHandleMessage<IMessage> {

	private short containerSlot;
	private ItemStack stack;

	public PacketSetItemStack() {}

	public PacketSetItemStack(short containerSlot, ItemStack stack) {
		this.containerSlot = containerSlot;
		this.stack = stack;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(containerSlot);
		ByteBufUtils.writeItemStack(buf, stack);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		containerSlot = buf.readShort();
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public IMessage onMessage(MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		world.addScheduledTask(()->{
			Container container = player.openContainer;
			if(container != null) {
				if(containerSlot >= 0 && containerSlot < container.inventorySlots.size()) {
					Slot slot = container.getSlot(containerSlot);
					if(slot instanceof SlotItemFalseCopy) {
						ItemStackHandler handler = (ItemStackHandler)((SlotItemFalseCopy)slot).getItemHandler();
						handler.setStackInSlot(slot.getSlotIndex(), stack);
					}
				}
			}
		});
		return null;
	}
}
