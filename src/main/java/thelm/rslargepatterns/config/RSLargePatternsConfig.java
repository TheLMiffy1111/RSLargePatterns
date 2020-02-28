package thelm.rslargepatterns.config;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RSLargePatternsConfig {

	private RSLargePatternsConfig() {}

	public static Configuration config;

	public static void init(File file) {
		MinecraftForge.EVENT_BUS.register(RSLargePatternsConfig.class);
		config = new Configuration(file);
		config.load();
		init();
	}

	public static void init() {
		String category;
		if(config.hasChanged()) {
			config.save();
		}
	}

	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event) {
		if(event.getModID().equals("rslargepatterns")) {
			init();
		}
	}
}
