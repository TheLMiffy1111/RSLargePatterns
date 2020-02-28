package thelm.rslargepatterns.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.block.BlockLargePatternEncoder;
import thelm.rslargepatterns.config.RSLargePatternsConfig;
import thelm.rslargepatterns.item.ItemLargePattern;
import thelm.rslargepatterns.network.GuiHandler;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.tile.TileLargePatternEncoder;

public class CommonProxy {

	public void registerBlock(Block block) {
		ForgeRegistries.BLOCKS.register(block);
	}

	public void registerItem(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}

	public void register(FMLPreInitializationEvent event) {
		registerConfig(event);
		registerBlocks();
		registerItems();
		registerModels();
		registerTileEntities();
		registerNetwork();
	}

	public void register(FMLInitializationEvent event) {
		registerColors();
	}

	protected void registerConfig(FMLPreInitializationEvent event) {
		RSLargePatternsConfig.init(event.getSuggestedConfigurationFile());
	}

	protected void registerBlocks() {
		registerBlock(BlockLargePatternEncoder.INSTANCE);
	}

	protected void registerItems() {
		registerItem(BlockLargePatternEncoder.ITEM_INSTANCE);
		registerItem(ItemLargePattern.INSTANCE);
	}

	protected void registerModels() {}

	protected void registerTileEntities() {
		GameRegistry.registerTileEntity(TileLargePatternEncoder.class, new ResourceLocation("rslargepatterns:large_pattern_encoder"));
	}

	protected void registerNetwork() {
		NetworkRegistry.INSTANCE.registerGuiHandler(RSLargePatterns.instance, GuiHandler.INSTANCE);
		PacketHandler.registerPackets();
	}

	protected void registerColors() {}
}
