package thelm.rslargepatterns.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class LargePatternItemStackRenderer extends ItemStackTileEntityRenderer {

	@Override
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228364_4_, int p_228364_5_) {
		LargeProcessingPattern pattern = LargePatternItem.fromCache(null, stack);
		ItemStack outputStack = pattern.getOutputs().get(0);
		outputStack.getItem().getItemStackTileEntityRenderer().func_239207_a_(outputStack, transformType, matrixStack, renderTypeBuffer, p_228364_4_, p_228364_5_);
	}
}