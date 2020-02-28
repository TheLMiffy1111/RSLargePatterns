package thelm.rslargepatterns.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thelm.rslargepatterns.container.ContainerLargePatternEncoder;
import thelm.rslargepatterns.network.ISelfHandleMessage;

public class PacketClearPattern implements ISelfHandleMessage<IMessage> {

	public PacketClearPattern() {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public IMessage onMessage(MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().player;
		WorldServer world = player.getServerWorld();
		world.addScheduledTask(()->{
			if(player.openContainer instanceof ContainerLargePatternEncoder) {
				ContainerLargePatternEncoder container = (ContainerLargePatternEncoder)player.openContainer;
				container.tile.clearMatrix();
			}
		});
		return null;
	}
}
