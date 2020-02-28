package thelm.rslargepatterns.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.client.gui.IGuiProvider;

public class GuiHandler implements IGuiHandler {

	public static final GuiHandler INSTANCE = new GuiHandler();

	protected GuiHandler() {

	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumFacing side = null;
		if(ID <= 5) {
			side = EnumFacing.byIndex(ID);
		}
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null) {
			return null;
		}
		else if(tile instanceof IGuiProvider) {
			return ((IGuiProvider)tile).getClientGuiElement(player);
		}
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		EnumFacing side = null;
		if(ID <= 5) {
			side = EnumFacing.byIndex(ID);
		}
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null) {
			return null;
		}
		else if(tile instanceof IGuiProvider) {
			return ((IGuiProvider)tile).getServerGuiElement(player);
		}
		return null;
	}

	public void launchGui(int ID, EntityPlayer player, World world, int x, int y, int z) {
		player.openGui(RSLargePatterns.instance, ID, world, x, y, z);
	}
}
