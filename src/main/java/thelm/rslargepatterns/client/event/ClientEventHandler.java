package thelm.rslargepatterns.client.event;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thelm.rslargepatterns.client.color.LargePatternItemColor;
import thelm.rslargepatterns.client.model.LargePatternBakedModel;
import thelm.rslargepatterns.client.screen.LargePatternEncoderScreen;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;
import thelm.rslargepatterns.item.LargePatternItem;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(LargePatternEncoderContainer.TYPE_INSTANCE, LargePatternEncoderScreen::new);
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		ModelResourceLocation location = new ModelResourceLocation("rslargepatterns:large_pattern#inventory");
		IBakedModel existingModel = event.getModelRegistry().get(location);
		if(existingModel != null) {
			LargePatternBakedModel model = new LargePatternBakedModel(existingModel);
			event.getModelRegistry().put(location, model);
		}
	}

	@SubscribeEvent
	public void onColorHandler(ColorHandlerEvent.Item event) {
		event.getItemColors().register(LargePatternItemColor.INSTANCE, LargePatternItem.INSTANCE);
	}
}
