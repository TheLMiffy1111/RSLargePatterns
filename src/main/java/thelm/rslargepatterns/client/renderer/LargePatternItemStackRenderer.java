package thelm.rslargepatterns.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class LargePatternItemStackRenderer extends ItemStackTileEntityRenderer {

	@Override
	public void render(final ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228364_4_, int p_228364_5_) {
		LargeProcessingPattern pattern = LargePatternItem.fromCache(null, stack);
		ItemStack outputStack = pattern.getOutputs().get(0);
		outputStack.getItem().getItemStackTileEntityRenderer().render(outputStack, matrixStack, renderTypeBuffer, p_228364_4_, p_228364_5_);
	}
}