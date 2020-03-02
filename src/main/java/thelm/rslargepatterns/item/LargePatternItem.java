package thelm.rslargepatterns.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.render.Styles;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.client.renderer.LargePatternItemStackRenderer;
import thelm.rslargepatterns.util.LargeAllowedTagList;
import thelm.rslargepatterns.util.LargeProcessingPattern;
import thelm.rslargepatterns.util.LargeProcessingPatternFactory;

public class LargePatternItem extends Item implements ICraftingPatternProvider {

	public static final Item INSTANCE = new LargePatternItem();
	private static Map<ItemStack, LargeProcessingPattern> CACHE = new HashMap<>();

	private static final String NBT_VERSION = "Version";
	private static final String NBT_INPUT_SLOT = "Input_%d";
	private static final String NBT_OUTPUT_SLOT = "Output_%d";
	private static final String NBT_FLUID_INPUT_SLOT = "FluidInput_%d";
	private static final String NBT_FLUID_OUTPUT_SLOT = "FluidOutput_%d";
	private static final String NBT_ALLOWED_TAGS = "AllowedTags";

	private static final int VERSION = 1;

	public LargePatternItem() {
		super(new Item.Properties().group(RSLargePatterns.ITEM_GROUP).setISTER(()->LargePatternItemStackRenderer::new));
		setRegistryName("rslargepatterns:large_pattern");
	}

	public static LargeProcessingPattern fromCache(World world, ItemStack stack) {
		if(!CACHE.containsKey(stack)) {
			CACHE.put(stack, LargeProcessingPatternFactory.INSTANCE.create(world, null, stack));
		}
		return CACHE.get(stack);
	}

	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		super.addInformation(stack, world, tooltip, flag);
		if(!stack.hasTag()) {
			return;
		}
		LargeProcessingPattern pattern = fromCache(world, stack);
		if(pattern.isValid()) {
			tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.inputs").setStyle(Styles.YELLOW));
			RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getInputs().stream().map(i -> i.size() > 0 ? i.get(0) : ItemStack.EMPTY).collect(Collectors.toList()));
			RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidInputs().stream().map(i -> i.size() > 0 ? i.get(0) : FluidStack.EMPTY).collect(Collectors.toList()));
			tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.outputs").setStyle(Styles.YELLOW));
			RenderUtils.addCombinedItemsToTooltip(tooltip, true, pattern.getOutputs());
			RenderUtils.addCombinedFluidsToTooltip(tooltip, true, pattern.getFluidOutputs());
			if(pattern.getAllowedTagList() != null) {
				for(int i = 0; i < pattern.getAllowedTagList().getAllowedItemTags().size(); ++i) {
					Set<ResourceLocation> allowedTags = pattern.getAllowedTagList().getAllowedItemTags().get(i);

					for(ResourceLocation tag : allowedTags) {
						tooltip.add(new TranslationTextComponent(
								"misc.refinedstorage.pattern.allowed_item_tag",
								tag.toString(),
								pattern.getInputs().get(i).get(0).getDisplayName()
								).setStyle(Styles.AQUA));
					}
				}
				for(int i = 0; i < pattern.getAllowedTagList().getAllowedFluidTags().size(); ++i) {
					Set<ResourceLocation> allowedTags = pattern.getAllowedTagList().getAllowedFluidTags().get(i);
					for(ResourceLocation tag : allowedTags) {
						tooltip.add(new TranslationTextComponent(
								"misc.refinedstorage.pattern.allowed_fluid_tag",
								tag.toString(),
								pattern.getFluidInputs().get(i).get(0).getDisplayName()
								).setStyle(Styles.AQUA));
					}
				}
			}
			tooltip.add(new TranslationTextComponent("misc.refinedstorage.processing").setStyle(Styles.BLUE));
		}
		else {
			tooltip.add(new TranslationTextComponent("misc.refinedstorage.pattern.invalid").setStyle(Styles.RED));
			tooltip.add(pattern.getErrorMessage().setStyle(Styles.GRAY));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(!world.isRemote && player.isCrouching()) {
			return new ActionResult<>(ActionResultType.SUCCESS, new ItemStack(INSTANCE, player.getHeldItem(hand).getCount()));
		}
		return new ActionResult<>(ActionResultType.PASS, player.getHeldItem(hand));
	}

	@Override
	public ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container) {
		return LargeProcessingPatternFactory.INSTANCE.create(world, container, stack);
	}

	public static void setInputSlot(ItemStack pattern, int slot, ItemStack stack) {
		if(!pattern.hasTag()) {
			pattern.setTag(new CompoundNBT());
		}
		pattern.getTag().put(String.format(NBT_INPUT_SLOT, slot), stack.serializeNBT());
	}

	public static ItemStack getInputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_INPUT_SLOT, slot);
		if(!pattern.hasTag() || !pattern.getTag().contains(id)) {
			return ItemStack.EMPTY;
		}
		return ItemStack.read(pattern.getTag().getCompound(id));
	}

	public static void setOutputSlot(ItemStack pattern, int slot, ItemStack stack) {
		if(!pattern.hasTag()) {
			pattern.setTag(new CompoundNBT());
		}
		pattern.getTag().put(String.format(NBT_OUTPUT_SLOT, slot), stack.serializeNBT());
	}

	public static ItemStack getOutputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_OUTPUT_SLOT, slot);
		if(!pattern.hasTag() || !pattern.getTag().contains(id)) {
			return ItemStack.EMPTY;
		}
		return ItemStack.read(pattern.getTag().getCompound(id));
	}

	public static void setFluidInputSlot(ItemStack pattern, int slot, FluidStack stack) {
		if(!pattern.hasTag()) {
			pattern.setTag(new CompoundNBT());
		}
		pattern.getTag().put(String.format(NBT_FLUID_INPUT_SLOT, slot), stack.writeToNBT(new CompoundNBT()));
	}

	public static FluidStack getFluidInputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_FLUID_INPUT_SLOT, slot);

		if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
			return FluidStack.EMPTY;
		}
		return FluidStack.loadFluidStackFromNBT(pattern.getTag().getCompound(id));
	}

	public static void setFluidOutputSlot(ItemStack pattern, int slot, FluidStack stack) {
		if (!pattern.hasTag()) {
			pattern.setTag(new CompoundNBT());
		}
		pattern.getTag().put(String.format(NBT_FLUID_OUTPUT_SLOT, slot), stack.writeToNBT(new CompoundNBT()));
	}

	public static FluidStack getFluidOutputSlot(ItemStack pattern, int slot) {
		String id = String.format(NBT_FLUID_OUTPUT_SLOT, slot);

		if (!pattern.hasTag() || !pattern.getTag().contains(id)) {
			return FluidStack.EMPTY;
		}

		return FluidStack.loadFluidStackFromNBT(pattern.getTag().getCompound(id));
	}

	public static void setToCurrentVersion(ItemStack pattern) {
		if(!pattern.hasTag()) {
			pattern.setTag(new CompoundNBT());
		}
		pattern.getTag().putInt(NBT_VERSION, VERSION);
	}

	public static void setAllowedTags(ItemStack pattern, LargeAllowedTagList allowedTagList) {
		if(!pattern.hasTag()) {
			pattern.setTag(new CompoundNBT());
		}
		pattern.getTag().put(NBT_ALLOWED_TAGS, allowedTagList.writeToNbt());
	}

	public static LargeAllowedTagList getAllowedTags(ItemStack pattern) {
		if(!pattern.hasTag() || !pattern.getTag().contains(NBT_ALLOWED_TAGS)) {
			return null;
		}
		LargeAllowedTagList allowedTagList = new LargeAllowedTagList();
		allowedTagList.readFromNbt(pattern.getTag().getCompound(NBT_ALLOWED_TAGS));
		return allowedTagList;
	}
}
