package thelm.rslargepatterns.network.packet;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;

public class SetProcessingTypePacket {

	private byte value;

	public SetProcessingTypePacket(byte value) {
		this.value = value;
	}

	public static void encode(SetProcessingTypePacket pkt, PacketBuffer buf) {
		buf.writeByte(pkt.value);
	}

	public static SetProcessingTypePacket decode(PacketBuffer buf) {
		return new SetProcessingTypePacket(buf.readByte());
	}

	public static void handle(SetProcessingTypePacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.openContainer instanceof LargePatternEncoderContainer) {
				LargePatternEncoderContainer container = (LargePatternEncoderContainer)player.openContainer;
				container.tile.processingType = pkt.value;
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
