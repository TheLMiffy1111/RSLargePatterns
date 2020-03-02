package thelm.rslargepatterns.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import thelm.rslargepatterns.client.BakedModelLargePattern;
import thelm.rslargepatterns.client.IModelRegister;
import thelm.rslargepatterns.client.ItemColorLargePattern;
import thelm.rslargepatterns.client.ModelUtil;
import thelm.rslargepatterns.client.TileEntityItemStackRendererLargePattern;
import thelm.rslargepatterns.item.ItemLargePattern;

public class ClientProxy extends CommonProxy {

	private static List<IModelRegister> modelRegisterList = new ArrayList<>();

	@Override
	public void registerBlock(Block block) {
		super.registerBlock(block);
		if(block instanceof IModelRegister) {
			modelRegisterList.add((IModelRegister)block);
		}
	}

	@Override
	public void registerItem(Item item) {
		super.registerItem(item);
		if(item instanceof IModelRegister) {
			modelRegisterList.add((IModelRegister)item);
		}
	}

	@Override
	protected void registerModels() {
		MinecraftForge.EVENT_BUS.register(new ModelUtil());
		for(IModelRegister model : modelRegisterList) {
			model.registerModels();
		}
		ItemLargePattern.INSTANCE.setTileEntityItemStackRenderer(new TileEntityItemStackRendererLargePattern());
		ModelUtil.registerCallback(registry->{
			IBakedModel existingModel = registry.getObject(ItemLargePattern.MODEL_LOCATION);
			if(existingModel != null) {
				BakedModelLargePattern model = new BakedModelLargePattern(existingModel);
				registry.putObject(ItemLargePattern.MODEL_LOCATION, model);
			}
		});
	}

	@Override
	protected void registerColors() {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ItemColorLargePattern.INSTANCE, ItemLargePattern.INSTANCE);
	}
}
