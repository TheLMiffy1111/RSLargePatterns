package thelm.rslargepatterns.network.packet;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;

public class CreatePatternPacket {

	public CreatePatternPacket() {}

	public static void encode(CreatePatternPacket pkt, PacketBuffer buf) {

	}

	public static CreatePatternPacket decode(PacketBuffer buf) {
		return new CreatePatternPacket();
	}

	public static void handle(CreatePatternPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.openContainer instanceof LargePatternEncoderContainer) {
				LargePatternEncoderContainer container = (LargePatternEncoderContainer)player.openContainer;
				container.tile.onCreatePattern();
			}
		});
	}
}
