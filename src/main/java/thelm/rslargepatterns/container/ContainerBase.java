package thelm.rslargepatterns.container;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import thelm.rslargepatterns.client.gui.IGuiProvider;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.PacketFluidSlotUpdate;
import thelm.rslargepatterns.slot.SlotFluidFalseCopy;
import thelm.rslargepatterns.slot.SlotItemFalseCopy;

//Large portions of code are taken from CoFHCore
public abstract class ContainerBase extends Container {

	public final InventoryPlayer playerInventory;
	public final IGuiProvider guiProvider;
	public final List<SlotFluidFalseCopy> fluidSlots = new ArrayList<>();
	public final List<FluidStack> fluids = new ArrayList<>();
	public final Int2IntMap prevSyncValues = new Int2IntRBTreeMap();

	public ContainerBase(InventoryPlayer playerInventory, IGuiProvider guiProvider) {
		this.playerInventory = playerInventory;
		this.guiProvider = guiProvider;
	}

	public abstract int getPlayerInvY();

	public abstract int getPlayerInvX();

	public void setupPlayerInventory() {
		int xOffset = getPlayerInvX();
		int yOffset = getPlayerInvY();
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInventory, j+i*9+9, xOffset+j*18, yOffset+i*18));
			}
		}
		for(int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInventory, i, xOffset+i*18, yOffset+58));
		}
	}

	public abstract int getSizeInventory();

	public boolean supportsShiftClick(EntityPlayer player, int slotIndex) {
		return true;
	}

	public boolean performMerge(EntityPlayer player, int slotIndex, ItemStack stack) {
		int invBase = getSizeInventory();
		int invFull = inventorySlots.size();
		if(slotIndex < invBase) {
			return mergeItemStack(stack, invBase, invFull, true);
		}
		return mergeItemStack(stack, 0, invBase, false);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
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
				if(slot instanceof SlotItemFalseCopy || slot instanceof SlotFluidFalseCopy) {
					i += iterOrder;
					continue;
				}
				existingStack = slot.getStack();
				if(!existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());
					if(slot.isItemValid(ItemHandlerHelper.copyStackWithSize(stack, rmv)) && existingStack.getItem().equals(stack.getItem()) && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
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
				if(slot instanceof SlotItemFalseCopy || slot instanceof SlotFluidFalseCopy) {
					i += iterOrder;
					continue;
				}
				existingStack = slot.getStack();
				if(existingStack.isEmpty()) {
					int maxStack = Math.min(stack.getMaxStackSize(), slot.getSlotStackLimit());
					int rmv = Math.min(maxStack, stack.getCount());
					if(slot.isItemValid(ItemHandlerHelper.copyStackWithSize(stack, rmv))) {
						existingStack = stack.splitStack(rmv);
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
	public ItemStack slotClick(int slotId, int mouseButton, ClickType clickType, EntityPlayer player) {
		Slot slot = slotId < 0 ? null : inventorySlots.get(slotId);
		if(slot instanceof SlotItemFalseCopy || slot instanceof SlotFluidFalseCopy) {
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
	protected Slot addSlotToContainer(Slot slot) {
		if(slot instanceof SlotFluidFalseCopy) {
			fluids.add(null);
			fluidSlots.add((SlotFluidFalseCopy)slot);
		}
		return super.addSlotToContainer(slot);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(int i = 0; i < fluidSlots.size(); ++i) {
			SlotFluidFalseCopy slot = fluidSlots.get(i);
			FluidStack actual = slot.fluidInventory.getStackInSlot(slot.getSlotIndex());
			FluidStack cached = fluids.get(i);
			if(!areFluidStacksEqual(cached, actual)) {
				fluids.set(i, actual);
				for(IContainerListener listener : listeners) {
					if(listener instanceof EntityPlayerMP) {
						PacketHandler.INSTANCE.sendTo(new PacketFluidSlotUpdate((short)slot.slotNumber, actual), (EntityPlayerMP)listener);
					}
				}
			}
		}
		if(guiProvider != null) {
			for(IContainerListener listener : listeners) {
				for(int i = 0; i < guiProvider.getFieldCount(); ++i) {
					if(!prevSyncValues.containsKey(i) || prevSyncValues.get(i) != guiProvider.getField(i)) {
						listener.sendWindowProperty(this, i, guiProvider.getField(i));
					}
				}
			}
			for(int i = 0; i < guiProvider.getFieldCount(); ++i) {
				prevSyncValues.put(i, guiProvider.getField(i));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int data) {
		if(guiProvider != null) {
			guiProvider.setField(id, data);
		}
	}

	public boolean areFluidStacksEqual(FluidStack left, FluidStack right) {
		if(left == null && right == null) {
			return true;
		}
		if((left == null && right != null) || (left != null && right == null)) {
			return false;
		}
		if(left.getFluid() != right.getFluid()) {
			return false;
		}
		if(left.amount != right.amount) {
			return false;
		}
		if(left.tag != null && !left.tag.equals(right.tag)) {
			return false;
		}
		return true;
	}
}
