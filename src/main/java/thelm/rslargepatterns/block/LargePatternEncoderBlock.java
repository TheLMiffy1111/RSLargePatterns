package thelm.rslargepatterns.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.tile.LargePatternEncoderTile;

public class LargePatternEncoderBlock extends Block {

	public static final LargePatternEncoderBlock INSTANCE = new LargePatternEncoderBlock();
	public static final Item ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Properties().group(RSLargePatterns.ITEM_GROUP)).setRegistryName("rslargepatterns:large_pattern_encoder");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("rslargepatterns:large_pattern_encoder#normal");

	protected LargePatternEncoderBlock() {
		super(Block.Properties.create(Material.ROCK).hardnessAndResistance(15F, 25F).sound(SoundType.METAL));
		setRegistryName("rslargepatterns:large_pattern_encoder");
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public LargePatternEncoderTile createTileEntity(BlockState state, IBlockReader worldIn) {
		return new LargePatternEncoderTile();
	}

	@Override
	public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
		if(playerIn.isShiftKeyDown()) {
			return ActionResultType.PASS;
		}
		if(!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if(tile instanceof INamedContainerProvider) {
				NetworkHooks.openGui((ServerPlayerEntity)playerIn, (INamedContainerProvider)tile, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof LargePatternEncoderTile) {
			IItemHandler handler = ((LargePatternEncoderTile)tileentity).getPatterns();
			for(int i = 0; i < handler.getSlots(); ++i) {
				ItemStack stack = handler.getStackInSlot(i);
				if(!stack.isEmpty()) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
}
