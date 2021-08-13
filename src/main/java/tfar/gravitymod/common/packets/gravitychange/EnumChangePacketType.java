package tfar.gravitymod.common.packets.gravitychange;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import tfar.gravitymod.api.EnumGravityDirection;
import tfar.gravitymod.common.packets.IMessageHelper;

import java.util.UUID;

/**
 * Created by Mysteryem on 2016-10-13.
 */
public enum EnumChangePacketType implements IMessageHelper<GravityChangeMessage> {
    SINGLE {
        @Override
        public void writeToBuff(GravityChangeMessage message, ByteBuf buf) {
            buf.writeLong(message.toSend.getMostSignificantBits());
            buf.writeLong(message.toSend.getLeastSignificantBits());
            buf.writeInt(message.newGravityDirection.ordinal());
            buf.writeBoolean(message.noTimeout);
        }

        @Override
        public void readFromBuff(GravityChangeMessage message, ByteBuf buf) {
            long most = buf.readLong();
            long least = buf.readLong();
            message.toSend = new UUID(most,least);
            message.newGravityDirection = EnumGravityDirection.getSafeDirectionFromOrdinal(buf.readInt());
            message.noTimeout = buf.readBoolean();
        }
    },
    CLIENT_REQUEST_GRAVITY_OF_PLAYER {
        @Override
        public void writeToBuff(GravityChangeMessage message, ByteBuf buf) {
            buf.writeLong(message.toSend.getMostSignificantBits());
            buf.writeLong(message.toSend.getLeastSignificantBits());        }

        @Override
        public void readFromBuff(GravityChangeMessage message, ByteBuf buf) {
            long most = buf.readLong();
            long least = buf.readLong();
            message.toSend = new UUID(most,least);        }
    }
}
