package thelm.rslargepatterns.tile;

import com.raoulvdberge.refinedstorage.item.ItemPattern;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thelm.rslargepatterns.client.gui.GuiLargePatternEncoder;
import thelm.rslargepatterns.client.gui.IGuiProvider;
import thelm.rslargepatterns.container.ContainerLargePatternEncoder;
import thelm.rslargepatterns.inventory.FluidInventoryBase;
import thelm.rslargepatterns.inventory.ItemHandlerBase;
import thelm.rslargepatterns.inventory.ItemHandlerLargePatterns;
import thelm.rslargepatterns.item.ItemLargePattern;

public class TileLargePatternEncoder extends TileEntity implements IGuiProvider {

	public static final String NBT_VIEW_TYPE = "ViewType";
	public static final String NBT_OREDICT_PATTERN = "OredictPattern";
	public static final String NBT_PROCESSING_TYPE = "ProcessingType";
	public static final String NBT_PROCESSING_MATRIX = "ProcessingMatrix";
	public static final String NBT_PROCESSING_MATRIX_FLUIDS = "ProcessingMatrixFluids";

	public final ItemHandlerBase patterns = new ItemHandlerLargePatterns(this);
	public final ItemHandlerBase processingMatrix = new ItemHandlerBase(this, 90);
	public final FluidInventoryBase processingMatrixFluids = new FluidInventoryBase(this, 90);

	public int processingType;
	public boolean oredictPattern;

	public TileLargePatternEncoder() {}

	public void clearMatrix() {
		for(int i = 0; i < processingMatrix.getSlots(); ++i) {
			processingMatrix.setStackInSlot(i, ItemStack.EMPTY);
		}
		for(int i = 0; i < processingMatrixFluids.getSlots(); ++i) {
			processingMatrixFluids.setStackInSlot(i, null);
		}
	}

	public void onCreatePattern() {
		if(canCreatePattern()) {
			if(patterns.getStackInSlot(1).isEmpty()) {
				patterns.extractItem(0, 1, false);
			}
			ItemStack pattern = new ItemStack(ItemLargePattern.INSTANCE);
			ItemLargePattern.setOredict(pattern, oredictPattern);
			for(int i = 0; i < 90; ++i) {
				if(!processingMatrix.getStackInSlot(i).isEmpty()) {
					if(i >= 81) {
						ItemPattern.setOutputSlot(pattern, i-81, processingMatrix.getStackInSlot(i));
					}
					else {
						ItemPattern.setInputSlot(pattern, i, processingMatrix.getStackInSlot(i));
					}
				}
				FluidStack fluid = processingMatrixFluids.getStackInSlot(i);
				if(fluid != null) {
					if(i >= 81) {
						ItemPattern.setFluidOutputSlot(pattern, i-81, fluid);
					}
					else {
						ItemPattern.setFluidInputSlot(pattern, i, fluid);
					}
				}
			}
			patterns.setStackInSlot(1, pattern);
		}
	}

	private boolean isPatternAvailable() {
		return !(patterns.getStackInSlot(0).isEmpty() && patterns.getStackInSlot(1).isEmpty());
	}

	public boolean canCreatePattern() {
		if(!isPatternAvailable()) {
			return false;
		}
		boolean inputsFilled = false;
		for(int i = 0; i < 81; ++i) {
			if(!processingMatrix.getStackInSlot(i).isEmpty()) {
				inputsFilled = true;
				break;
			}
			if(processingMatrixFluids.getStackInSlot(i) != null) {
				inputsFilled = true;
				break;
			}
		}
		for(int i = 81; i < 90; ++i) {
			if(!processingMatrix.getStackInSlot(i).isEmpty()) {
				return true;
			}
			if(processingMatrixFluids.getStackInSlot(i) != null) {
				return true;
			}
		}
		return false;
	}

	public IItemHandler getPatterns() {
		return patterns;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readSyncNBT(nbt);
		patterns.readFromNBT(nbt);
		processingMatrix.readFromNBT(nbt.getCompoundTag(NBT_PROCESSING_MATRIX));
		processingMatrixFluids.readFromNBT(nbt.getCompoundTag(NBT_PROCESSING_MATRIX_FLUIDS));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		writeSyncNBT(nbt);
		patterns.writeToNBT(nbt);
		nbt.setTag(NBT_PROCESSING_MATRIX, processingMatrix.writeToNBT(new NBTTagCompound()));
		nbt.setTag(NBT_PROCESSING_MATRIX_FLUIDS, processingMatrixFluids.writeToNBT(new NBTTagCompound()));
		return nbt;
	}

	public void readSyncNBT(NBTTagCompound nbt) {
		processingType = nbt.getByte(NBT_VIEW_TYPE);
		oredictPattern = nbt.getBoolean(NBT_OREDICT_PATTERN);
	}

	public NBTTagCompound writeSyncNBT(NBTTagCompound nbt) {
		nbt.setByte(NBT_VIEW_TYPE, (byte)processingType);
		nbt.setBoolean(NBT_OREDICT_PATTERN, oredictPattern);
		return nbt;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readSyncNBT(pkt.getNbtCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, -10, getUpdateTag());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		readSyncNBT(tag);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.removeTag("ForgeData");
		nbt.removeTag("ForgeCaps");
		writeSyncNBT(nbt);
		return nbt;
	}

	public void syncTile(boolean rerender) {
		if(world != null && world.isBlockLoaded(pos)) {
			IBlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 2 + (rerender ? 4 : 0));
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing from) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				super.hasCapability(capability, from);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T)patterns;
		}
		return super.getCapability(capability, facing);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return new GuiLargePatternEncoder(new ContainerLargePatternEncoder(player.inventory, this));
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerLargePatternEncoder(player.inventory, this);
	}

	@Override
	public int getField(int id) {
		switch(id) {
		case 0:
			return processingType;
		case 1:
			return oredictPattern ? 1 : 0;
		}
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
		case 0:
			processingType = value;
			break;
		case 1:
			oredictPattern = value == 1;
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 2;
	}
}
