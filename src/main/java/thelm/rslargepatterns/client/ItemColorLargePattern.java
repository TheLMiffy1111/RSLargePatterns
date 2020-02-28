package thelm.rslargepatterns.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.item.ItemLargePattern;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class ItemColorLargePattern implements IItemColor {

	public static final ItemColorLargePattern INSTANCE = new ItemColorLargePattern();

	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		LargeProcessingPattern pattern = ItemLargePattern.getPatternFromCache(Minecraft.getMinecraft().world, stack);
		if(BakedModelLargePattern.canDisplayOutput(stack, pattern)) {
			int color = Minecraft.getMinecraft().getItemColors().colorMultiplier(pattern.getOutputs().get(0), tintIndex);
			if(color != -1) {
				return color;
			}
		}
		return 0xFFFFFF;
	}
}
