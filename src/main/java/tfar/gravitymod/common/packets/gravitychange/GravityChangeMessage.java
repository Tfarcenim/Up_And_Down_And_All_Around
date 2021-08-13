package tfar.gravitymod.common.packets.gravitychange;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import tfar.gravitymod.api.EnumGravityDirection;
import tfar.mystlib.annotations.UsedReflexively;

import java.util.UUID;

/**
 * Created by Mysteryem on 2016-08-07.
 */
public class GravityChangeMessage implements IMessage {

    EnumGravityDirection newGravityDirection;
    boolean noTimeout;
    UUID toSend;
    private EnumChangePacketType packetType;

    @UsedReflexively
    public GravityChangeMessage() {/**/}

    public GravityChangeMessage(UUID stringToSend, EnumGravityDirection newGravityDirection, boolean noTimeout) {
        this.toSend = stringToSend;
        this.newGravityDirection = newGravityDirection;
        this.noTimeout = noTimeout;
        this.packetType = EnumChangePacketType.SINGLE;
    }

    public GravityChangeMessage(UUID playerToRequest) {
        this.toSend = playerToRequest;
        this.packetType = EnumChangePacketType.CLIENT_REQUEST_GRAVITY_OF_PLAYER;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        final int packetTypeOrdinal = buf.readInt();
        final EnumChangePacketType type = EnumChangePacketType.values()[packetTypeOrdinal];
        this.packetType = type;
        type.readFromBuff(this, buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.packetType.ordinal());
        this.packetType.writeToBuff(this, buf);
    }

    public EnumGravityDirection getNewGravityDirection() {
        return this.newGravityDirection;
    }

    public boolean getNoTimeout() {
        return this.noTimeout;
    }

    public EnumChangePacketType getPacketType() {
        return this.packetType;
    }

    public UUID getStringData() {
        return this.toSend;
    }

}
