package cannonmod.core;

import java.util.List;

import cannonmod.entity.EntityTracer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class ShellChunkManager implements LoadingCallback {

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
		for (ForgeChunkManager.Ticket ticket : tickets){
			if (!ticket.isPlayerTicket())
			{
				Entity entity = ticket.getEntity();
				if(entity instanceof EntityTracer){
					EntityTracer shell=(EntityTracer)entity;
					shell.setTicket(ticket);
					//shell.forceChunkLoading(shell.chunkCoordX, shell.chunkCoordZ);
				}
			}
		}
	}
	/*
	@SubscribeEvent
	public void entityEnteredChunk(EntityEvent.EnteringChunk event) {
		Entity entity = event.entity;
		if ((entity instanceof EntityShell))
			if (!entity.worldObj.isRemote)
			{
				(( EntityShell)entity).forceChunkLoading(event.newChunkX, event.newChunkZ);
			}
	}
	*/
	/*
	@SubscribeEvent
	public void unloadChunk(ChunkEvent.Unload event) {
		for(List<Entity> list:event.getChunk().entityLists){
			for(Entity entity:list){
				if ((entity instanceof EntityShell)){
					if (!entity.worldObj.isRemote)
					{
						(( EntityShell)entity).loadChunksAround();
					}
				}
			}
		}
	}
	
*/
}
