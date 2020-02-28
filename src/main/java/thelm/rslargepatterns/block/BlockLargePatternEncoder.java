package thelm.rslargepatterns.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.items.IItemHandler;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.client.IModelRegister;
import thelm.rslargepatterns.tile.TileLargePatternEncoder;

public class BlockLargePatternEncoder extends Block implements ITileEntityProvider, IModelRegister {

	public static final BlockLargePatternEncoder INSTANCE = new BlockLargePatternEncoder();
	public static final Item ITEM_INSTANCE = new ItemBlock(INSTANCE).setRegistryName("rslargepatterns:large_pattern_encoder");
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("rslargepatterns:large_pattern_encoder#normal");

	protected BlockLargePatternEncoder() {
		super(Material.ROCK);
		setHardness(15F);
		setResistance(25F);
		setSoundType(SoundType.METAL);
		setTranslationKey("rslargepatterns.large_pattern_encoder");
		setRegistryName("rslargepatterns:large_pattern_encoder");
		setCreativeTab(RSLargePatterns.CREATIVE_TAB);
	}

	@Override
	public TileLargePatternEncoder createNewTileEntity(World worldIn, int meta) {
		return new TileLargePatternEncoder();
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(playerIn.isSneaking()) {
			return false;
		}
		if(!worldIn.isRemote) {
			playerIn.openGui(RSLargePatterns.instance, facing.getIndex(), worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)  {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(tileentity instanceof TileLargePatternEncoder) {
			IItemHandler handler = ((TileLargePatternEncoder)tileentity).getPatterns();
			for(int i = 0; i < handler.getSlots(); ++i) {
				ItemStack stack = handler.getStackInSlot(i);
				if(!stack.isEmpty()) {
					InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				}
			}
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(ITEM_INSTANCE, 0, MODEL_LOCATION);
	}
}
