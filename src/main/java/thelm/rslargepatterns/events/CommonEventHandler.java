package thelm.rslargepatterns.events;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.rslargepatterns.block.LargePatternEncoderBlock;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;
import thelm.rslargepatterns.item.LargePatternItem;
import thelm.rslargepatterns.network.PacketHandler;
import thelm.rslargepatterns.tile.LargePatternEncoderTile;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	@SubscribeEvent
	public void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(LargePatternEncoderBlock.INSTANCE);
	}

	@SubscribeEvent
	public void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(LargePatternEncoderBlock.ITEM_INSTANCE);
		event.getRegistry().register(LargePatternItem.INSTANCE);
	}

	@SubscribeEvent
	public void onTileRegister(RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().register(LargePatternEncoderTile.TYPE_INSTANCE);
	}

	@SubscribeEvent
	public void onContainerRegister(RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().register(LargePatternEncoderContainer.TYPE_INSTANCE);
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		PacketHandler.registerPackets();
	}
}
