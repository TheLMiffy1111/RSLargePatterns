package thelm.rslargepatterns.integration.jei;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;

@JEIPlugin
public class RSLargePatternsJEIPlugin implements IModPlugin {

	public static IModRegistry registry;
	public static IJeiRuntime jeiRuntime;
	public static List<String> allCategories = Collections.emptyList();

	@Override
	public void register(IModRegistry registry) {
		RSLargePatternsJEIPlugin.registry = registry;
		registry.getRecipeTransferRegistry().addUniversalRecipeTransferHandler(LargePatternEncoderTransferHandler.INSTANCE);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		RSLargePatternsJEIPlugin.jeiRuntime = jeiRuntime;
		allCategories = Lists.transform(jeiRuntime.getRecipeRegistry().getRecipeCategories(), IRecipeCategory::getUid);
	}

	public static void showAllCategories() {
		if(jeiRuntime != null && !allCategories.isEmpty()) {
			jeiRuntime.getRecipesGui().showCategories(allCategories);
		}
	}
}
