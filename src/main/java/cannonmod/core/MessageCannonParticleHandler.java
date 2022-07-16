package cannonmod.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
/**a130*/
public class MessageCannonParticleHandler implements IMessageHandler<MessageCannonParticle, IMessage> {

	@Override//IMessageHandler
	public IMessage onMessage(MessageCannonParticle message, MessageContext ctx) {
		WorldClient world=Minecraft.getMinecraft().world;
		EnumParticleTypes particle=EnumParticleTypes.getParticleFromId(message.type);
		for(int i=0;i<message.num;i++){
			double noise=0,movNoise=0;
			switch(message.noiseType){
			case 1:
				noise=message.noise*world.rand.nextGaussian();
				break;
			case 2:
				noise=message.noise*(world.rand.nextDouble()-0.5);
				break;
			}
			switch(message.movNoiseType){
			case 1:
				movNoise=message.movNoise*world.rand.nextGaussian();
				break;
			case 2:
				movNoise=message.movNoise*(world.rand.nextDouble()-0.5);
				break;
			}
			world.spawnParticle(particle,true,message.x+noise,message.y+noise,message.z+noise,message.mx+movNoise,message.my+movNoise,message.mz+movNoise,message.params);
		}
		/*
		switch(message.type){
		case 0:
			//System.out.println("received!");
			
			world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE,true, message.x,message.y,message.z, 0,0,0,new int[0]);
			for(int i=0;i<message.num;i++){
				world.spawnParticle(EnumParticleTypes.FLAME,true,
						message.x+message.calibre*(world.rand.nextGaussian())/10d,
						message.y+message.calibre*(world.rand.nextGaussian())/10d,
						message.z+message.calibre*(world.rand.nextGaussian())/10d,
						message.mx+message.calibre*(world.rand.nextGaussian())/50d,
						message.my+message.calibre*(world.rand.nextGaussian())/50d,
						message.mz+message.calibre*(world.rand.nextGaussian())/50d,new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,true,
						message.x+message.calibre*(world.rand.nextGaussian())/10d,
						message.y+message.calibre*(world.rand.nextGaussian())/10d,
						message.z+message.calibre*(world.rand.nextGaussian())/10d,
						message.mx/2+message.calibre*(world.rand.nextGaussian())/50d,
						message.my/2+message.calibre*(world.rand.nextGaussian())/50d,
						message.mz/2+message.calibre*(world.rand.nextGaussian())/50d,new int[0]);
			}
			//ctx.getClientHandler().handleParticles(new SPacketParticles(EnumParticleTypes.EXPLOSION_LARGE,true, (float) message.x,(float)message.y,(float)message.z, 0,0,0,0,2, new int[0]));
			break;
		case 1:
			//System.out.println("received!");
			world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE,true, message.x,message.y,message.z, 0,0,0,new int[0]);
			for(int i=0;i<message.firenum;i++){
				world.spawnParticle(EnumParticleTypes.FLAME,true,
						message.x+message.calibre*(world.rand.nextGaussian())/10d,
						message.y+message.calibre*(world.rand.nextGaussian())/10d,
						message.z+message.calibre*(world.rand.nextGaussian())/10d,
						message.mx+message.calibre*(world.rand.nextGaussian())/50d,
						message.my+message.calibre*(world.rand.nextGaussian())/50d,
						message.mz+message.calibre*(world.rand.nextGaussian())/50d,new int[0]);
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,true,
						message.x+message.calibre*(world.rand.nextGaussian())/10d,
						message.y+message.calibre*(world.rand.nextGaussian())/10d,
						message.z+message.calibre*(world.rand.nextGaussian())/10d,
						message.mx/2+message.calibre*(world.rand.nextGaussian())/50d,
						message.my/2+message.calibre*(world.rand.nextGaussian())/50d,
						message.mz/2+message.calibre*(world.rand.nextGaussian())/50d,new int[0]);
			}
			//ctx.getClientHandler().handleParticles(new SPacketParticles(EnumParticleTypes.EXPLOSION_HUGE,true, (float) message.x,(float)message.y,(float)message.z, 0,0,0,0,2, new int[0]));
			break;
		}
		*/
		
		return null;
		
	}
}
