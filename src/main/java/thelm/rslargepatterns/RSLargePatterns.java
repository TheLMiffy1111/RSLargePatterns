package thelm.rslargepatterns;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thelm.rslargepatterns.client.event.ClientEventHandler;
import thelm.rslargepatterns.events.CommonEventHandler;
import thelm.rslargepatterns.item.LargePatternItem;

@Mod(RSLargePatterns.MOD_ID)
public class RSLargePatterns {

	public static final String MOD_ID = "rslargepatterns";
	public static final ItemGroup ITEM_GROUP = new ItemGroup("rslargepatterns") {
		@OnlyIn(Dist.CLIENT)
		@Override
		public ItemStack createIcon() {
			return new ItemStack(LargePatternItem.INSTANCE);
		}
	};
	public static RSLargePatterns core;

	public RSLargePatterns() {
		core = this;
		FMLJavaModLoadingContext.get().getModEventBus().register(CommonEventHandler.getInstance());
		MinecraftForge.EVENT_BUS.register(CommonEventHandler.getInstance());
		DistExecutor.runWhenOn(Dist.CLIENT, ()->()->{
			FMLJavaModLoadingContext.get().getModEventBus().register(ClientEventHandler.getInstance());
			MinecraftForge.EVENT_BUS.register(ClientEventHandler.getInstance());
		});
	}
}
