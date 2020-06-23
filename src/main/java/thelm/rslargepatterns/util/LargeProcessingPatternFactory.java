package thelm.rslargepatterns.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPatternFactoryException;
import com.refinedmods.refinedstorage.item.PatternItem;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.item.LargePatternItem;

public class LargeProcessingPatternFactory {

	public static final LargeProcessingPatternFactory INSTANCE = new LargeProcessingPatternFactory();

	public LargeProcessingPattern create(World world, ICraftingPatternContainer container, ItemStack stack) {
		LargeAllowedTagList allowedTagList = LargePatternItem.getAllowedTags(stack);

		List<NonNullList<ItemStack>> inputs = new ArrayList<>();
		NonNullList<ItemStack> outputs = NonNullList.create();
		List<NonNullList<FluidStack>> fluidInputs = new ArrayList<>();
		NonNullList<FluidStack> fluidOutputs = NonNullList.create();
		ICraftingRecipe recipe = null;
		boolean valid = true;
		ITextComponent errorMessage = null;

		try {
			for(int i = 0; i < 81; ++i) {
				fillProcessingItems(i, stack, inputs, outputs, allowedTagList);
				fillProcessingFluids(i, stack, fluidInputs, fluidOutputs, allowedTagList);
			}
			if(outputs.isEmpty() && fluidOutputs.isEmpty()) {
				throw new CraftingPatternFactoryException(new TranslationTextComponent("misc.refinedstorage.pattern.error.processing_no_outputs"));
			}
		}
		catch(CraftingPatternFactoryException e) {
			valid = false;
			errorMessage = e.getErrorMessage();
		}

		return new LargeProcessingPattern(container, stack, errorMessage, valid, recipe, inputs, outputs, fluidInputs, fluidOutputs, allowedTagList);
	}

	private void fillProcessingItems(int i, ItemStack stack, List<NonNullList<ItemStack>> inputs, NonNullList<ItemStack> outputs, LargeAllowedTagList allowedTagList) throws CraftingPatternFactoryException {
		ItemStack input = PatternItem.getInputSlot(stack, i);
		if(input.isEmpty()) {
			inputs.add(NonNullList.create());
		}
		else {
			NonNullList<ItemStack> possibilities = NonNullList.create();
			possibilities.add(input.copy());
			if(allowedTagList != null) {
				Collection<ResourceLocation> tagsOfItem = ItemTags.getCollection().getOwningTags(input.getItem());
				Set<ResourceLocation> declaredAllowedTags = allowedTagList.getAllowedItemTags().get(i);
				for(ResourceLocation declaredAllowedTag : declaredAllowedTags) {
					if(!tagsOfItem.contains(declaredAllowedTag)) {
						throw new CraftingPatternFactoryException(
								new TranslationTextComponent(
										"misc.refinedstorage.pattern.error.tag_no_longer_applicable",
										declaredAllowedTag.toString(),
										input.getDisplayName()
										)
								);
					}
					else {
						for(Item element : ItemTags.getCollection().get(declaredAllowedTag).getAllElements()) {
							possibilities.add(new ItemStack(element, input.getCount()));
						}
					}
				}
			}
			inputs.add(possibilities);
		}
		if(i < 9) {
			ItemStack output = LargePatternItem.getOutputSlot(stack, i);
			if(!output.isEmpty()) {
				outputs.add(output);
			}
		}
	}

	private void fillProcessingFluids(int i, ItemStack stack, List<NonNullList<FluidStack>> fluidInputs, NonNullList<FluidStack> fluidOutputs, LargeAllowedTagList allowedTagList) throws CraftingPatternFactoryException {
		FluidStack input = PatternItem.getFluidInputSlot(stack, i);
		if(input.isEmpty()) {
			fluidInputs.add(NonNullList.create());
		}
		else {
			NonNullList<FluidStack> possibilities = NonNullList.create();
			possibilities.add(input.copy());
			if(allowedTagList != null) {
				Collection<ResourceLocation> tagsOfFluid = FluidTags.getCollection().getOwningTags(input.getFluid());
				Set<ResourceLocation> declaredAllowedTags = allowedTagList.getAllowedFluidTags().get(i);
				for(ResourceLocation declaredAllowedTag : declaredAllowedTags) {
					if(!tagsOfFluid.contains(declaredAllowedTag)) {
						throw new CraftingPatternFactoryException(
								new TranslationTextComponent(
										"misc.refinedstorage.pattern.error.tag_no_longer_applicable",
										declaredAllowedTag.toString(),
										input.getDisplayName()
										)
								);
					}
					else {
						for(Fluid element : FluidTags.getCollection().get(declaredAllowedTag).getAllElements()) {
							possibilities.add(new FluidStack(element, input.getAmount()));
						}
					}
				}
			}

			fluidInputs.add(possibilities);
		}
		if(i < 9) {
			FluidStack output = LargePatternItem.getFluidOutputSlot(stack, i);
			if(!output.isEmpty()) {
				fluidOutputs.add(output);
			}
		}
	}
}
