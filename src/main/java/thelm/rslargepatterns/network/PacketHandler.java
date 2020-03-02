package thelm.rslargepatterns.network;

import java.util.Optional;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import thelm.rslargepatterns.network.packet.ClearPatternPacket;
import thelm.rslargepatterns.network.packet.CreatePatternPacket;
import thelm.rslargepatterns.network.packet.FluidSlotUpdatePacket;
import thelm.rslargepatterns.network.packet.SetAllowedFluidTagsPacket;
import thelm.rslargepatterns.network.packet.SetAllowedItemTagsPacket;
import thelm.rslargepatterns.network.packet.SetFluidStackPacket;
import thelm.rslargepatterns.network.packet.SetItemStackPacket;
import thelm.rslargepatterns.network.packet.SetProcessingTypePacket;
import thelm.rslargepatterns.network.packet.SetRecipePacket;

public class PacketHandler {

	public static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("rslargepatterns", PROTOCOL_VERSION),
			()->PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(id++, ClearPatternPacket.class,
				ClearPatternPacket::encode, ClearPatternPacket::decode,
				ClearPatternPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, CreatePatternPacket.class,
				CreatePatternPacket::encode, CreatePatternPacket::decode,
				CreatePatternPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, SetProcessingTypePacket.class,
				SetProcessingTypePacket::encode, SetProcessingTypePacket::decode,
				SetProcessingTypePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, SetItemStackPacket.class,
				SetItemStackPacket::encode, SetItemStackPacket::decode,
				SetItemStackPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, SetFluidStackPacket.class,
				SetFluidStackPacket::encode, SetFluidStackPacket::decode,
				SetFluidStackPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, SetAllowedItemTagsPacket.class,
				SetAllowedItemTagsPacket::encode, SetAllowedItemTagsPacket::decode,
				SetAllowedItemTagsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, SetAllowedFluidTagsPacket.class,
				SetAllowedFluidTagsPacket::encode, SetAllowedFluidTagsPacket::decode,
				SetAllowedFluidTagsPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		INSTANCE.registerMessage(id++, FluidSlotUpdatePacket.class,
				FluidSlotUpdatePacket::encode, FluidSlotUpdatePacket::decode,
				FluidSlotUpdatePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		INSTANCE.registerMessage(id++, SetRecipePacket.class,
				SetRecipePacket::encode, SetRecipePacket::decode,
				SetRecipePacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}
}
