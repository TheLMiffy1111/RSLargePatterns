package thelm.rslargepatterns.network.packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import thelm.rslargepatterns.container.LargePatternEncoderContainer;

public class SetAllowedFluidTagsPacket {

	private List<Set<ResourceLocation>> tags;

	public SetAllowedFluidTagsPacket(List<Set<ResourceLocation>> tags) {
		this.tags = tags;
	}

	public static void encode(SetAllowedFluidTagsPacket pkt, PacketBuffer buf) {
		buf.writeShort(pkt.tags.size());
		for(Set<ResourceLocation> values : pkt.tags) {
			buf.writeInt(values.size());
			for(ResourceLocation value : values) {
				buf.writeResourceLocation(value);
			}
		}
	}

	public static SetAllowedFluidTagsPacket decode(PacketBuffer buf) {
		List<Set<ResourceLocation>> tags = new ArrayList<>();
		for(int size = buf.readInt(), i = 0; i < size; ++i) {
			int setSize = buf.readInt();
			Set<ResourceLocation> values = new HashSet<>();
			for(int j = 0; j < setSize; ++j) {
				values.add(buf.readResourceLocation());
			}
			tags.add(values);
		}
		return new SetAllowedFluidTagsPacket(tags);
	}

	public static void handle(SetAllowedFluidTagsPacket pkt, Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		ctx.get().enqueueWork(()->{
			if(player.openContainer instanceof LargePatternEncoderContainer) {
				LargePatternEncoderContainer container = (LargePatternEncoderContainer)player.openContainer;
				container.tile.allowedTagList.setAllowedFluidTags(pkt.tags);
				container.tile.markDirty();
				container.tile.syncTile(false);
			}
		});
	}
}