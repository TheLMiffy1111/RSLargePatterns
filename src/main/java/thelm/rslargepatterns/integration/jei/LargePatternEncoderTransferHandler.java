package thelm.rslargepatterns.integration.jei;

import java.util.LinkedList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.container.ContainerLargePatternEncoder;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.PacketSetRecipe;

public class LargePatternEncoderTransferHandler implements IRecipeTransferHandler<ContainerLargePatternEncoder> {

	public static final LargePatternEncoderTransferHandler INSTANCE = new LargePatternEncoderTransferHandler();

	@Override
	public Class<ContainerLargePatternEncoder> getContainerClass() {
		return ContainerLargePatternEncoder.class;
	}

	@Override
	public IRecipeTransferError transferRecipe(ContainerLargePatternEncoder container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if(doTransfer) {
            List<ItemStack> itemInputs = new LinkedList<>();
            List<ItemStack> itemOutputs = new LinkedList<>();
            List<FluidStack> fluidInputs = new LinkedList<>();
            List<FluidStack> fluidOutputs = new LinkedList<>();
            for(IGuiIngredient<ItemStack> guiIngredient : recipeLayout.getItemStacks().getGuiIngredients().values()) {
                if(guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                    ItemStack ingredient = guiIngredient.getDisplayedIngredient().copy();
                    if(guiIngredient.isInput()) {
                        itemInputs.add(ingredient);
                    }
                    else {
                        itemOutputs.add(ingredient);
                    }
                }
            }
            for(IGuiIngredient<FluidStack> guiIngredient : recipeLayout.getFluidStacks().getGuiIngredients().values()) {
                if(guiIngredient != null && guiIngredient.getDisplayedIngredient() != null) {
                    FluidStack ingredient = guiIngredient.getDisplayedIngredient().copy();
                    if(guiIngredient.isInput()) {
                        fluidInputs.add(ingredient);
                    }
                    else {
                        fluidOutputs.add(ingredient);
                    }
                }
            }
			PacketHandler.INSTANCE.sendToServer(new PacketSetRecipe(itemInputs, itemOutputs, fluidInputs, fluidOutputs));
		}
		return null;
	}
}
