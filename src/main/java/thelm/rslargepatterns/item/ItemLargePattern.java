package thelm.rslargepatterns.item;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.client.IModelRegister;
import thelm.rslargepatterns.util.LargeProcessingPattern;

/**
 * Code copied from Refined Storage to "maintain the coding style"
 */
public class ItemLargePattern extends Item implements ICraftingPatternProvider, IModelRegister {

	public static final Item INSTANCE = new ItemLargePattern();
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation("rslargepatterns:large_pattern#inventory");
	private static Map<ItemStack, LargeProcessingPattern> PATTERN_CACHE = new WeakHashMap<>();

	public static final String NBT_INPUT_SLOT = "Input_%d";
	public static final String NBT_OUTPUT_SLOT = "Output_%d";
	private static final String NBT_FLUID_INPUT_SLOT = "FluidInput_%d";
	private static final String NBT_FLUID_OUTPUT_SLOT = "FluidOutput_%d";
	private static final String NBT_OREDICT = "Oredict";

	public static LargeProcessingPattern getPatternFromCache(World world, ItemStack stack) {
		if(!PATTERN_CACHE.containsKey(stack)) {
			PATTERN_CACHE.put(stack, new LargeProcessingPattern(world, null, stack));
		}
		return PATTERN_CACHE.get(stack);
	}

	protected ItemLargePattern() {
		setRegistryName("rslargepatterns:large_pattern");
		setTranslationKey("rslargepatterns.large_pattern");
		setCreativeTab(RSLargePatterns.CREATIVE_TAB);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		if(!stack.hasTagCompound()) {
			return;
		}
		ICraftingPattern pattern = getPatternFromCache(world, stack);
		if(pattern.isValid()) {
			tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.inputs") + TextFormatting.RESET);
			RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getInputs().stream().map(i -> i.size() > 0 ? i.get(0) : ItemStack.EMPTY).collect(Collectors.toList()));
			RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidInputs());
			tooltip.add(TextFormatting.YELLOW + I18n.format("misc.refinedstorage:pattern.outputs") + TextFormatting.RESET);
			RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getOutputs());
			RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidOutputs());
			if(isOredict(stack)) {
				tooltip.add(TextFormatting.BLUE + I18n.format("misc.refinedstorage:pattern.oredict") + TextFormatting.RESET);
			}
			tooltip.add(TextFormatting.BLUE + I18n.format("misc.refinedstorage:processing") + TextFormatting.RESET);
		}
		else {
			tooltip.add(TextFormatting.RED + I18n.format("misc.refinedstorage:pattern.invalid") + TextFormatting.RESET);
		}
	}

	public static void setInputSlot(ItemStack pattern, int slot, ItemStack stack) {
		if(!pattern.hasTagCompound()) {
			pattern.setTagCompound(new NBTTagCompound());
		}
		pattern.getTagCompound().setTag(String.format(NBT_INPUT_SLOT, slot), stack.serializeNBT());
	}

	public static ItemStack getInputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_INPUT_SLOT, slot);
		if(!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = new ItemStack(pattern.getTagCompound().getCompoundTag(id));
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	public static void setOutputSlot(ItemStack pattern, int slot, ItemStack stack) {
		if(!pattern.hasTagCompound()) {
			pattern.setTagCompound(new NBTTagCompound());
		}
		pattern.getTagCompound().setTag(String.format(NBT_OUTPUT_SLOT, slot), stack.serializeNBT());
	}

	public static ItemStack getOutputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_OUTPUT_SLOT, slot);
		if(!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = new ItemStack(pattern.getTagCompound().getCompoundTag(id));
		if(stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return stack;
	}

	public static void setFluidInputSlot(ItemStack pattern, int slot, FluidStack stack) {
		if(!pattern.hasTagCompound()) {
			pattern.setTagCompound(new NBTTagCompound());
		}
		pattern.getTagCompound().setTag(String.format(NBT_FLUID_INPUT_SLOT, slot), stack.writeToNBT(new NBTTagCompound()));
	}

	public static FluidStack getFluidInputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_FLUID_INPUT_SLOT, slot);
		if(!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(pattern.getTagCompound().getCompoundTag(id));
	}

	public static void setFluidOutputSlot(ItemStack pattern, int slot, FluidStack stack) {
		if(!pattern.hasTagCompound()) {
			pattern.setTagCompound(new NBTTagCompound());
		}
		pattern.getTagCompound().setTag(String.format(NBT_FLUID_OUTPUT_SLOT, slot), stack.writeToNBT(new NBTTagCompound()));
	}

	public static FluidStack getFluidOutputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_FLUID_OUTPUT_SLOT, slot);
		if(!pattern.hasTagCompound() || !pattern.getTagCompound().hasKey(id)) {
			return null;
		}
		return FluidStack.loadFluidStackFromNBT(pattern.getTagCompound().getCompoundTag(id));
	}

	public static boolean isOredict(ItemStack pattern) {
		return pattern.hasTagCompound() && pattern.getTagCompound().hasKey(NBT_OREDICT) && pattern.getTagCompound().getBoolean(NBT_OREDICT);
	}

	public static void setOredict(ItemStack pattern, boolean oredict) {
		if(!pattern.hasTagCompound()) {
			pattern.setTagCompound(new NBTTagCompound());
		}
		pattern.getTagCompound().setBoolean(NBT_OREDICT, oredict);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(!world.isRemote && player.isSneaking()) {
			return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(this, player.getHeldItem(hand).getCount()));
		}
		return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container) {
		return new LargeProcessingPattern(world, container, stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, MODEL_LOCATION);
	}
}
