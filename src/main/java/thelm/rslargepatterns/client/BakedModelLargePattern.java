package thelm.rslargepatterns.client;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thelm.rslargepatterns.item.ItemLargePattern;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class BakedModelLargePattern implements IBakedModel {

	protected final IBakedModel base;

	public BakedModelLargePattern(IBakedModel base) {
		this.base = base;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return base.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return base.isAmbientOcclusion();
	}

	@Override
	public boolean isAmbientOcclusion(IBlockState state) {
		return base.isAmbientOcclusion(state);
	}

	@Override
	public boolean isGui3d() {
		return base.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return base.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return base.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return base.getItemCameraTransforms();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		return base.handlePerspective(cameraTransformType);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList(base.getOverrides().getOverrides()) {
			@Override
			public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
				LargeProcessingPattern pattern = ItemLargePattern.getPatternFromCache(world, stack);
				if(canDisplayOutput(stack, pattern)) {
					ItemStack outputToRender = pattern.getOutputs().get(0);
					if(!hasBrokenRendering(outputToRender)) {
						return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(outputToRender, world, entity);
					}
				}
				return super.handleItemState(originalModel, stack, world, entity);
			}
		};
	}

	private boolean hasBrokenRendering(ItemStack stack) {
		if("gregtech".equals(stack.getItem().getCreatorModId(stack))) {
			if("tile.pipe".equals(stack.getTranslationKey())) {
				return true;
			}
			if("machine".equals(stack.getItem().delegate.name().getPath())) {
				return true;
			}
		}
		return false;
	}

	public static boolean canDisplayOutput(ItemStack patternStack, LargeProcessingPattern pattern) {
		if(pattern.isValid() && pattern.getOutputs().size() == 1) {
			for(ICraftingPatternRenderHandler renderHandler : API.instance().getPatternRenderHandlers()) {
				if(renderHandler.canRenderOutput(patternStack)) {
					return true;
				}
			}
		}
		return false;
	}
}
