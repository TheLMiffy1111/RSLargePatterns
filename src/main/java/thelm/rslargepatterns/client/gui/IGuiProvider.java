package thelm.rslargepatterns.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuiProvider {

	@SideOnly(Side.CLIENT)
	GuiContainer getClientGuiElement(EntityPlayer player, Object... args);

	Container getServerGuiElement(EntityPlayer player, Object... args);

    int getField(int id);

    void setField(int id, int value);

    int getFieldCount();
}
