package thelm.rslargepatterns.network.packet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemStackHandler;
import thelm.rslargepatterns.container.ContainerLargePatternEncoder;
import thelm.rslargepatterns.inventory.FluidInventoryBase;
import thelm.rslargepatterns.network.ISelfHandleMessage;
import thelm.rslargepatterns.tile.TileLargePatternEncoder;

public class PacketSetRecipe implements ISelfHandleMessage<IMessage> {

	private List<ItemStack> itemInputs;
	private List<ItemStack> itemOutputs;
	private List<FluidStack> fluidInputs;
	private List<FluidStack> fluidOutputs;

	public PacketSetRecipe() {}

	public PacketSetRecipe(List<ItemStack> itemInputs, List<ItemStack> itemOutputs, List<FluidStack> fluidInputs, List<FluidStack> fluidOutputs) {
		this.itemInputs = itemInputs;
		this.itemOutputs = itemOutputs;
		this.fluidInputs = fluidInputs;
		this.fluidOutputs = fluidOutputs;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(itemInputs.size());
		for(ItemStack stack : itemInputs) {
			ByteBufUtils.writeItemStack(buf, stack);
		}
		buf.writeShort(itemOutputs.size());
		for(ItemStack stack : itemOutputs) {
			ByteBufUtils.writeItemStack(buf, stack);
		}
		buf.writeShort(fluidInputs.size());
		for(FluidStack stack : fluidInputs) {
			if(stack == null) {
				buf.writeInt(0);
			}
			else {
				buf.writeInt(stack.amount);
				ByteBufUtils.writeUTF8String(buf, stack.getFluid().getName());
				ByteBufUtils.writeTag(buf, stack.tag);
			}
		}
		buf.writeShort(fluidOutputs.size());
		for(FluidStack stack : fluidOutputs) {
			if(stack == null) {
				buf.writeInt(0);
			}
			else {
				buf.writeInt(stack.amount);
				ByteBufUtils.writeUTF8String(buf, stack.getFluid().getName());
				ByteBufUtils.writeTag(buf, stack.tag);
			}
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		short size = buf.readShort();
		itemInputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			itemInputs.add(ByteBufUtils.readItemStack(buf));
		}
		size = buf.readShort();
		itemOutputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			itemOutputs.add(ByteBufUtils.readItemStack(buf));
		}
		size = buf.readShort();
		fluidInputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			FluidStack stack;
			int amount = buf.readInt();
			if(amount == 0) {
				stack = null;
			}
			else {
				stack = new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), amount, ByteBufUtils.readTag(buf));
			}
			fluidInputs.add(stack);
		}
		size = buf.readShort();
		fluidOutputs = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			FluidStack stack;
			int amount = buf.readInt();
			if(amount == 0) {
				stack = null;
			}
			else {
				stack = new FluidStack(FluidRegistry.getFluid(ByteBufUtils.readUTF8String(buf)), amount, ByteBufUtils.readTag(buf));
			}
			fluidOutputs.add(stack);
		}
	}

	@Override
	public IMessage onMessage(MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		world.addScheduledTask(()->{
			if(player.openContainer instanceof ContainerLargePatternEncoder) {
				TileLargePatternEncoder tile = ((ContainerLargePatternEncoder)player.openContainer).tile;
				ItemStackHandler handler = tile.processingMatrix;
				FluidInventoryBase handlerFluid = tile.processingMatrixFluids;
				clearInputsAndOutputs(handler);
				clearInputsAndOutputs(handlerFluid);
				setItemInputs(handler, itemInputs);
				setItemOutputs(handler, itemOutputs);
				setFluidInputs(handlerFluid, fluidInputs);
				setFluidOutputs(handlerFluid, fluidOutputs);
			}
		});
		return null;
	}

	private void clearInputsAndOutputs(ItemStackHandler handler) {
		for(int i = 0; i < 90; ++i) {
			handler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	private void clearInputsAndOutputs(FluidInventoryBase handler) {
		for(int i = 0; i < 90; ++i) {
			handler.setStackInSlot(i, null);
		}
	}

	private void setItemInputs(ItemStackHandler handler, Collection<ItemStack> stacks) {
		setItemSlots(handler, stacks, 0, 81);
	}

	private void setItemOutputs(ItemStackHandler handler, Collection<ItemStack> stacks) {
		setItemSlots(handler, stacks, 81, 90);
	}

	private void setItemSlots(ItemStackHandler handler, Collection<ItemStack> stacks, int begin, int end) {
		for(ItemStack stack : stacks) {
			handler.setStackInSlot(begin, stack);
			begin++;
			if(begin >= end) {
				break;
			}
		}
	}

	private void setFluidInputs(FluidInventoryBase inventory, Collection<FluidStack> stacks) {
		setFluidSlots(inventory, stacks, 0, 81);
	}

	private void setFluidOutputs(FluidInventoryBase inventory, Collection<FluidStack> stacks) {
		setFluidSlots(inventory, stacks, 81, 90);
	}

	private void setFluidSlots(FluidInventoryBase inventory, Collection<FluidStack> stacks, int begin, int end) {
		for(FluidStack stack : stacks) {
			inventory.setStackInSlot(begin, stack.copy());
			begin++;
			if(begin >= end) {
				break;
			}
		}
	}
}
