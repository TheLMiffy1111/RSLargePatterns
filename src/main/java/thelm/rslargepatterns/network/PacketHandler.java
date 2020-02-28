package thelm.rslargepatterns.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import thelm.rslargepatterns.RSLargePatterns;
import thelm.rslargepatterns.network.packet.PacketClearPattern;
import thelm.rslargepatterns.network.packet.PacketCreatePattern;
import thelm.rslargepatterns.network.packet.PacketFluidSlotUpdate;
import thelm.rslargepatterns.network.packet.PacketSetFluidStack;
import thelm.rslargepatterns.network.packet.PacketSetItemStack;
import thelm.rslargepatterns.network.packet.PacketSetOredictPattern;
import thelm.rslargepatterns.network.packet.PacketSetProcessingType;
import thelm.rslargepatterns.network.packet.PacketSetRecipe;

public class PacketHandler<REQ extends ISelfHandleMessage<? extends IMessage>> implements IMessageHandler<REQ, IMessage> {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(RSLargePatterns.MOD_ID);

	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(get(), PacketClearPattern.class, id++, Side.SERVER);
		INSTANCE.registerMessage(get(), PacketCreatePattern.class, id++, Side.SERVER);
		INSTANCE.registerMessage(get(), PacketSetOredictPattern.class, id++, Side.SERVER);
		INSTANCE.registerMessage(get(), PacketSetProcessingType.class, id++, Side.SERVER);
		INSTANCE.registerMessage(get(), PacketSetItemStack.class, id++, Side.SERVER);
		INSTANCE.registerMessage(get(), PacketSetFluidStack.class, id++, Side.SERVER);
		INSTANCE.registerMessage(get(), PacketFluidSlotUpdate.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(get(), PacketSetRecipe.class, id++, Side.SERVER);
	}

	public static <REQ extends ISelfHandleMessage<? extends IMessage>> PacketHandler<REQ> get() {
		return new PacketHandler<>();
	}

	@Override
	public IMessage onMessage(REQ message, MessageContext ctx) {
		return message.onMessage(ctx);
	}
}
