package cannonmod.entity;

import java.util.ArrayList;
import java.util.List;

import cannonmod.core.CannonCore;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
/**a130*/
public class EntityTracer extends Entity {
	public ForgeChunkManager.Ticket ticket;
	public List<ChunkPos> forcedChunkList=new ArrayList();
	public Entity entityShooter;
	public Entity traceTarget;
	public int lowSpeedTimer;
	
	public boolean emitSmoke;
	public static final DataParameter<Boolean> smokePar=EntityDataManager.<Boolean>createKey(EntityTracer.class,DataSerializers.BOOLEAN);
	
	public EntityTracer(World worldIn) {
		super(worldIn);
	}
	public EntityTracer(World worldIn,Entity target,Entity shooter) {
		super(worldIn);
		this.entityShooter=shooter;
		this.traceTarget=target;
		this.setPositionAndUpdate(target.posX, target.posY, target.posZ);
	}
	@Override
	public void entityInit(){
		this.getDataManager().register(smokePar, true);
	}
	
	public void setSmoke(boolean value){
		if(this.emitSmoke!=value){
			this.emitSmoke=value;
			this.getDataManager().set(smokePar, value);
		}
	}
	public boolean getSmoke(){
		return this.getDataManager().get(smokePar);
	}
	@Override
	public void onUpdate(){
		super.onUpdate();
		if(!this.world.isRemote && (this.traceTarget==null || this.traceTarget.isDead)){
			this.setDead();
			return;
		}
		if(this.traceTarget!=null){
			this.setPosition(this.traceTarget.posX, this.traceTarget.posY, this.traceTarget.posZ);
			double xdif=this.posX-this.lastTickPosX;
			double ydif=this.posY-this.lastTickPosY;
			double zdif=this.posZ-this.lastTickPosZ;
			//System.out.println("speed"+(xdif*xdif+ydif*ydif+zdif*zdif));
			if(xdif*xdif+ydif*ydif+zdif*zdif<0.4){
				this.lowSpeedTimer++;
				this.setSmoke(false);
			}else{
				this.lowSpeedTimer=0;
				this.setSmoke(true);
				
				this.loadChunksAround();
			}
		}
		if(this.world.isRemote && this.getSmoke()){
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX+this.rand.nextGaussian()
					,this.posY,this.posZ+this.rand.nextGaussian(),0,0.1,0,new int[0]);
		}
		if(this.lowSpeedTimer>200){
			this.setDead();
			return;
		}
	}
	
	@Override
	public void setDead(){
		super.setDead();
		this.releaseForcedChunks();
		CannonCore.tracersList.remove(this);
	}
	
	public void loadChunksAround(){

		this.makeTicket();

	}

	public void makeTicket(){
		if(this.ticket==null){
			ForgeChunkManager.Ticket chunkTicket=ForgeChunkManager.requestTicket(CannonCore.instance, this.world, ForgeChunkManager.Type.ENTITY);
			if (chunkTicket != null)
			{
				chunkTicket.getModData();
				chunkTicket.setChunkListDepth(12);
				chunkTicket.bindEntity(this);
				this.setTicket(chunkTicket);
				//System.out.println("not null!");
			}else{
				//System.out.println("null!");
			}

		}
		forceChunkLoading((MathHelper.floor(this.posX))>>4, (MathHelper.floor(this.posZ))>>4);
	}

	public void setTicket(ForgeChunkManager.Ticket ticket){
		this.ticket=ticket;
	}
	public void forceChunkLoading(int xChunk, int zChunk) {
		if (this.ticket == null) {
			return;
		}
		List<ChunkPos> coordsLoad=new ArrayList();
		int xmod=this.motionX<0?0:1;
		int zmod=this.motionZ<0?0:1;
		for(int x=xChunk-1+xmod;x<=xChunk+1+xmod;x++){
			for(int z=zChunk-1+zmod;z<=zChunk+1+zmod;z++){
				coordsLoad.add(new ChunkPos(x, z));

			}
		}
		for(int i=0;i<coordsLoad.size();i++){
			ChunkPos chunk=coordsLoad.get(i);
			if(this.forcedChunkList.contains(chunk)){
				this.forcedChunkList.remove(chunk);
			}else{
				//System.out.println("forced a chunk "+chunk.chunkXPos*16+"~"+(chunk.chunkXPos*16+16)+","+chunk.chunkZPos*16+"~"+(chunk.chunkZPos*16+16));
				ForgeChunkManager.forceChunk(this.ticket, chunk);
			}
		}
		for(int i=0;i<this.forcedChunkList.size();i++){
			ForgeChunkManager.unforceChunk(ticket, this.forcedChunkList.get(i));
		}
		this.forcedChunkList=coordsLoad;
	}

	public void releaseForcedChunks(){
		for(int i=0;i<this.forcedChunkList.size();i++){
			ForgeChunkManager.unforceChunk(ticket, this.forcedChunkList.get(i));
		}
		this.forcedChunkList.clear();
	}


	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

}
