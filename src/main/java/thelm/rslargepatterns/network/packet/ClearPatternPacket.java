package thelm.rslargepatterns.network.packet;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;

public class ClearPatternPacket {

	public ClearPatternPacket() {}

	public static void encode(ClearPatternPacket pkt, PacketBuffer buf) {

	}

	public static ClearPatternPacket decode(PacketBuffer buf) {
		return new ClearPatternPacket();
	}

	public static void handle(ClearPatternPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.openContainer instanceof LargePatternEncoderContainer) {
				LargePatternEncoderContainer container = (LargePatternEncoderContainer)player.openContainer;
				container.tile.clearMatrix();
			}
		});
	}
}
