package cannonmod.core;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
 
public class MessageCannonGui implements IMessage {
 
    public int entityId;
    public byte type;
 
    public MessageCannonGui(){}
 
    public MessageCannonGui(int par1,byte type) {
        this.entityId= par1;
        this.type=type;
    }
 
    @Override//IMessage
    public void fromBytes(ByteBuf buf) {
        this.entityId= buf.readInt();
        this.type=buf.readByte();
    }
 
    @Override//IMessage
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeByte(this.type);
    }
}