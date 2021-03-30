package thelm.rslargepatterns.network.packet;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;
import thelm.rslargepatterns.inventory.BaseFluidInventory;
import thelm.rslargepatterns.slot.FalseCopyFluidSlot;

public class FluidSlotUpdatePacket {

	private short containerSlot;
	private FluidStack stack;

	public FluidSlotUpdatePacket(short containerSlot, FluidStack stack) {
		this.containerSlot = containerSlot;
		this.stack = stack;
	}

	public static void encode(FluidSlotUpdatePacket pkt, PacketBuffer buf) {
		buf.writeShort(pkt.containerSlot);
		buf.writeFluidStack(pkt.stack);
	}

	public static FluidSlotUpdatePacket decode(PacketBuffer buf) {
		return new FluidSlotUpdatePacket(buf.readShort(), buf.readFluidStack());
	}

	public static void handle(FluidSlotUpdatePacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Container container = Minecraft.getInstance().player.openContainer;
			if(container != null) {
				if(pkt.containerSlot >= 0 && pkt.containerSlot < container.inventorySlots.size()) {
					Slot slot = container.getSlot(pkt.containerSlot);
					if(slot instanceof FalseCopyFluidSlot) {
						BaseFluidInventory inventory = ((FalseCopyFluidSlot)slot).fluidInventory;
						inventory.setStackInSlot(slot.getSlotIndex(), pkt.stack);
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
