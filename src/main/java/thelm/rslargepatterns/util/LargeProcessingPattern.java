package thelm.rslargepatterns.util;

import java.util.List;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.AllowedTagList;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.v5.CraftingTaskFactory;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class LargeProcessingPattern implements ICraftingPattern {

	private final ICraftingPatternContainer container;
	private final ItemStack stack;
	private final boolean valid;
	private final ITextComponent errorMessage;
	private final ICraftingRecipe recipe;
	private final List<NonNullList<ItemStack>> inputs;
	private final NonNullList<ItemStack> outputs;
	private final List<NonNullList<FluidStack>> fluidInputs;
	private final NonNullList<FluidStack> fluidOutputs;
	private final LargeAllowedTagList allowedTagList;

	public LargeProcessingPattern(ICraftingPatternContainer container, ItemStack stack, ITextComponent errorMessage, boolean valid, ICraftingRecipe recipe, List<NonNullList<ItemStack>> inputs, NonNullList<ItemStack> outputs, List<NonNullList<FluidStack>> fluidInputs, NonNullList<FluidStack> fluidOutputs, LargeAllowedTagList allowedTagList) {
		this.container = container;
		this.stack = stack;
		this.valid = valid;
		this.errorMessage = errorMessage;
		this.recipe = recipe;
		this.inputs = inputs;
		this.outputs = outputs;
		this.fluidInputs = fluidInputs;
		this.fluidOutputs = fluidOutputs;
		this.allowedTagList = allowedTagList;
	}

	public LargeAllowedTagList getAllowedTagList() {
		return allowedTagList;
	}

	@Override
	public ICraftingPatternContainer getContainer() {
		return container;
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public ITextComponent getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean isProcessing() {
		return true;
	}

	@Override
	public List<NonNullList<ItemStack>> getInputs() {
		return inputs;
	}

	@Override
	public NonNullList<ItemStack> getOutputs() {
		return outputs;
	}

	@Override
	public ItemStack getOutput(NonNullList<ItemStack> took) {
		throw new IllegalStateException("Cannot get crafting output from processing pattern");
	}

	@Override
	public NonNullList<ItemStack> getByproducts() {
		throw new IllegalStateException("Cannot get byproduct outputs from processing pattern");
	}

	@Override
	public NonNullList<ItemStack> getByproducts(NonNullList<ItemStack> took) {
		throw new IllegalStateException("Cannot get byproduct outputs from processing pattern");
	}

	@Override
	public List<NonNullList<FluidStack>> getFluidInputs() {
		return fluidInputs;
	}

	@Override
	public NonNullList<FluidStack> getFluidOutputs() {
		return fluidOutputs;
	}

	@Override
	public ResourceLocation getCraftingTaskFactoryId() {
		return CraftingTaskFactory.ID;
	}

	@Override
	public boolean canBeInChainWith(ICraftingPattern other) {
		if(!other.isProcessing()) {
			return false;
		}
		if((other.getInputs().size() != inputs.size()) ||
				(other.getFluidInputs().size() != fluidInputs.size()) ||
				(other.getOutputs().size() != outputs.size()) ||
				(other.getFluidOutputs().size() != fluidOutputs.size())) {
			return false;
		}
		for(int i = 0; i < inputs.size(); ++i) {
			List<ItemStack> inputs = this.inputs.get(i);
			List<ItemStack> otherInputs = other.getInputs().get(i);
			if(inputs.size() != otherInputs.size()) {
				return false;
			}
			for(int j = 0; j < inputs.size(); ++j) {
				if(!API.instance().getComparer().isEqual(inputs.get(j), otherInputs.get(j))) {
					return false;
				}
			}
		}
		for(int i = 0; i < fluidInputs.size(); ++i) {
			List<FluidStack> inputs = this.fluidInputs.get(i);
			List<FluidStack> otherInputs = other.getFluidInputs().get(i);
			if(inputs.size() != otherInputs.size()) {
				return false;
			}
			for(int j = 0; j < inputs.size(); ++j) {
				if(!API.instance().getComparer().isEqual(inputs.get(j), otherInputs.get(j), IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY)) {
					return false;
				}
			}
		}
		for(int i = 0; i < outputs.size(); ++i) {
			if(!API.instance().getComparer().isEqual(outputs.get(i), other.getOutputs().get(i))) {
				return false;
			}
		}
		for(int i = 0; i < fluidOutputs.size(); ++i) {
			if(!API.instance().getComparer().isEqual(fluidOutputs.get(i), other.getFluidOutputs().get(i), IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getChainHashCode() {
		int result = 0;
		for(List<ItemStack> inputs : this.inputs) {
			for(ItemStack input : inputs) {
				result = 31 * result + API.instance().getItemStackHashCode(input);
			}
		}
		for(List<FluidStack> inputs : this.fluidInputs) {
			for(FluidStack input : inputs) {
				result = 31 * result + API.instance().getFluidStackHashCode(input);
			}
		}
		for(ItemStack output : this.outputs) {
			result = 31 * result + API.instance().getItemStackHashCode(output);
		}

		for(FluidStack output : this.fluidOutputs) {
			result = 31 * result + API.instance().getFluidStackHashCode(output);
		}
		return result;
	}
}