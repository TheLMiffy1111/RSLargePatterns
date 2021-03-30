package thelm.rslargepatterns.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thelm.rslargepatterns.block.LargePatternEncoderBlock;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;
import thelm.rslargepatterns.inventory.BaseFluidInventory;
import thelm.rslargepatterns.inventory.BaseItemHandler;
import thelm.rslargepatterns.inventory.LargePatternsItemHandler;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.util.LargeAllowedTagList;

public class LargePatternEncoderTile extends TileEntity implements INamedContainerProvider {

	public static final TileEntityType<LargePatternEncoderTile> TYPE_INSTANCE = (TileEntityType<LargePatternEncoderTile>)TileEntityType.Builder.
			create(LargePatternEncoderTile::new, LargePatternEncoderBlock.INSTANCE).
			build(null).setRegistryName("rslargepatterns:large_pattern_encoder");

	public static final String NBT_VIEW_TYPE = "ViewType";
	public static final String NBT_PROCESSING_TYPE = "ProcessingType";
	public static final String NBT_PROCESSING_MATRIX = "ProcessingMatrix";
	public static final String NBT_PROCESSING_MATRIX_FLUIDS = "ProcessingMatrixFluids";
	public static final String NBT_ALLOWED_TAGS = "AllowedTags";

	public final BaseItemHandler patterns = new LargePatternsItemHandler(this);
	public final BaseItemHandler processingMatrix = new BaseItemHandler(this, 90);
	public final BaseFluidInventory processingMatrixFluids = new BaseFluidInventory(this, 90);
	public final LargeAllowedTagList allowedTagList = new LargeAllowedTagList();

	public int processingType;

	public LargePatternEncoderTile() {
		super(TYPE_INSTANCE);
	}

	public void clearMatrix() {
		for(int i = 0; i < processingMatrix.getSlots(); ++i) {
			processingMatrix.setStackInSlot(i, ItemStack.EMPTY);
		}
		for(int i = 0; i < processingMatrixFluids.getSlots(); ++i) {
			processingMatrixFluids.setStackInSlot(i, FluidStack.EMPTY);
		}
	}

	public void onCreatePattern() {
		if(canCreatePattern()) {
			if(patterns.getStackInSlot(1).isEmpty()) {
				patterns.extractItem(0, 1, false);
			}
			ItemStack pattern = new ItemStack(LargePatternItem.INSTANCE);
			LargePatternItem.setToCurrentVersion(pattern);
			LargePatternItem.setAllowedTags(pattern, allowedTagList);
			for(int i = 0; i < 90; ++i) {
				if(!processingMatrix.getStackInSlot(i).isEmpty()) {
					if(i >= 81) {
						LargePatternItem.setOutputSlot(pattern, i-81, processingMatrix.getStackInSlot(i));
					}
					else {
						LargePatternItem.setInputSlot(pattern, i, processingMatrix.getStackInSlot(i));
					}
				}
				FluidStack fluid = processingMatrixFluids.getStackInSlot(i);
				if(!fluid.isEmpty()) {
					if(i >= 81) {
						LargePatternItem.setFluidOutputSlot(pattern, i-81, fluid);
					}
					else {
						LargePatternItem.setFluidInputSlot(pattern, i, fluid);
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
			if(!processingMatrixFluids.getStackInSlot(i).isEmpty()) {
				inputsFilled = true;
				break;
			}
		}
		for(int i = 81; i < 90; ++i) {
			if(!processingMatrix.getStackInSlot(i).isEmpty()) {
				return true;
			}
			if(!processingMatrixFluids.getStackInSlot(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public IItemHandler getPatterns() {
		return patterns;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		readSync(nbt);
		patterns.readFromNBT(nbt);
		processingMatrix.readFromNBT(nbt.getCompound(NBT_PROCESSING_MATRIX));
		processingMatrixFluids.read(nbt.getCompound(NBT_PROCESSING_MATRIX_FLUIDS));
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);
		writeSync(nbt);
		patterns.writeToNBT(nbt);
		nbt.put(NBT_PROCESSING_MATRIX, processingMatrix.writeToNBT(new CompoundNBT()));
		nbt.put(NBT_PROCESSING_MATRIX_FLUIDS, processingMatrixFluids.write(new CompoundNBT()));
		return nbt;
	}

	public void readSync(CompoundNBT nbt) {
		processingType = nbt.getByte(NBT_VIEW_TYPE);
		allowedTagList.readFromNbt(nbt.getCompound(NBT_ALLOWED_TAGS));
	}

	public CompoundNBT writeSync(CompoundNBT nbt) {
		nbt.putByte(NBT_VIEW_TYPE, (byte)processingType);
		nbt.put(NBT_ALLOWED_TAGS, allowedTagList.writeToNbt());
		return nbt;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readSync(pkt.getNbtCompound());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -10, getUpdateTag());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		readSync(tag);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		nbt.remove("ForgeData");
		nbt.remove("ForgeCaps");
		writeSync(nbt);
		return nbt;
	}

	public void syncTile(boolean rerender) {
		if(world != null && world.isBlockLoaded(pos)) {
			BlockState state = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, state, state, 2 + (rerender ? 4 : 0));
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(()->(T)patterns);
		}
		return super.getCapability(capability, facing);
	}

	public int getField(int id) {
		switch(id) {
		case 0:
			return processingType;
		}
		return 0;
	}

	public void setField(int id, int value) {
		switch(id) {
		case 0:
			processingType = value;
			break;
		}
	}

	public int getFieldCount() {
		return 1;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
		syncTile(false);
		return new LargePatternEncoderContainer(windowId, playerInventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.rslargepatterns.large_pattern_encoder");
	}
}
