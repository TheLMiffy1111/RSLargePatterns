package thelm.rslargepatterns.network.packet;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;
import thelm.rslargepatterns.inventory.BaseFluidInventory;
import thelm.rslargepatterns.slot.FalseCopyFluidSlot;

public class SetFluidStackPacket {

	private short containerSlot;
	private FluidStack stack;

	public SetFluidStackPacket(short containerSlot, FluidStack stack) {
		this.containerSlot = containerSlot;
		this.stack = stack;
	}

	public static void encode(SetFluidStackPacket pkt, PacketBuffer buf) {
		buf.writeShort(pkt.containerSlot);
		buf.writeFluidStack(pkt.stack);
	}

	public static SetFluidStackPacket decode(PacketBuffer buf) {
		return new SetFluidStackPacket(buf.readShort(), buf.readFluidStack());
	}

	public static void handle(SetFluidStackPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			Container container = player.openContainer;
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
