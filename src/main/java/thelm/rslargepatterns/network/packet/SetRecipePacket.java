package thelm.rslargepatterns.network.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemStackHandler;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;
import thelm.rslargepatterns.inventory.BaseFluidInventory;
import thelm.rslargepatterns.tile.LargePatternEncoderTile;

public class SetRecipePacket {

	private List<ItemStack> itemInputs;
	private List<ItemStack> itemOutputs;
	private List<FluidStack> fluidInputs;
	private List<FluidStack> fluidOutputs;

	public SetRecipePacket(List<ItemStack> itemInputs, List<ItemStack> itemOutputs, List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs) {
		this.itemInputs = itemInputs;
		this.itemOutputs = itemOutputs;
		this.fluidInputs = fluidInputs;
		this.fluidOutputs = fluidOutputs;
	}

	public static void encode(SetRecipePacket pkt, PacketBuffer buf) {
		buf.writeShort(pkt.itemInputs.size());
		for(ItemStack stack : pkt.itemInputs) {
			buf.writeItemStack(stack);
		}
		buf.writeShort(pkt.itemOutputs.size());
		for(ItemStack stack : pkt.itemOutputs) {
			buf.writeItemStack(stack);
		}
		buf.writeShort(pkt.fluidInputs.size());
		for(FluidStack stack : pkt.fluidInputs) {
			buf.writeFluidStack(stack);
		}
		buf.writeShort(pkt.fluidOutputs.size());
		for(FluidStack stack : pkt.fluidOutputs) {
			buf.writeFluidStack(stack);
		}
	}

	public static SetRecipePacket decode(PacketBuffer buf) {
		short size = buf.readShort();
		List<ItemStack> itemInputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			itemInputs.add(buf.readItemStack());
		}
		size = buf.readShort();
		List<ItemStack> itemOutputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			itemOutputs.add(buf.readItemStack());
		}
		size = buf.readShort();
		List<FluidStack> fluidInputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			fluidInputs.add(buf.readFluidStack());
		}
		size = buf.readShort();
		List<FluidStack> fluidOutputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			fluidOutputs.add(buf.readFluidStack());
		}
		return new SetRecipePacket(itemInputs, itemOutputs, fluidInputs, fluidOutputs);
	}

	public static void handle(SetRecipePacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.openContainer instanceof LargePatternEncoderContainer) {
				LargePatternEncoderTile tile = ((LargePatternEncoderContainer)player.openContainer).tile;
				ItemStackHandler handler = tile.processingMatrix;
				BaseFluidInventory handlerFluid = tile.processingMatrixFluids;
				clearInputsAndOutputs(handler);
				clearInputsAndOutputs(handlerFluid);
				setItemInputs(handler, pkt.itemInputs);
				setItemOutputs(handler, pkt.itemOutputs);
				setFluidInputs(handlerFluid, pkt.fluidInputs);
				setFluidOutputs(handlerFluid, pkt.fluidOutputs);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	private static void clearInputsAndOutputs(ItemStackHandler handler) {
		for(int i = 0; i < 90; ++i) {
			handler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	private static void clearInputsAndOutputs(BaseFluidInventory handler) {
		for(int i = 0; i < 90; ++i) {
			handler.setStackInSlot(i, FluidStack.EMPTY);
		}
	}

	private static void setItemInputs(ItemStackHandler handler, Collection<ItemStack> stacks) {
		setItemSlots(handler, stacks, 0, 81);
	}

	private static void setItemOutputs(ItemStackHandler handler, Collection<ItemStack> stacks) {
		setItemSlots(handler, stacks, 81, 90);
	}

	private static void setItemSlots(ItemStackHandler handler, Collection<ItemStack> stacks, int begin, int end) {
		for(ItemStack stack : stacks) {
			handler.setStackInSlot(begin, stack);
			begin++;
			if(begin >= end) {
				break;
			}
		}
	}

	private static void setFluidInputs(BaseFluidInventory inventory, Collection<FluidStack> stacks) {
		setFluidSlots(inventory, stacks, 0, 81);
	}

	private static void setFluidOutputs(BaseFluidInventory inventory, Collection<FluidStack> stacks) {
		setFluidSlots(inventory, stacks, 81, 90);
	}

	private static void setFluidSlots(BaseFluidInventory inventory, Collection<FluidStack> stacks, int begin, int end) {
		for(FluidStack stack : stacks) {
			inventory.setStackInSlot(begin, stack.copy());
			begin++;
			if(begin >= end) {
				break;
			}
		}
	}
}
