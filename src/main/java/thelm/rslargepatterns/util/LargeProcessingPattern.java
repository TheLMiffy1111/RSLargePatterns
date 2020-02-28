package thelm.rslargepatterns.util;

import java.util.ArrayList;
import java.util.List;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.registry.CraftingTaskFactory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import thelm.rslargepatterns.item.ItemLargePattern;

/**
 * Code copied from Refined Storage to "maintain the coding style"
 */
public class LargeProcessingPattern implements ICraftingPattern {

	private ICraftingPatternContainer container;
	private ItemStack stack;
	private boolean oredict;
	private boolean valid;
	private IRecipe recipe;
	private List<NonNullList<ItemStack>> inputs = new ArrayList<>();
	private NonNullList<ItemStack> outputs = NonNullList.create();
	private NonNullList<ItemStack> byproducts = NonNullList.create();
	private NonNullList<FluidStack> fluidInputs = NonNullList.create();
	private NonNullList<FluidStack> fluidOutputs = NonNullList.create();

	public LargeProcessingPattern(World world, ICraftingPatternContainer container, ItemStack stack) {
		this.container = container;
		this.stack = stack;
		this.oredict = ItemLargePattern.isOredict(stack);
		for(int i = 0; i < 81; ++i) {
			ItemStack input = ItemLargePattern.getInputSlot(stack, i);
			if(input.isEmpty()) {
				inputs.add(NonNullList.create());
			}
			else if(oredict) {
				NonNullList<ItemStack> ores = NonNullList.create();
				ores.add(input.copy());
				for(int id : OreDictionary.getOreIDs(input)) {
					String name = OreDictionary.getOreName(id);
					for(ItemStack ore : OreDictionary.getOres(name)) {
						if(ore.getMetadata() == OreDictionary.WILDCARD_VALUE) {
							ore.getItem().getSubItems(CreativeTabs.SEARCH, ores);
						}
						else {
							ores.add(ore.copy());
						}
					}
				}
				for(ItemStack ore : ores) {
					ore.setCount(input.getCount());
				}
				inputs.add(ores);
			}
			else {
				inputs.add(NonNullList.from(ItemStack.EMPTY, input));
			}
			if(i < 9) {
				ItemStack output = ItemLargePattern.getOutputSlot(stack, i);
				if(!output.isEmpty()) {
					this.valid = true; // As soon as we have one output, we are valid.
					outputs.add(output);
				}
				FluidStack fluidInput = ItemLargePattern.getFluidInputSlot(stack, i);
				if(fluidInput != null) {
					this.valid = true;
					fluidInputs.add(fluidInput);
				}
				FluidStack fluidOutput = ItemLargePattern.getFluidOutputSlot(stack, i);
				if(fluidOutput != null) {
					this.valid = true;
					fluidOutputs.add(fluidOutput);
				}
			}
		}
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
	public boolean isProcessing() {
		return true;
	}

	@Override
	public boolean isOredict() {
		return oredict;
	}

	@Override
	public List<NonNullList<ItemStack>> getInputs() {
		return inputs;
	}

	@Override
	public NonNullList<ItemStack> getOutputs() {
		return outputs;
	}

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
	public NonNullList<FluidStack> getFluidInputs() {
		return fluidInputs;
	}

	@Override
	public NonNullList<FluidStack> getFluidOutputs() {
		return fluidOutputs;
	}

	@Override
	public String getId() {
		return CraftingTaskFactory.ID;
	}

	@Override
	public boolean canBeInChainWith(ICraftingPattern other) {
		if(!other.isProcessing() || other.isOredict() != oredict) {
			return false;
		}
		if(other.getInputs().size() != inputs.size() ||
				other.getFluidInputs().size() != fluidInputs.size() ||
				other.getOutputs().size() != outputs.size() ||
				other.getFluidOutputs().size() != fluidOutputs.size()) {
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
			if(!API.instance().getComparer().isEqual(fluidInputs.get(i), other.getFluidInputs().get(i), IComparer.COMPARE_NBT | IComparer.COMPARE_QUANTITY)) {
				return false;
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
		result = 31 * result + (oredict ? 1 : 0);
		for(List<ItemStack> inputs : inputs) {
			for(ItemStack input : inputs) {
				result = 31 * result + API.instance().getItemStackHashCode(input);
			}
		}
		for(FluidStack input : fluidInputs) {
			result = 31 * result + API.instance().getFluidStackHashCode(input);
		}
		for(ItemStack output : outputs) {
			result = 31 * result + API.instance().getItemStackHashCode(output);
		}
		for(FluidStack output : fluidOutputs) {
			result = 31 * result + API.instance().getFluidStackHashCode(output);
		}
		for(ItemStack byproduct : byproducts) {
			result = 31 * result + API.instance().getItemStackHashCode(byproduct);
		}
		return result;
	}
}