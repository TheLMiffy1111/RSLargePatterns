package thelm.rslargepatterns.client.screen.widget;

import java.util.LinkedList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.integration.jei.GridRecipeTransferHandler;
import com.raoulvdberge.refinedstorage.integration.jei.JeiIntegration;
import com.raoulvdberge.refinedstorage.screen.widget.ScrollbarWidgetListener;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.util.ResourceLocation;
import thelm.rslargepatterns.client.screen.BaseScreen;

public class ScrollbarWidget implements IGuiEventListener {

	public static final ResourceLocation RS_ICONS = new ResourceLocation("refinedstorage:textures/icons.png");

	private static final int SCROLLER_HEIGHT = 15;

	private int x;
	private int y;
	private int width;
	private int height;
	private boolean enabled = false;

	private int offset;
	private int maxOffset;

	private boolean clicked = false;

	private List<ScrollbarWidgetListener> listeners = new LinkedList<>();

	private BaseScreen screen;

	public ScrollbarWidget(BaseScreen screen, int x, int y, int width, int height) {
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void addListener(ScrollbarWidgetListener listener) {
		listeners.add(listener);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void render() {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		screen.getMinecraft().textureManager.bindTexture(RS_ICONS);
		screen.blit(screen.getGuiLeft()+x, screen.getGuiTop()+y+(int)Math.min(height-SCROLLER_HEIGHT, (float)offset/(float)maxOffset*(float)(height-SCROLLER_HEIGHT)), isEnabled() ? 232 : 244, 0, 12, 15);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		mx -= screen.getGuiLeft();
		my -= screen.getGuiTop();
		if(button == 0 && RenderUtils.inBounds(x, y, width, height, mx, my)) {
			// Prevent accidental scrollbar click after clicking recipe transfer button
			//if(JeiIntegration.isLoaded() && System.currentTimeMillis()-GridRecipeTransferHandler.LAST_TRANSFER_TIME <= 200) {
			//	return false;
			//}
			updateOffset(my);
			clicked = true;
			return true;
		}
		return false;
	}

	@Override
	public void mouseMoved(double mx, double my) {
		mx -= screen.getGuiLeft();
		my -= screen.getGuiTop();
		if(clicked && RenderUtils.inBounds(x, y, width, height, mx, my)) {
			updateOffset(my);
		}
	}

	private void updateOffset(double my) {
		setOffset((int)Math.floor((float)(my-y)/(float)(height-SCROLLER_HEIGHT)*(float)maxOffset));
	}

	@Override
	public boolean mouseReleased(double mx, double my, int button) {
		if(clicked) {
			clicked = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if(isEnabled()) {
			setOffset(offset+Math.max(Math.min(-(int)scrollDelta, 1), -1));
			return true;
		}
		return false;
	}

	public void setMaxOffset(int maxOffset) {
		this.maxOffset = maxOffset;
		if(offset > maxOffset) {
			offset = Math.max(0, maxOffset);
		}
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		int oldOffset = this.offset;
		if(offset >= 0 && offset <= maxOffset) {
			this.offset = offset;
			listeners.forEach(l -> l.onOffsetChanged(oldOffset, offset));
		}
	}
}
