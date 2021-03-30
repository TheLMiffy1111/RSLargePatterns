package thelm.rslargepatterns.client.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.refinedmods.refinedstorage.apiimpl.API;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.util.LargeProcessingPattern;

public class LargePatternBakedModel implements IBakedModel {

	protected final IBakedModel base;

	public LargePatternBakedModel(IBakedModel base) {
		this.base = base;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return base.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return base.isAmbientOcclusion();
	}

	@Override
	public boolean isAmbientOcclusion(BlockState state) {
		return base.isAmbientOcclusion(state);
	}

	@Override
	public boolean isGui3d() {
		return base.isGui3d();
	}

	@Override
	public boolean isSideLit() {
		return base.isSideLit();
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
	public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
		return base.handlePerspective(cameraTransformType, mat);
	}

	@Override
	public ItemOverrideList getOverrides() {
		return new ItemOverrideList() {
			@Override
			public IBakedModel getOverrideModel(IBakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity) {
				if(entity != null) {
					LargeProcessingPattern pattern = LargePatternItem.fromCache(entity.world, stack);
					if(canDisplayOutput(stack, pattern)) {
						ItemStack outputToRender = pattern.getOutputs().get(0);
						return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(outputToRender, world, entity);
					}
				}
				return super.getOverrideModel(model, stack, world, entity);
			}

			@Override
			public ImmutableList<ItemOverride> getOverrides() {
				return (ImmutableList<ItemOverride>)base.getOverrides().getOverrides();
			}
		};
	}

	public static boolean canDisplayOutput(final ItemStack patternStack, LargeProcessingPattern pattern) {
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
