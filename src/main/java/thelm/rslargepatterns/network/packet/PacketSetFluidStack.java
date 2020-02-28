package thelm.rslargepatterns.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thelm.rslargepatterns.inventory.FluidInventoryBase;
import thelm.rslargepatterns.network.ISelfHandleMessage;
import thelm.rslargepatterns.slot.SlotFluidFalseCopy;

public class PacketSetFluidStack implements ISelfHandleMessage<IMessage> {

	private short containerSlot;
	private FluidStack stack;

	public PacketSetFluidStack() {}

	public PacketSetFluidStack(short containerSlot, FluidStack stack) {
		this.containerSlot = containerSlot;
		this.stack = stack;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(containerSlot);
		if(stack == null) {
			buf.writeInt(0);
		}
		else {
			buf.writeInt(stack.amount);
			ByteBufUtils.writeUTF8String(buf, stack.getFluid().getName());
			ByteBufUtils.writeTag(buf, stack.tag);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		containerSlot = buf.readShort();
		int amount = buf.readInt();
		if(amount == 0) {
			stack = null;
		}
		else {
			stack = new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), amount, ByteBufUtils.readTag(buf));
		}
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
					if(slot instanceof SlotFluidFalseCopy) {
						FluidInventoryBase inventory = ((SlotFluidFalseCopy)slot).fluidInventory;
						inventory.setStackInSlot(slot.getSlotIndex(), stack);
					}
				}
			}
		});
		return null;
	}
}
