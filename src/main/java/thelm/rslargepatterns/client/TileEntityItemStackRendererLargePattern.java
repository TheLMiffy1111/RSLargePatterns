package thelm.rslargepatterns.client;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.item.ItemLargePattern;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class TileEntityItemStackRendererLargePattern extends TileEntityItemStackRenderer {

	@Override
	public void renderByItem(ItemStack stack) {
		LargeProcessingPattern pattern = ItemLargePattern.getPatternFromCache(null, stack);
		ItemStack outputStack = pattern.getOutputs().get(0);
		outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack);
	}

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		LargeProcessingPattern pattern = ItemLargePattern.getPatternFromCache(null, stack);
		ItemStack outputStack = pattern.getOutputs().get(0);
		outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack, partialTicks);
	}
}