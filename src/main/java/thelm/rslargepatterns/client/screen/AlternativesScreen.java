package thelm.rslargepatterns.client.screen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.AllowedTagList;
import com.raoulvdberge.refinedstorage.render.FluidRenderer;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import thelm.rslargepatterns.client.screen.widget.CheckboxWidget;
import thelm.rslargepatterns.client.screen.widget.ScrollbarWidget;
import thelm.rslargepatterns.container.AlternativesContainer;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.network.packet.SetAllowedFluidTagsPacket;
import thelm.rslargepatterns.network.packet.SetAllowedItemTagsPacket;
import thelm.rslargepatterns.tile.LargePatternEncoderTile;
import thelm.rslargepatterns.util.LargeAllowedTagList;

public class AlternativesScreen extends BaseScreen<AlternativesContainer> {

	public static final ResourceLocation BACKGROUND = new ResourceLocation("refinedstorage:textures/gui/alternatives.png");

	private final BaseScreen<?> parent;
	private final LargePatternEncoderTile tile;
	private final ScrollbarWidget scrollbar;

	private final List<Line> lines = new ArrayList<>();

	private int type;
	private int slot;
	private ItemStack item;
	private FluidStack fluid;

	private AlternativesScreen(BaseScreen<?> parent, PlayerInventory playerInventory, ITextComponent title, LargePatternEncoderTile tile) {
		super(new AlternativesContainer(playerInventory), playerInventory, title);
		xSize = 175;
		ySize = 143;
		this.parent = parent;
		this.tile = tile;
		this.scrollbar = new ScrollbarWidget(this, 155, 20, 12, 89);
	}

	public AlternativesScreen(BaseScreen<?> parent, PlayerInventory playerInventory, ITextComponent title, LargePatternEncoderTile tile, ItemStack item, int slot) {
		this(parent, playerInventory, title, tile);
		this.type = IType.ITEMS;
		this.slot = slot;
		this.item = item;
		this.fluid = null;
	}

	public AlternativesScreen(BaseScreen<?> parent, PlayerInventory playerInventory, ITextComponent title, LargePatternEncoderTile tile, FluidStack fluid, int slot) {
		this(parent, playerInventory, title, tile);
		this.type = IType.FLUIDS;
		this.slot = slot;
		this.item = null;
		this.fluid = fluid;
	}

	@Override
	protected ResourceLocation getBackgroundTexture() {
		return BACKGROUND;
	}

	@Override
	public void init() {
		super.init();
		lines.clear();
		if(item != null) {
			lines.add(new ItemLine(item));
			for(ResourceLocation owningTag : ItemTags.getCollection().getOwningTags(item.getItem())) {
				lines.add(new TagLine(owningTag, tile.allowedTagList.getAllowedItemTags().get(slot).contains(owningTag)));
				int itemCount = 0;
				ItemListLine line = new ItemListLine();
				for(Item item : ItemTags.getCollection().get(owningTag).getAllElements()) {
					if(itemCount > 0 && itemCount % 8 == 0) {
						lines.add(line);
						line = new ItemListLine();
					}
					itemCount++;
					line.addItem(new ItemStack(item));
				}
				lines.add(line);
			}
		}
		else if(fluid != null) {
			lines.add(new FluidLine(fluid));
			for(ResourceLocation owningTag : FluidTags.getCollection().getOwningTags(fluid.getFluid())) {
				lines.add(new TagLine(owningTag, tile.allowedTagList.getAllowedFluidTags().get(slot).contains(owningTag)));
				int fluidCount = 0;
				FluidListLine line = new FluidListLine();
				for(Fluid fluid : FluidTags.getCollection().get(owningTag).getAllElements()) {
					if(fluidCount > 0 && fluidCount % 8 == 0) {
						lines.add(line);
						line = new FluidListLine();
					}
					fluidCount++;
					line.addFluid(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
				}
				lines.add(line);
			}
		}
		int xx = 8;
		int yy = 20;
		for(int i = 0; i < lines.size(); ++i) {
			boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + getVisibleRows();
			if(visible) {
				lines.get(i).layoutDependantControls(true, guiLeft + xx + 3, guiTop + yy + 3);
				yy += 18;
			}
		}
		Button apply = addButton(guiLeft+7, guiTop+114, 50, 20, I18n.format("gui.refinedstorage.alternatives.apply"), btn->apply());
		apply.active = lines.size() > 1;
		addButton(guiLeft+apply.getWidth()+7+4, guiTop+114, 50, 20, I18n.format("gui.cancel"), btn -> close());
	}

	@Override
	public void tick() {
		scrollbar.setEnabled(getRows() > getVisibleRows());
		scrollbar.setMaxOffset(getRows() - getVisibleRows());
	}

	private int getRows() {
		return lines.size();
	}

	private int getVisibleRows() {
		return 5;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		font.drawString(title.getFormattedText(), 7, 7, 0x404040);
		int x = 8;
		int y = 20;
		for(int i = 0; i < lines.size(); ++i) {
			boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + getVisibleRows();
			if (visible) {
				lines.get(i).layoutDependantControls(true, guiLeft + x + 3, guiTop + y + 3);
				lines.get(i).render(x, y);
				y += 18;
			}
			else {
				lines.get(i).layoutDependantControls(false, -100, -100);
			}
		}
		x = 8;
		y = 20;
		for(int i = 0; i < lines.size(); ++i) {
			boolean visible = i >= scrollbar.getOffset() && i < scrollbar.getOffset() + getVisibleRows();
			if(visible) {
				lines.get(i).renderTooltip(x, y, mouseX, mouseY);
				y += 18;
			}
		}
	}

	@Override
	public void mouseMoved(double mx, double my) {
		scrollbar.mouseMoved(mx, my);
		super.mouseMoved(mx, my);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		return scrollbar.mouseClicked(mx, my, button) || super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		return scrollbar.mouseReleased(mx, my, button) || super.mouseReleased(mx, my, button);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double delta) {
		return scrollbar.mouseScrolled(x, y, delta) || super.mouseScrolled(x, y, delta);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if(key == GLFW.GLFW_KEY_ESCAPE) {
			close();
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	private void close() {
		minecraft.displayGuiScreen(parent);
	}

	private void apply() {
		Set<ResourceLocation> allowed = new HashSet<>();
		for(Line line : lines) {
			if(line instanceof TagLine) {
				TagLine tagLine = (TagLine) line;
				if(tagLine.widget.isChecked()) {
					allowed.add(tagLine.tagName);
				}
			}
		}
		LargeAllowedTagList allowedTagList = tile.allowedTagList;
		if(type == IType.ITEMS) {
			List<Set<ResourceLocation>> existing = allowedTagList.getAllowedItemTags();
			existing.set(slot, allowed);
			PacketHandler.INSTANCE.sendToServer(new SetAllowedItemTagsPacket(existing));
		}
		else if(type == IType.FLUIDS) {
			List<Set<ResourceLocation>> existing = allowedTagList.getAllowedFluidTags();
			existing.set(slot, allowed);
			PacketHandler.INSTANCE.sendToServer(new SetAllowedFluidTagsPacket(existing));
		}
		close();
	}

	private interface Line {

		default void render(int x, int y) {
		}

		default void renderTooltip(int x, int y, int mx, int my) {
		}

		default void layoutDependantControls(boolean visible, int x, int y) {
		}
	}

	private class ItemLine implements Line {

		private final ItemStack item;

		public ItemLine(ItemStack item) {
			this.item = item;
		}

		@Override
		public void render(int x, int y) {
			RenderSystem.color4f(1, 1, 1, 1);
			itemRenderer.renderItemAndEffectIntoGUI(item, x+3, y+2);
			font.drawString(item.getDisplayName().getFormattedText(), x+4+19, y+7, 0x404040);
		}
	}

	private class FluidLine implements Line {

		private final FluidStack fluid;

		public FluidLine(FluidStack item) {
			this.fluid = item;
		}

		@Override
		public void render(int x, int y) {
			FluidRenderer.INSTANCE.render(x+3, y+2, fluid);
			font.drawString(fluid.getDisplayName().getFormattedText(), x+4+19, y+7, 0x404040);
		}
	}

	private class TagLine implements Line {

		private final ResourceLocation tagName;
		private final CheckboxWidget widget;

		public TagLine(ResourceLocation tagName, boolean checked) {
			this.tagName = tagName;
			this.widget = addCheckBox(-100, -100, RenderUtils.shorten(tagName.toString(), 22), checked, btn->{});
			widget.setFGColor(0xFF373737);
			widget.setShadow(false);
		}

		@Override
		public void layoutDependantControls(boolean visible, int x, int y) {
			widget.visible = visible;
			widget.x = x;
			widget.y = y;
		}
	}

	private class ItemListLine implements Line {

		private final List<ItemStack> items = new ArrayList<>();

		public ItemListLine addItem(ItemStack stack) {
			items.add(stack);
			return this;
		}

		@Override
		public void render(int x, int y) {
			for(ItemStack item : items) {
				itemRenderer.renderItemAndEffectIntoGUI(item, x+3, y);
				x += 17;
			}
		}

		@Override
		public void renderTooltip(int x, int y, int mx, int my) {
			for(ItemStack item : items) {
				if(RenderUtils.inBounds(x+3, y, 16, 16, mx, my)) {
					AlternativesScreen.this.renderTooltip(item, mx, my);
				}
				x += 17;
			}
		}
	}

	private class FluidListLine implements Line {

		private final List<FluidStack> fluids = new ArrayList<>();

		public FluidListLine addFluid(FluidStack stack) {
			fluids.add(stack);
			return this;
		}

		@Override
		public void render(int x, int y) {
			for(FluidStack fluid : fluids) {
				FluidRenderer.INSTANCE.render(x + 3, y, fluid);
				x += 17;
			}
		}

		@Override
		public void renderTooltip(int x, int y, int mx, int my) {
			for(FluidStack fluid : fluids) {
				if(RenderUtils.inBounds(x + 3, y, 16, 16, mx, my)) {
					AlternativesScreen.this.renderTooltip(fluid.getDisplayName().getFormattedText(), mx, my);
				}
				x += 17;
			}
		}
	}
}
