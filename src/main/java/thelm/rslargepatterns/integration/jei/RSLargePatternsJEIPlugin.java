package thelm.rslargepatterns.integration.jei;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class RSLargePatternsJEIPlugin implements IModPlugin {

	public static IJeiRuntime jeiRuntime;
	public static List<ResourceLocation> allCategories = Collections.emptyList();

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("rslargepatterns:1");
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
		registry.addUniversalRecipeTransferHandler(LargePatternEncoderTransferHandler.INSTANCE);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		RSLargePatternsJEIPlugin.jeiRuntime = jeiRuntime;
		allCategories = Lists.transform(jeiRuntime.getRecipeManager().getRecipeCategories(), IRecipeCategory::getUid);
	}

	public static void showAllCategories() {
		if(jeiRuntime != null && !allCategories.isEmpty()) {
			jeiRuntime.getRecipesGui().showCategories(allCategories);
		}
	}
}
