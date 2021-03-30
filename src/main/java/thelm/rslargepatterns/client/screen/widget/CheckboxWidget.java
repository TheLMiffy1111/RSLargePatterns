package thelm.rslargepatterns.client.screen.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class CheckboxWidget extends CheckboxButton {

	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	private static final int BOX_WIDTH = 13;

	private final Consumer<CheckboxButton> onPress;
	private boolean shadow = true;

	public CheckboxWidget(int x, int y, String text, boolean isChecked, Consumer<CheckboxButton> onPress) {
		super(x, y, Minecraft.getInstance().fontRenderer.getStringWidth(text)+BOX_WIDTH, 10, new StringTextComponent(text), isChecked);
		this.onPress = onPress;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	@Override
	public void onPress() {
		super.onPress();
		onPress.accept(this);
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mx, int my, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		FontRenderer fontRenderer = mc.fontRenderer;
		RenderSystem.color4f(1F, 1F, 1F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		blit(matrixStack, x, y, isFocused() ? 10.0F : 0.0F, isChecked() ? 10F : 0F, 10, 10, 32, 32);
		renderBg(matrixStack, mc, mx, my);
		int color = 0xE0E0E0;
		if(!active) {
			color = 0xA0A0A0;
		}
		else if (packedFGColor != 0) {
			color = packedFGColor;
		}
		if(shadow) {
			super.drawString(matrixStack, fontRenderer, getMessage(), x+13, y+(height-8)/2, color);
		}
		else {
			fontRenderer.func_243248_b(matrixStack, getMessage(), x+13, y+(height-8)/2F, color);
		}
	}
}