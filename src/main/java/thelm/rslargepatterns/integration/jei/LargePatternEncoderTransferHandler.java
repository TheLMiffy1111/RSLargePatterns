package thelm.rslargepatterns.integration.jei;

import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredient;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.SetRecipePacket;

public class LargePatternEncoderTransferHandler implements IRecipeTransferHandler<LargePatternEncoderContainer> {

	public static final LargePatternEncoderTransferHandler INSTANCE = new LargePatternEncoderTransferHandler();

	@Override
	public Class<LargePatternEncoderContainer> getContainerClass() {
		return LargePatternEncoderContainer.class;
	}

	@Override
	public IRecipeTransferError transferRecipe(LargePatternEncoderContainer container, IRecipeLayout recipeLayout, PlayerEntity player, boolean maxTransfer, boolean doTransfer) {
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
			PacketHandler.INSTANCE.sendToServer(new SetRecipePacket(itemInputs, itemOutputs, fluidInputs, fluidOutputs));
		}
		return null;
	}
}
