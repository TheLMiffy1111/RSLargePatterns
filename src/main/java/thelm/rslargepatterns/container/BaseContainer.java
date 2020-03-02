package thelm.rslargepatterns.container;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.FluidSlotUpdatePacket;
import thelm.rslargepatterns.slot.FalseCopyFluidSlot;
import thelm.rslargepatterns.slot.FalseCopySlot;

//Large portions of code are taken from CoFHCore
public abstract class BaseContainer extends Container {

	public final PlayerInventory playerInventory;
	public final List<FalseCopyFluidSlot> fluidSlots = new ArrayList<>();
	public final List<FluidStack> fluids = NonNullList.create();
	public final Int2IntMap prevSyncValues = new Int2IntRBTreeMap();

	public BaseContainer(ContainerType<?> type, int windowId, PlayerInventory playerInventory) {
		super(type, windowId);
		this.playerInventory = playerInventory;
	}

	public abstract int getPlayerInvY();

	public abstract int getPlayerInvX();

	public void setupPlayerInventory() {
		int xOffset = getPlayerInvX();
		int yOffset = getPlayerInvY();
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInventory, j+i*9+9, xOffset+j*18, yOffset+i*18));
			}
		}
		for(int i = 0; i < 9; i++) {
			addSlot(new Slot(playerInventory, i, xOffset+i*18, yOffset+58));
		}
	}

	public abstract int getSizeInventory();

	public boolean supportsShiftClick(PlayerEntity player, int slotIndex) {
		return true;
	}

	public boolean performMerge(PlayerEntity player, int slotIndex, ItemStack stack) {
		int invBase = getSizeInventory();
		int invFull = inventorySlots.size();
		if(slotIndex < invBase) {
			return mergeItemStack(stack, invBase, invFull, true);
		}
		return mergeItemStack(stack, 0, invBase, false);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		if(!supportsShiftClick(player, slotIndex)) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);
		if(slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();
			if(!performMerge(player, slotIndex, stackInSlot)) {
				return ItemStack.EMPTY;
			}
			slot.onSlotChange(stackInSlot, stack);
			if(stackInSlot.getCount() <= 0) {
				slot.putStack(ItemStack.EMPTY);
			}
			else {
				slot.putStack(stackInSlot);
			}
			if(stackInSlot.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, stackInSlot);
		}
		return stack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotMin, int slotMax, boolean ascending) {
		boolean successful = false;
		int i = !ascending ? slotMin : slotMax - 1;
		int iterOrder = !ascending ? 1 : -1;
		Slot slot;
		ItemStack existingStack;
		if(stack.isStackable()) {
			while(stack.getCount() > 0 && (!ascending && i < slotMax || ascending && i >= slotMin)) {
				slot = inventorySlots.get(i);
				if(slot instanceof FalseCopySlot || slot instanceof FalseCopyFluidSlot) {
					i += iterOrder;
					continue;
				}
				existingStack = slot.getStack();
				if(!existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());
					if(slot.isItemValid(ItemHandlerHelper.copyStackWithSize(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
						int existingSize = existingStack.getCount() + stack.getCount();
						if(existingSize <= maxStack) {
							stack.setCount(0);
							existingStack.setCount(existingSize);
							slot.putStack(existingStack);
							successful = true;
						}
						else if(existingStack.getCount() < maxStack) {
							stack.shrink(maxStack - existingStack.getCount());
							existingStack.setCount(maxStack);
							slot.putStack(existingStack);
							successful = true;
						}
					}
				}
				i += iterOrder;
			}
		}
		if(stack.getCount() > 0) {
			i = !ascending ? slotMin : slotMax - 1;
			while(stack.getCount() > 0 && (!ascending && i < slotMax || ascending && i >= slotMin)) {
				slot = inventorySlots.get(i);
				if(slot instanceof FalseCopySlot || slot instanceof FalseCopyFluidSlot) {
					i += iterOrder;
					continue;
				}
				existingStack = slot.getStack();
				if(existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());
					if(slot.isItemValid(ItemHandlerHelper.copyStackWithSize(stack, rmv))) {
						existingStack = stack.split(rmv);
						slot.putStack(existingStack);
						successful = true;
					}
				}
				i += iterOrder;
			}
		}
		return successful;
	}

	@Override
	public ItemStack slotClick(int slotId, int mouseButton, ClickType clickType, PlayerEntity player) {
		Slot slot = slotId < 0 ? null : inventorySlots.get(slotId);
		if(slot instanceof FalseCopySlot || slot instanceof FalseCopyFluidSlot) {
			if(clickType == ClickType.QUICK_MOVE) {
				slot.putStack(ItemStack.EMPTY);
			}
			else if(!player.inventory.getItemStack().isEmpty()) {
				slot.putStack(player.inventory.getItemStack().copy());
			}
			return player.inventory.getItemStack();
		}
		return super.slotClick(slotId, mouseButton, clickType, player);
	}

	@Override
	protected Slot addSlot(Slot slot) {
		if(slot instanceof FalseCopyFluidSlot) {
			fluids.add(FluidStack.EMPTY);
			fluidSlots.add((FalseCopyFluidSlot)slot);
		}
		return super.addSlot(slot);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(int i = 0; i < fluidSlots.size(); ++i) {
			FalseCopyFluidSlot slot = fluidSlots.get(i);
			FluidStack actual = slot.fluidInventory.getStackInSlot(slot.getSlotIndex());
			FluidStack cached = fluids.get(i);
			if(!areFluidStacksEqual(cached, actual)) {
				fluids.set(i, actual);
				PacketHandler.INSTANCE.sendTo(new FluidSlotUpdatePacket((short)slot.slotNumber, actual),
						((ServerPlayerEntity)playerInventory.player).connection.getNetworkManager(),
						NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}

	public boolean areFluidStacksEqual(FluidStack left, FluidStack right) {
		if(left.isEmpty() && right.isEmpty()) {
			return true;
		}
		if((left.isEmpty() && !right.isEmpty()) || (!left.isEmpty() && right.isEmpty())) {
			return false;
		}
		if(left.getFluid() != right.getFluid()) {
			return false;
		}
		if(left.getAmount() != right.getAmount()) {
			return false;
		}
		if(left.getTag() != null && !left.getTag().equals(right.getTag())) {
			return false;
		}
		return true;
	}
}
