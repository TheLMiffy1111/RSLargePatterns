package thelm.rslargepatterns.client.color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import thelm.rslargepatterns.client.model.LargePatternBakedModel;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class LargePatternItemColor implements IItemColor {

	public static final LargePatternItemColor INSTANCE = new LargePatternItemColor();

	@Override
	public int getColor(ItemStack stack, int tintIndex) {
		LargeProcessingPattern pattern = LargePatternItem.fromCache(Minecraft.getInstance().world, stack);
		if(LargePatternBakedModel.canDisplayOutput(stack, pattern)) {
			int color = Minecraft.getInstance().getItemColors().getColor(pattern.getOutputs().get(0), tintIndex);
			if(color != -1) {
				return color;
			}
		}
		return 0xFFFFFF;
	}
}
