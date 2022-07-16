package cannonmod.core;

import cannonmod.entity.EntityCannon;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCannonGuiHandler implements IMessageHandler<MessageCannonGui, IMessage> {

	@Override//IMessageHandler
	public IMessage onMessage(MessageCannonGui message, MessageContext ctx) {
		
		switch(message.type){
		case 0:
			ctx.getServerHandler().player.openGui(CannonCore.instance, GuiHandler.MOD_TILE_ENTITY_GUI, ctx.getServerHandler().player.world, message.entityId, 0, 0);
			break;
		case 1:
			Entity e1=ctx.getServerHandler().player.world.getEntityByID(message.entityId);
			if(e1 instanceof EntityCannon){
				/**a120*/
				((EntityCannon)e1).sendFireCommand();
			}
			break;
			/**a130*/
		case 2:
			Entity e2=ctx.getServerHandler().player.world.getEntityByID(message.entityId);
			if(e2 instanceof EntityCannon){
				((EntityCannon)e2).dropArtyAndInventoryItems();
				e2.setDead();
			}
			break;
		}
		return null;
		
	}
}