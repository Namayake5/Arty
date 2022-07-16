package cannonmod.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import cannonmod.core.CannonCore;
import cannonmod.core.MessageCannonGui;
import cannonmod.core.MessageCannonParticle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCannon extends EntityLivingBase implements IInventory{

	/**a120*/
	public boolean doShootNextTick;

	public ItemStack[] inventory;

	public double speedMultiplier=2.0;
	public float turningSpeed=5;
	public double playerDistanceFromCentre=1.0;
	public int lengthOfBarrel=20;
	public double rotationOffsetX=0;
	public double rotationOffsetZ=0;
	public double turretTurning=4;
	public String customName;
	public int fuelLeft=0;
	public int currentItemBurnTime=200;
	public boolean reflectedDamage;
	public int ammoSlot=9;
	/**a130*/
	public int gunpowderSlot=17;
	/**a130*/
	public double vehicleLength=2.875;
	public double vehicleWidth=0.5;
	public double damageReduction=0.1;
	public static final int symbolNum=4;
	public EntityPainting.EnumArt[] arts=new EntityPainting.EnumArt[symbolNum];
	public BannerData[] bannerDatas=new BannerData[symbolNum];

	public int calibre=20;
	public double bulletSpeed=0.4;
	public double precision=0.1;
	public int reload=20;
	public int engine=0;
	public int motor=0;
	public int armor=1;
	public int reloadtime=0;
	public double size=1;
	public int design=0;
	/**a110*/
	public int loader=0;

	public ModelCannon model;

	public static final DataParameter<Integer> lengthPar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> calibrePar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> motorPar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> enginePar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> armorPar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> reloadTimePar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> designPar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	/**a110*/
	public static final DataParameter<Integer> loaderPar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	/**a130*/
	public static final DataParameter<Integer> fuelPar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<Integer> burnTimePar=EntityDataManager.<Integer>createKey(EntityCannon.class,DataSerializers.VARINT);
	public static final DataParameter<ItemStack>[] symbolPar=new DataParameter[symbolNum];
	public static final DataParameter<String>[] artPar=new DataParameter[symbolNum];
	//public static final DataParameter<Optional<ItemStack>> symbol2Par=EntityDataManager.<Optional<ItemStack>>createKey(EntityCannon.class,DataSerializers.OPTIONAL_ITEM_STACK);
	/**a130*/
	private Field FthrowerThrowable;
	private Field FtntPlacedBy;
	
	static{
		for(int i=0;i<symbolNum;i++){
			symbolPar[i]=EntityDataManager.<ItemStack>createKey(EntityCannon.class,DataSerializers.ITEM_STACK);
			artPar[i]=EntityDataManager.<String>createKey(EntityCannon.class,DataSerializers.STRING);
		}
	}
	/**a130*/
	public static class BannerData{
		public int baseColor;
		/** A list of all the banner patterns. */
		public NBTTagList patterns;
		public boolean patternDataSet;
		public List<BannerPattern> patternList;
		public List<EnumDyeColor> colorList;
		/** This is a String representation of this banners pattern and color lists, used for texture caching. */
		public String patternResourceLocation;
	}

	public EntityCannon(World par1World) {
		super(par1World);
		this.stepHeight=1.1f;
		//this.setTurnLimitPerTick(1f);
		this.setSize(1.5f, 2f);
		/**a130*/
		this.inventory=new ItemStack[31];
		/**a130*/
		FthrowerThrowable=ReflectionHelper.findField(EntityThrowable.class, 
				ObfuscationReflectionHelper.remapFieldNames(EntityThrowable.class.getName(),"thrower","field_70192_c"));
		FthrowerThrowable.setAccessible(true);
		FtntPlacedBy=ReflectionHelper.findField(EntityTNTPrimed.class, 
				ObfuscationReflectionHelper.remapFieldNames(EntityTNTPrimed.class.getName(),"tntPlacedBy","field_94084_b"));
		FtntPlacedBy.setAccessible(true);
		
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(CannonCore.cannonHealth);

	}

	@Override
	public void entityInit(){
		super.entityInit();
		this.getDataManager().register(calibrePar, this.calibre);
		this.getDataManager().register(lengthPar,this.lengthOfBarrel);
		this.getDataManager().register(motorPar,this.motor);
		this.getDataManager().register(enginePar,this.engine);
		this.getDataManager().register(armorPar,this.armor);
		this.getDataManager().register(reloadTimePar,this.reloadtime);
		this.getDataManager().register(designPar,this.design);
		/**a110*/
		this.getDataManager().register(loaderPar,this.loader);
		/**a130*/
		this.getDataManager().register(fuelPar,this.fuelLeft);
		this.getDataManager().register(burnTimePar, this.currentItemBurnTime);
		for(int i=0;i<symbolNum;i++){
			this.getDataManager().register(symbolPar[i],(ItemStack)null);
			this.getDataManager().register(artPar[i],"");
		}
	}

	public void updateDataWatcher(){
		this.getDataManager().set(calibrePar, this.calibre);
		this.getDataManager().set(lengthPar,this.lengthOfBarrel);
		this.getDataManager().set(motorPar,this.motor);
		this.getDataManager().set(enginePar,this.engine);
		this.getDataManager().set(armorPar,this.armor);
		this.getDataManager().set(designPar,this.design);
		/**a110*/
		this.getDataManager().set(loaderPar,this.loader);
		/**a130*/
		this.getDataManager().set(fuelPar,this.fuelLeft);
		this.getDataManager().set(burnTimePar, this.currentItemBurnTime);
		for(int i=0;i<symbolNum;i++){
			this.getDataManager().set(symbolPar[i],this.inventory[this.getSymbolSlotNum(i)]);
			if(this.arts[i]!=null){
				this.getDataManager().set(artPar[i],this.arts[i].title);
			}
		}
	}

	public void setStats(){
		if(this.calibre*this.lengthOfBarrel==0)return;
		/**a130*/
		this.speedMultiplier=Math.min(CannonCore.cannonSpeed*(2d+engine*4d)/(this.calibre*this.lengthOfBarrel+300d+30d*this.armor)*0.05,0.7);
		this.turningSpeed=400f*(2f+engine*2f)/(this.calibre*this.lengthOfBarrel+300f+30f*this.armor);
		/**a110*/
		this.reload=(int)(this.calibre*this.calibre/2*(1-this.loader*0.15));
		this.bulletSpeed=CannonCore.bulletSpeed*Math.sqrt(this.lengthOfBarrel)*1.2;
		this.precision=1d/(Math.pow(Math.E, ((double)this.lengthOfBarrel-(double)this.calibre)/50d)+1d)*15d;
		this.size=(2*(double)this.calibre+(double)this.lengthOfBarrel/2+1)/40d;
		this.setSize((float)this.calibre/5+0.5f, ((float)this.calibre/5+(float)this.lengthOfBarrel/20+2)/4+(float)this.calibre/10);
		this.turretTurning=300d*(2d+this.motor*2d)/(this.calibre*this.lengthOfBarrel+200d);
		/**a130*/
		this.vehicleLength=this.calibre/5d+this.lengthOfBarrel/20d+1;
		this.vehicleWidth=this.calibre/10d+1;
		double red=MathHelper.sqrt(this.armor/this.size*0.02);
		this.damageReduction=(red<0.9?red:0.9);
		//System.out.println("turretSpeed:"+this.turretTurning);
	}

	@Override
	public void onEntityUpdate(){
		super.onEntityUpdate();
		this.reflectedDamage=false;
		if(this.world.isRemote){
			this.reloadtime=this.getDataManager().get(reloadTimePar);
		}
		/**a110*/
		if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D)
		{
			List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.2, 0.2, 0.2), EntitySelectors.<Entity>getTeamCollisionPredicate(this));

			if (!list.isEmpty())
			{
				for (int j1 = 0; j1 < list.size(); ++j1)
				{
					Entity entity1 = (Entity)list.get(j1);

					if ((entity1 instanceof EntityLiving) && !this.isBeingRidden() && !entity1.isRiding())
					{
						entity1.startRiding(this);
					}
					else
					{
						entity1.applyEntityCollision(this);
					}
				}
			}
		}
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this,this.getEntityBoundingBox().expand(0.5, 0.5, 0.5));
		if (!list.isEmpty())
		{
			for (int j1 = 0; j1 < list.size(); ++j1)
			{
				Entity entity1 = (Entity)list.get(j1);
				if(entity1 instanceof EntityItem && !entity1.isDead){
					EntityItem entityitem=(EntityItem)entity1;

					if(!entityitem.cannotPickup()){

						if(entityitem.getItem().getItem() == Items.GUNPOWDER){
							if(this.getStackInSlot(this.ammoSlot+this.gunpowderSlot-1)==null){
								this.setInventorySlotContents(this.ammoSlot+this.gunpowderSlot-1, entityitem.getItem());
								this.onItemPickup(entityitem, entityitem.getItem().getCount());
								entityitem.setDead();
							}
						}else{
							if(this.getStackInSlot(this.ammoSlot-1)==null){
								this.setInventorySlotContents(this.ammoSlot-1, entityitem.getItem());
								this.onItemPickup(entityitem, entityitem.getItem().getCount());
								entityitem.setDead();
							}
						}
					}
				}
			}
		}
		/**a110*/
		boolean isReloadable=false;
		if(!this.getPassengers().isEmpty()){
			Entity riddenByEntity=this.getPassengers().get(0);
			if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase)
			{
				isReloadable=true;
				this.getDataManager().set(reloadTimePar,this.reloadtime);
				EntityLivingBase entitylivingbase = (EntityLivingBase)riddenByEntity;
				float f = this.rotationYaw;
				float rotationRd=f * (float)Math.PI / 180.0F;
				
				if(this.onGround){
					if(this.engine==0 || this.fuelLeft>0){
						/**a130*/
						if(entitylivingbase.moveForward>0.001){
							this.motionX += -MathHelper.sin(rotationRd) * this.speedMultiplier;
							this.motionZ += MathHelper.cos(rotationRd) * this.speedMultiplier;
							this.moveForward=(float) this.speedMultiplier;
						}else if(entitylivingbase.moveForward<-0.001){
							this.motionX += MathHelper.sin(rotationRd) * this.speedMultiplier*0.5;
							this.motionZ += -MathHelper.cos(rotationRd) * this.speedMultiplier*0.5;
							this.moveForward=-0.5f*(float) this.speedMultiplier;
						}else{
							this.moveForward=0;
						}
						if(entitylivingbase.moveStrafing>0.001){
							this.rotationYaw-=this.turningSpeed;
							/**a130*/
							this.rotationYawHead-=this.turningSpeed;
							this.moveStrafing=-this.turningSpeed;
						}else if(entitylivingbase.moveStrafing<-0.001){
							this.rotationYaw+=this.turningSpeed;
							/**a130*/
							this.rotationYawHead+=this.turningSpeed;
							this.moveStrafing=this.turningSpeed;
						}
						if(!(entitylivingbase instanceof EntityPlayer)){
							if(this.rotationYaw-entitylivingbase.rotationYaw>180){
								this.rotationYaw-=360;
							}else if(this.rotationYaw-entitylivingbase.rotationYaw<-180){
								this.rotationYaw+=360;
							}
							if(this.rotationYaw-entitylivingbase.rotationYaw>this.turningSpeed){
								this.rotationYaw-=this.turningSpeed;
							}else if(this.rotationYaw-entitylivingbase.rotationYaw<-this.turningSpeed){
								this.rotationYaw+=this.turningSpeed;
							}else{
								this.rotationYaw=entitylivingbase.rotationYaw;
							}
						}
					}
					/**a130*/
					//System.out.println("forward:"+entitylivingbase.moveForward+" strafing:"+entitylivingbase.moveStrafing);
					if(MathHelper.abs(entitylivingbase.moveForward)>0.001 || MathHelper.abs(entitylivingbase.moveStrafing)>0.001){
						if(this.engine==0 || this.fuelLeft>0){
							if(this.world.isRemote){
								this.createRunningParticles2();
							}else if(!(entitylivingbase instanceof EntityPlayer)){
								this.createRunningParticles2();
							}
						}
						if(this.engine>0 ){
							this.fuelLeft--;
							if(this.fuelLeft>0){
								if(MathHelper.abs(entitylivingbase.moveForward)<=0.001){
									/**a130*/
									float speed=0.5f+(float)this.speedMultiplier*3;
									if(this.ticksExisted%(int)(5/speed)==0){
										//System.out.println("speed:"+this.speedMultiplier*entitylivingbase.moveStrafing);
										this.playSound(CannonCore.cannonRun,1 , speed);
									}
									/**a130*/
									if(this.world.isRemote){
										for(int i=0;i<this.engine;i++){
											this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX+this.vehicleLength*MathHelper.sin(rotationRd)/2+(this.rand.nextGaussian())
													,this.posY+this.height/2,this.posZ-this.vehicleLength*MathHelper.cos(rotationRd)/2+(this.rand.nextGaussian()),
													0,0.1,0,new int[0]);

										}
									}else if(!(entitylivingbase instanceof EntityPlayer)){
										CannonCore.INSTANCE.sendToAll(new MessageCannonParticle
												(this.posX+this.vehicleLength*MathHelper.sin(rotationRd)/2,this.posY+this.height/2,this.posZ-this.vehicleLength*MathHelper.cos(rotationRd)/2,
														0d,0.1,0d,this.engine,1d,0d,(byte)1,(byte)0,EnumParticleTypes.SMOKE_LARGE.getParticleID()));
									}

								}else{
									/**a130*/
									float speed=0.5f+(float)this.speedMultiplier*3;
									if(this.ticksExisted%(int)(5/speed)==0){
										//System.out.println("speed:"+this.speedMultiplier*entitylivingbase.moveForward);
										this.playSound(CannonCore.cannonRun,1 , speed);

									}
									/**a130*/
									if(this.world.isRemote){
										for(int i=0;i<this.engine;i++){
											this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX+this.vehicleLength*MathHelper.sin(rotationRd)/2+(this.rand.nextGaussian())
													,this.posY+this.height/2,this.posZ-this.vehicleLength*MathHelper.cos(rotationRd)/2+(this.rand.nextGaussian()),
													0,0.1,0,new int[0]);

										}
									}else if(!(entitylivingbase instanceof EntityPlayer)){
										CannonCore.INSTANCE.sendToAll(new MessageCannonParticle
												(this.posX+this.vehicleLength*MathHelper.sin(rotationRd)/2,this.posY+this.height/2,this.posZ-this.vehicleLength*MathHelper.cos(rotationRd)/2,
														0d,0.1,0d,this.engine,1d,0d,(byte)1,(byte)0,EnumParticleTypes.SMOKE_LARGE.getParticleID()));
									}
								}
							}

							if(this.fuelLeft<0){
								/**a130*/
								ItemStack stack=this.getStackInSlot(this.getFuelSlotNum());
								if(stack!=null && TileEntityFurnace.isItemFuel(stack)){
									this.fuelLeft+=TileEntityFurnace.getItemBurnTime(stack)/2;
									this.currentItemBurnTime=TileEntityFurnace.getItemBurnTime(stack)/2;
									stack.setCount(stack.getCount()-1);
									if(stack.getCount()==0){
										/**a130*/
										this.setInventorySlotContents(this.getFuelSlotNum(), stack.getItem().getContainerItem(stack));
									}
								}
							}

						}
					}
				}
				if(this.rotationYawHead-entitylivingbase.rotationYawHead>180){
					this.rotationYawHead-=360;
				}else if(this.rotationYawHead-entitylivingbase.rotationYawHead<-180){
					this.rotationYawHead+=360;
				}
				//System.out.println("yawhead="+entitylivingbase.rotationYawHead+" pitch="+entitylivingbase.rotationPitch);
				if(this.rotationYawHead-entitylivingbase.rotationYawHead>this.turretTurning){
					this.rotationYawHead-=this.turretTurning;
				}else if(this.rotationYawHead-entitylivingbase.rotationYawHead<-this.turretTurning){
					this.rotationYawHead+=this.turretTurning;
				}else{
					this.rotationYawHead=entitylivingbase.rotationYawHead;
				}

				if(this.rotationPitch-entitylivingbase.rotationPitch>180){
					this.rotationPitch-=360;
				}else if(this.rotationPitch-entitylivingbase.rotationPitch<-180){
					this.rotationPitch+=360;
				}

				if(this.rotationPitch-entitylivingbase.rotationPitch>this.turretTurning){
					this.rotationPitch-=this.turretTurning;
				}else if(this.rotationPitch-entitylivingbase.rotationPitch<-this.turretTurning){
					/**a120*/
					if(this.rotationPitch<10){
						this.rotationPitch+=this.turretTurning;
					}

				}else{
					this.rotationPitch=entitylivingbase.rotationPitch;
				}

				/**a110*/
				if(entitylivingbase instanceof EntityLiving && ((EntityLiving)entitylivingbase).getAttackTarget()!=null && this.isReloaded()){
					this.openFire();
				}else if(this.doShootNextTick){
					this.openFire();
					this.doShootNextTick=false;
				}

				//System.out.println("yawhead="+this.rotationYawHead+" pitch="+this.rotationPitch);


			}

		}
		/**a110*/
		if(this.reload>this.reloadtime){
			if((this.loader>0) || isReloadable){
				this.reloadtime++;
				float speed=0.7f-((float)this.calibre/100);
				if(this.reload==this.reloadtime+(int)(6/speed)){
					this.playSound(SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, 1.0f, speed);
				}
			}
		}

		/*
		if(this.dataWatcher.getWatchableObjectByte(14)==1){
			if(this.worldObj.isRemote){
				double xVel=-MathHelper.sin(this.rotationYaw*(float)Math.PI/180f)*0.8;
				double yVel=MathHelper.sin((float) (this.rotationPitch/180.0f*Math.PI))*0.8;
				double zVel=MathHelper.cos(this.rotationYaw*(float)Math.PI/180f)*0.8;
				for(int i=0;i<30;i++){
					this.worldObj.spawnParticle("largesmoke", this.posX,this.posY+0.5,this.posZ,
							xVel+this.rand.nextGaussian()*0.3-0.15,
							yVel+this.rand.nextGaussian()*0.3-0.15,
							zVel+this.rand.nextGaussian()*0.3-0.15);
				}
				for(int i=0;i<60;i++){
					this.worldObj.spawnParticle("flame", this.posX,this.posY+0.5,this.posZ,
							xVel+this.rand.nextGaussian()*0.3-0.15,
							yVel+this.rand.nextGaussian()*0.3-0.15,
							zVel+this.rand.nextGaussian()*0.3-0.15);
				}

			}else{
				this.dataWatcher.updateObject(14, (byte)0);
			}
		}
		 */
		this.updateInventory();
	}
/**a130*/
	public void createRunningParticles2()
	{
		int i = MathHelper.floor(this.posX);
		int j = MathHelper.floor(this.posY - 0.2);
		int k = MathHelper.floor(this.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		float f = this.rotationYaw;
		float rotationRd=f * (float)Math.PI / 180.0F;
		double xBase1=this.posX+this.vehicleLength*MathHelper.sin(rotationRd)/2-this.vehicleWidth*MathHelper.cos(rotationRd)/2;
		double zBase1=this.posZ-this.vehicleLength*MathHelper.cos(rotationRd)/2+this.vehicleWidth*MathHelper.sin(rotationRd)/2;
		double xBase2=this.posX+this.vehicleLength*MathHelper.sin(rotationRd)/2+this.vehicleWidth*MathHelper.cos(rotationRd)/2;
		double zBase2=this.posZ-this.vehicleLength*MathHelper.cos(rotationRd)/2-this.vehicleWidth*MathHelper.sin(rotationRd)/2;
		if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE)
		{
			
			if(this.world.isRemote){
				for(int i1=0;i1<this.size*2+1;i1++){
					this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,xBase1+this.vehicleLength*(this.rand.nextDouble()-0.5)/4
							,this.posY+0.1,zBase1+this.vehicleLength*(this.rand.nextDouble()-0.5)/4,-this.motionX,0.1,-this.motionZ, new int[] {Block.getStateId(iblockstate)});
					this.world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,xBase2+this.vehicleLength*(this.rand.nextDouble()-0.5)/4
							,this.posY+0.1,zBase2+this.vehicleLength*(this.rand.nextDouble()-0.5)/4,-this.motionX,0.1,-this.motionZ, new int[] {Block.getStateId(iblockstate)});
				}
			}else{
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(xBase1,this.posY+0.1,zBase1,-this.motionX,0.1,-this.motionZ,(int)(this.size*2+1),
						this.vehicleLength/4,0,(byte)2,(byte)0,EnumParticleTypes.BLOCK_CRACK.getParticleID(), new int[] {Block.getStateId(iblockstate)}));
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(xBase2,this.posY+0.1,zBase2,-this.motionX,0.1,-this.motionZ,(int)(this.size*2+1),
						this.vehicleLength/4,0,(byte)2,(byte)0,EnumParticleTypes.BLOCK_CRACK.getParticleID(), new int[] {Block.getStateId(iblockstate)}));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int p_145955_1_)
	{
		if (this.currentItemBurnTime == 0)
		{
			this.currentItemBurnTime = 200;
		}

		return this.fuelLeft * p_145955_1_ / this.currentItemBurnTime;
	}

	@SideOnly(Side.CLIENT)
	public int getReloadTimeProceedingScaled(int p_145955_1_)
	{
		if (this.reload == 0)
		{
			this.reload = 10;
		}

		return this.reloadtime * p_145955_1_ / this.reload;
	}

	public boolean isReloaded(){
		return this.reload==this.reloadtime;
	}

	@Override
	public void updatePassenger(Entity passenger){
		
		/**a120*/
		/**a130*/
		if(this.hasTurret()){
			passenger.setPosition(this.posX
					+this.rotationOffsetX*MathHelper.sin(this.rotationYaw*(float)Math.PI/180),
					this.posY+this.getMountedYOffset(),
					this.posZ
					-this.rotationOffsetZ*MathHelper.cos(this.rotationYaw*(float)Math.PI/180));
		}else{
			passenger.setPosition(this.posX+this.playerDistanceFromCentre*MathHelper.sin(this.rotationYawHead*(float)Math.PI/180)
					+this.rotationOffsetX*MathHelper.sin(this.rotationYaw*(float)Math.PI/180),
					this.posY+this.getMountedYOffset(),
					this.posZ-this.playerDistanceFromCentre*MathHelper.cos(this.rotationYawHead*(float)Math.PI/180)
					-this.rotationOffsetZ*MathHelper.cos(this.rotationYaw*(float)Math.PI/180));
		}

	}
	/**a130*/
	public boolean hasTurret(){
		return ((this.lengthOfBarrel/(this.calibre+30))<=2);
	}

	@Override
	public double getMountedYOffset(){
		/**a110*/
		/**a130*/
		return this.calibre*3/20+this.vehicleLength/4-0.9;
	}

	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_){
		boolean flag=super.attackEntityFrom(p_70097_1_, p_70097_2_);
		if(this.reflectedDamage){
			flag=false;
		}
		return flag;
	}

	@Override
	protected void damageEntity(DamageSource dmgSource, float dmgValue)
	{
		if(!dmgSource.isFireDamage()){ 
			Entity source= dmgSource.getTrueSource();

			if(source!=null){

				double width=source.width<0.2?0.2:source.width;
				double height=source.height<0.2?0.2:source.height;
				double volume=Math.sqrt(width*width*height);

				if (source instanceof EntityLivingBase){

					//ItemStack tool=((EntityLivingBase)source).getHeldItemMainhand();
					//if(tool!=null && tool.getItem().getHarvestLevel(tool, "pickaxe")>=1){
						super.damageEntity(dmgSource, dmgValue);
						return;
					//}
				}
				/**a120*/
				double prob=0.4f*MathHelper.sqrt(dmgValue) * volume*this.size/this.armor/2*(1+Math.sqrt(source.motionX*source.motionX+source.motionY*source.motionY+source.motionZ*source.motionZ));
				//System.out.println("prob:"+prob+" armor:"+this.armor+" size:"+this.size);
				if(this.world.rand.nextDouble()<prob){
					super.damageEntity(dmgSource, dmgValue);
					return;

				}else{
					/**a120*/
					this.newKnockBack(source, (float)(prob/this.size), source.posX-this.posX, source.posZ-this.posZ);
					this.reflectDamager(source, prob);
					return;
				}

			}else{
				/**a120*/
				double prob=0.4f*MathHelper.sqrt(dmgValue) * this.size/this.armor/2;
				//System.out.println("prob:"+prob+" armor:"+this.armor+" size:"+this.size);
				if(this.world.rand.nextDouble()<prob){
					super.damageEntity(dmgSource, dmgValue);
					return;

				}else{
					this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.5f+(float)prob, 0.5f+(float)prob);
					this.reflectedDamage=true;
					return;
				}
			}
		}else{
			super.damageEntity(dmgSource, dmgValue);
		}
	}

	public void newKnockBack(Entity source,float strenght,double xRatio,double zRatio){
		this.isAirBorne = true;
		float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
		this.motionX /= 2.0D;
		this.motionZ /= 2.0D;
		this.motionX -= xRatio / (double)f * (double)strenght;
		this.motionZ -= zRatio / (double)f * (double)strenght;

		if (this.onGround)
		{
			this.motionY /= 2.0D;
			this.motionY += (double)strenght;

			if (this.motionY > 0.4000000059604645D)
			{
				this.motionY = 0.4000000059604645D;
			}
		}
	}

	public void reflectDamager(Entity source,double probability){
		source.motionX=-source.motionX/2;
		source.motionY=-source.motionY/2;
		source.motionZ=-source.motionZ/2;
		this.playSound(SoundEvents.BLOCK_ANVIL_LAND, 0.5f+(float)probability, 0.5f+(float)probability);
		this.reflectedDamage=true;
		return;
	}

	protected void dropFewItems(boolean par1, int par2)
	{
		EntityTNTPrimed tnt=new EntityTNTPrimed(this.world,this.posX,this.posY,this.posZ,this.getLastAttackedEntity());
		tnt.setFuse(0);
		this.world.spawnEntity(tnt);
		tnt.onUpdate();

		List<ItemStack> dropList=new ArrayList();

		dropList.add(new ItemStack(CannonCore.itemBarrel,1,this.calibre+(this.lengthOfBarrel<<8)));
		dropList.add(new ItemStack(CannonCore.itemCarriage,1,this.motor));
		dropList.add(new ItemStack(CannonCore.itemTrack,1));
		dropList.add(new ItemStack(CannonCore.itemTrack,1));
		if(this.engine>0){
			dropList.add(new ItemStack(CannonCore.itemEngine,1,this.engine-1));
		}
		for(int i=0;i<this.armor-1;i++){
			dropList.add(new ItemStack(CannonCore.itemArmor,1));
		}
		if(this.loader>0){
			dropList.add(new ItemStack(CannonCore.itemLoader,1,this.loader-1));
		}
		for(ItemStack stack:dropList){
			if(this.world.rand.nextDouble()<0.7){
				this.entityDropItem(stack, 1);
			}
		}

		for(int i=0;i<this.getSizeInventory();i++){
			if(this.getStackInSlot(i)!=null){
				this.entityDropItem(this.getStackInSlot(i), 1);
			}
		}

	}

	public void updateInventory(){
		for(int i=0;i<this.ammoSlot-1;i++){
			if(this.inventory[i]==null && this.inventory[i+1]!=null){
				this.setInventorySlotContents(i, this.inventory[i+1]);
				this.setSlotNull(i+1);
			}
		}
		for(int i=this.ammoSlot;i<this.ammoSlot+this.gunpowderSlot-1;i++){
			if(this.inventory[i]==null && this.inventory[i+1]!=null){
				this.setInventorySlotContents(i, this.inventory[i+1]);
				this.setSlotNull(i+1);
			}
		}
	}

	/**
	 * First layer of player interaction
	 */
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand)
	{
		/**a130*/
		if(stack!=null && stack.getItem()==Items.NAME_TAG){
			stack.splitStack(1);
			if(stack.getCount()==0){
				player.setHeldItem(hand,null);
			}
			this.setCustomNameTag(stack.getDisplayName());
			return EnumActionResult.SUCCESS;
		}
		if (!this.getPassengers().isEmpty())
		{
			return EnumActionResult.PASS;
		}
		else
		{
			if (!this.world.isRemote)
			{
				player.startRiding(this);
			}

			return EnumActionResult.SUCCESS;
		}
	}

	public void setSlotNull(int num) {
		if(num>this.getSizeInventory())return;
		this.inventory[num]=null;
	}

	@Override
	public int getSizeInventory() {
		return this.inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int num) {
		if(num>this.getSizeInventory())return null;
		return this.inventory[num];
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		ItemStack stackInSlot=this.getStackInSlot(p_70298_1_);
		if (stackInSlot != null)
		{
			ItemStack itemstack;

			if (stackInSlot.getCount() <= p_70298_2_)
			{

				this.setSlotNull(p_70298_1_);
				return stackInSlot;
			}
			else
			{
				itemstack = stackInSlot.splitStack(p_70298_2_);

				if (stackInSlot.getCount() == 0)
				{
					this.setSlotNull(p_70298_1_);
				}

				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int num, ItemStack stack) {
		if(num>this.getSizeInventory())return;
		this.inventory[num]=stack;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer p_70300_1_) {
		return this.isDead ? false : p_70300_1_.getDistanceSq(this) <= 64.0D;
	}

	@Override
	public boolean isItemValidForSlot(int num, ItemStack stack) {
		if(num>this.getSizeInventory())return false;
		if(num<this.ammoSlot){
			return true;
		}else if(num<this.ammoSlot+this.gunpowderSlot){
			return stack.getItem()==Items.GUNPOWDER;
		}else{
			return TileEntityFurnace.isItemFuel(stack);
		}
	}
	
	/**a130*/
	public ItemStack getThisItemStack(){
		NBTTagCompound nbt=new NBTTagCompound();
		nbt.setInteger("Calibre", this.calibre);
		nbt.setInteger("Barrel",this.lengthOfBarrel);
		nbt.setInteger("Engine",this.engine);
		nbt.setInteger("Motor",this.motor);
		nbt.setInteger("Armor", this.armor-1);
		nbt.setInteger("Design",this.design);
		nbt.setInteger("Loader",this.loader);
		nbt.setInteger("HPLeft",MathHelper.ceil(this.getHealth()));
		ItemStack stack=new ItemStack(CannonCore.itemArty);
		stack.setTagCompound(nbt);
		if(this.hasCustomName()){
			stack.setStackDisplayName(this.getCustomNameTag());
		}
		return stack;
	}
	
	/**a130*/
	public void dropArtyAndInventoryItems(){
		
		for(int i=0;i<this.getSizeInventory();i++){
			if(this.getStackInSlot(i)!=null){
				this.entityDropItem(this.getStackInSlot(i),1);
			}
		}
		this.entityDropItem(this.getThisItemStack(),1);
	}

	public void openGUI(){

		if(this.getPassengers().get(0) instanceof EntityPlayer){

			//((EntityPlayer)this.riddenByEntity).openGui(CannonCore.instance, GuiHandler.MOD_TILE_ENTITY_GUI, this.worldObj, this.getEntityId(), 0, 0);
			CannonCore.INSTANCE.sendToServer(new MessageCannonGui(this.getEntityId(),(byte)0));
		}
	}
	/**a120*/
	public void sendFireCommand(){
		if(this.world.isRemote){
			CannonCore.INSTANCE.sendToServer(new MessageCannonGui(this.getEntityId(),(byte)1));
			return;
		}else{
			this.doShootNextTick=true;
		}
	}
	/**a130*/
	public void sendFireSuccessCommand(double x,double y,double z,double motionx,double motiony,double motionz,int firenum,byte type){
		if(!this.world.isRemote){
			switch(type){
			case 0:
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(x,y,z,EnumParticleTypes.EXPLOSION_LARGE.getParticleID()));
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(x,y,z,motionx,motiony,motionz,firenum,this.calibre/10d,this.calibre/50d,
						(byte)1,(byte)1,EnumParticleTypes.FLAME.getParticleID()));
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(x,y,z,motionx/2,motiony/2,motionz/2,firenum,this.calibre/10d,this.calibre/50d,
						(byte)1,(byte)1,EnumParticleTypes.SMOKE_NORMAL.getParticleID()));
				break;
			case 1:
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(x,y,z,EnumParticleTypes.EXPLOSION_HUGE.getParticleID()));
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(x,y,z,motionx,motiony,motionz,firenum,this.calibre/10d,this.calibre/50d,
						(byte)1,(byte)1,EnumParticleTypes.FLAME.getParticleID()));
				CannonCore.INSTANCE.sendToAll(new MessageCannonParticle(x,y,z,motionx/2,motiony/2,motionz/2,firenum,this.calibre/10d,this.calibre/50d,
						(byte)1,(byte)1,EnumParticleTypes.SMOKE_LARGE.getParticleID()));
				break;
			}
			return;
		}
	}
	/**a120*/
	public void openFire(){

		if(!this.isReloaded()){
			return;
		}
		Entity riddenByEntity=this.getPassengers().get(0);
		if(this.getStackInSlot(this.ammoSlot)==null){
			Entity entity=riddenByEntity;
			if(entity instanceof EntityPlayer){
				((EntityPlayer)entity).sendMessage(new TextComponentString("No Gunpowder!"));
			}
			return;
		}
		if(this.getStackInSlot(0)==null){
			Entity entity=riddenByEntity;
			if(entity instanceof EntityPlayer){
				((EntityPlayer)entity).sendMessage(new TextComponentString("No Ammo!"));
			}
			return;
		}

		if(this.rotationPitch<0)this.rotationPitch+=360;
		if(this.rotationYawHead<0)this.rotationYawHead+=360;
		/**a110*/
		double l=(this.lengthOfBarrel<=20?2:this.lengthOfBarrel/10)*MathHelper.cos(this.rotationPitch*(float)Math.PI/180);

		double x= this.posX-l*MathHelper.sin(this.rotationYawHead*(float)Math.PI/180)
				+this.rotationOffsetX*MathHelper.sin(this.rotationYaw*(float)Math.PI/180);
		double y= this.posY-this.lengthOfBarrel*MathHelper.sin(this.rotationPitch*(float)Math.PI/180)/5+this.getMountedYOffset();
		double z= this.posZ+l*MathHelper.cos(this.rotationYawHead*(float)Math.PI/180)
				-this.rotationOffsetZ*MathHelper.cos(this.rotationYaw*(float)Math.PI/180);

		int meta=0;
		EnumFacing face;
		if(this.rotationPitch>45 && this.rotationPitch<135){
			face=EnumFacing.DOWN;
		}else if(this.rotationPitch>225 && this.rotationPitch<315){
			face=EnumFacing.UP;
		}else{
			if(this.rotationYawHead>45 && this.rotationYawHead<135){
				face=EnumFacing.WEST;
			}else if(this.rotationYawHead>=135 && this.rotationYawHead<225){
				face=EnumFacing.NORTH;
			}else if(this.rotationYawHead>=225 && this.rotationYawHead<315){
				face=EnumFacing.EAST;
			}else{
				face=EnumFacing.SOUTH;
			}
		}
		int fx=MathHelper.floor(x),fy=MathHelper.floor(y),fz=MathHelper.floor(z);
		int bx=fx-face.getFrontOffsetX();
		int by=fy-face.getFrontOffsetY();
		int bz=fz-face.getFrontOffsetZ();

		if(!(this.world.getBlockState(new BlockPos(bx, by, bz)).getBlock() instanceof BlockAir)){
			Entity entity=riddenByEntity;
			if(entity instanceof EntityPlayer){
				((EntityPlayer)entity).sendMessage(new TextComponentString("A block is blocking fire!"));
			}
			return;
		}

		//Remember chunks and compare later
		/*
		int cxm=(bx-8)>>4;
		int cym=(by-8)>>4;
		int czm=(bz-8)>>4;

		ExtendedBlockStorage[] chunks=new ExtendedBlockStorage[8];

		chunks[0]=this.worldObj.getChunkFromChunkCoords(cxm, czm).getBlockStorageArray()[cym];
		chunks[1]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm).getBlockStorageArray()[cym];
		chunks[2]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm+1).getBlockStorageArray()[cym];
		chunks[3]=this.worldObj.getChunkFromChunkCoords(cxm, czm+1).getBlockStorageArray()[cym];
		chunks[4]=this.worldObj.getChunkFromChunkCoords(cxm, czm).getBlockStorageArray()[cym+1];
		chunks[5]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm).getBlockStorageArray()[cym+1];
		chunks[6]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm+1).getBlockStorageArray()[cym+1];
		chunks[7]=this.worldObj.getChunkFromChunkCoords(cxm, czm+1).getBlockStorageArray()[cym+1];

		for(int i=0;i<4;i++){
			ExtendedBlockStorage chunk=new ExtendedBlockStorage(cym<<4,false);
			chunk.setBlockLSBArray(chunks[i].getBlockLSBArray());
			chunk.setBlockMSBArray(chunks[i].getBlockMSBArray());
		}
		for(int i=4;i<8;i++){
			ExtendedBlockStorage chunk=new ExtendedBlockStorage((cym+1)<<4,false);
			chunk.setBlockLSBArray(chunks[i].getBlockLSBArray());
			chunk.setBlockMSBArray(chunks[i].getBlockMSBArray());
		}
		 */
		//dispense
		BlockPos posf=new BlockPos(fx, fy, fz);
		BlockPos posb=new BlockPos(bx, by, bz);

		Block block=this.world.getBlockState(posf).getBlock();

		IBlockState state=Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, face);
		this.world.setBlockState(posb,state);
		TileEntity t=this.world.getTileEntity(posb);
		if(t instanceof TileEntityDispenser){
			TileEntityDispenser te=(TileEntityDispenser) t;
			ItemStack stackAmmo=this.getStackInSlot(0);
			te.setInventorySlotContents(0, stackAmmo);
			List entityListCopy=new ArrayList(this.world.loadedEntityList);

			//int entityNum=this.worldObj.loadedEntityList.size();
			List<Entity> firedEntityList=new ArrayList();

			for(int i=0;i<((this.calibre/5)*(this.calibre/5)*2);i++){
				/**Make a list of what are fired*/

				Blocks.DISPENSER.updateTick(this.world,posb,state, this.world.rand);


				/*
				ExtendedBlockStorage[] chunksL=new ExtendedBlockStorage[8];

				chunksL[0]=this.worldObj.getChunkFromChunkCoords(cxm, czm).getBlockStorageArray()[cym];
				chunksL[1]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm).getBlockStorageArray()[cym];
				chunksL[2]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm+1).getBlockStorageArray()[cym];
				chunksL[3]=this.worldObj.getChunkFromChunkCoords(cxm, czm+1).getBlockStorageArray()[cym];
				chunksL[4]=this.worldObj.getChunkFromChunkCoords(cxm, czm).getBlockStorageArray()[cym+1];
				chunksL[5]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm).getBlockStorageArray()[cym+1];
				chunksL[6]=this.worldObj.getChunkFromChunkCoords(cxm+1, czm+1).getBlockStorageArray()[cym+1];
				chunksL[7]=this.worldObj.getChunkFromChunkCoords(cxm, czm+1).getBlockStorageArray()[cym+1];

				for(int j=0;j<8;j++){
					for(int cx=0;cx<16;cx++){
						for(int cy=0;cy<16;cy++){
							for(int cz=0;cz<16;cz++){
								if(chunks[j].getBlockByExtId(cx, cy, cz) != chunksL[j].getBlockByExtId(cx, cy, cz)){
									firedEntityList.add(new EntityFallingBlock(cxm+))
								}
							}
						}
					}
				}
				 */
			}

			if((stackAmmo!=null && stackAmmo.getCount()<=0)||te.getStackInSlot(0)==null){
				this.setInventorySlotContents(0, null);
			}
			te.setInventorySlotContents(0, null);
			this.world.setBlockToAir(posb);


			Block block1=this.world.getBlockState(posf).getBlock();
			if(block != block1){

				EntityFallingBlockEx entity=new EntityFallingBlockEx(this.world,fx+0.5, fy+0.5, fz+0.5,state);
				NBTTagCompound nbt=new NBTTagCompound();
				TileEntity tileentity=this.world.getTileEntity(posf);
				if(tileentity!=null){
					tileentity.writeToNBT(nbt);
				}
				entity.tileEntityData=nbt;
				entity.shouldDropItem=false;
				this.world.spawnEntity(entity);
				((WorldServer)this.world).getEntityTracker().sendToTracking
				(entity,new SPacketSpawnObject(entity, 70, Block.getIdFromBlock(entity.func_145805_f())));
				this.world.setBlockToAir(posf);
				//System.out.println(block1.getLocalizedName());
			}

			for(int i=0;i<this.world.loadedEntityList.size();i++){
				Entity e=(Entity) this.world.loadedEntityList.get(i);
				if(!((i<entityListCopy.size() && e==entityListCopy.get(i)) || entityListCopy.contains(e))){
					if(e instanceof EntityItem ){
						EntityItem entityitem=(EntityItem)e;
						if(entityitem.getItem().getItem() instanceof ItemBlock){
							ItemStack stack=entityitem.getItem();
							ItemBlock item=(ItemBlock)stack.getItem();
							if(item.getBlock().canPlaceBlockAt(this.world, posf) &&
									this.world.setBlockState(posf, item.getBlock().getDefaultState())){


								EntityFallingBlockEx entity=new EntityFallingBlockEx(this.world,fx+0.5, fy+0.5, fz+0.5,this.world.getBlockState(posf));
								NBTTagCompound nbt=new NBTTagCompound();
								TileEntity tileentity=this.world.getTileEntity(posf);
								if(tileentity!=null){
									tileentity.writeToNBT(nbt);
								}
								entity.tileEntityData=nbt;
								entity.shouldDropItem=false;
								this.world.spawnEntity(entity);
								((WorldServer)this.world).getEntityTracker().sendToTracking
								(entity,new SPacketSpawnObject(entity, 70, Block.getIdFromBlock(entity.func_145805_f())));
								this.world.setBlockToAir(posf);
								this.world.loadedEntityList.set(i, entity);
								e.setDead();
								((WorldServer)this.world).getEntityTracker().sendToTracking
								(e,new SPacketDestroyEntities(new int[]{e.getEntityId()}));
								//System.out.println(block1.getLocalizedName());
								firedEntityList.add(entity);

							}
						}
					}else{
						firedEntityList.add(e);
					}
				}
			}



			if(firedEntityList.size()==0){
				Entity entity=riddenByEntity;
				if(entity instanceof EntityPlayer){
					((EntityPlayer)entity).sendMessage(new TextComponentString("Something is blocking fire!"));
				}
				return;
			}
			this.reloadtime=0;
			this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE,1.0f,1.0f-((float)this.calibre/80));
			WorldServer worldserver=(WorldServer)this.world;
			/**a130*/


			/**Calculate their volume and decide speed*/
			double volumeSum=0;
			sum:{
				for(Entity e:firedEntityList){
					double width=e.width<0.2 ? 0.2:e.width;
					double height=e.height<0.2 ? 0.2:e.height;
					if(width>(double)this.calibre/10 || height>(double)this.calibre/10){
						volumeSum=64;
						break sum;
					}
					volumeSum+=width*width*height;
				}
				if(volumeSum>32)volumeSum=32;
				if(volumeSum<0.25)volumeSum=0.25;
			}
			volumeSum=Math.sqrt(volumeSum);
			double speed=this.bulletSpeed*MathHelper.sqrt(this.getStackInSlot(this.ammoSlot).getCount())/MathHelper.sqrt(volumeSum);

			//System.out.println("volume:"+volumeSum+" speed:"+speed);
			/**a130*/
			Random r=this.world.rand;
			float yaw=(float) (this.rotationYawHead+(MathHelper.clamp(r.nextGaussian(), -1, 1))*this.precision);
			float pitch=(float) (this.rotationPitch+(MathHelper.clamp(r.nextGaussian(), -1, 1))*this.precision);
			double ml=speed*MathHelper.cos(pitch*(float)Math.PI/180);
			double mx= -ml*MathHelper.sin(yaw*(float)Math.PI/180);
			double my= -speed*MathHelper.sin(pitch*(float)Math.PI/180);
			double mz= ml*MathHelper.cos(yaw*(float)Math.PI/180);
			if(this.calibre<=15){
				this.sendFireSuccessCommand(x,y,z,mx,my,mz,this.getStackInSlot(this.ammoSlot).getCount(),(byte)0);
			}else{
				this.sendFireSuccessCommand(x,y,z,mx,my,mz,this.getStackInSlot(this.ammoSlot).getCount(),(byte)1);
			}

			for(int i=0;i<firedEntityList.size();i++){
				Entity entity=(Entity)firedEntityList.get(i);
				/**a130*/
				Entity shooter=this;
				if(this.getPassengers().size()!=0){
					shooter=this.getPassengers().get(0);
				}
				if(entity instanceof EntityArrow){
					EntityArrow arrow=(EntityArrow) entity;
					/**a130*/
					arrow.setDamage(Math.min(arrow.getDamage()*speed*1.5,10.0));
					arrow.shootingEntity=shooter;
				}
				/**a130*/
				if(entity instanceof EntityFireball){
					if(shooter instanceof EntityLivingBase){
						((EntityFireball)entity).shootingEntity=(EntityLivingBase) shooter;
					}
				}
				if(entity instanceof EntityThrowable){
					if(shooter instanceof EntityLivingBase){
						try {
							FthrowerThrowable.set(entity, shooter);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				if(entity instanceof EntityTNTPrimed){
					if(shooter instanceof EntityLivingBase){
						try {
							FtntPlacedBy.set(entity, shooter);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				/**a130*/
				entity.setPositionAndRotation(x+MathHelper.clamp(r.nextGaussian(), -1, 1)*(double)this.calibre/20, y+MathHelper.clamp(r.nextGaussian(), -1, 1)*(double)this.calibre/20,
						z+MathHelper.clamp(r.nextGaussian(), -1, 1)*(double)this.calibre/20, this.rotationYawHead, this.rotationPitch);

				entity.motionX=mx;
				entity.motionY=my;
				entity.motionZ=mz;

				entity.rotationPitch=this.rotationPitch;
				entity.rotationYaw=this.rotationYawHead;

				((WorldServer)this.world).getEntityTracker().sendToTracking
				(entity, new SPacketEntityTeleport(entity));
				((WorldServer)this.world).getEntityTracker().sendToTracking
				(entity, new SPacketEntityVelocity(entity.getEntityId(), entity.motionX, entity.motionY, entity.motionZ));

				//System.out.println(entity.toString());
				/**a130*/
				
				EntityTracer tracer=new EntityTracer(this.world,entity,shooter);
				this.world.spawnEntity(tracer);
				CannonCore.tracersList.add(tracer);

			}
			/**a130*/
			this.setSlotNull(this.ammoSlot);

		}

	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		{
			NBTTagList tagList = tagCompound.getTagList("AmmoList", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
				byte slot = tag.getByte("Slot");
				if (slot >= 0 && slot < this.ammoSlot) {
					this.inventory[slot] = new ItemStack(tag);
				}
			}
		}
		{
			NBTTagList tagList = tagCompound.getTagList("GunPowderList", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
				byte slot = tag.getByte("Slot");
				if (slot >= this.ammoSlot && slot < this.ammoSlot+this.gunpowderSlot) {
					this.inventory[slot] = new ItemStack(tag);
				}
			}
		}
		/**a120*/
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
		/**a130*/
		this.inventory[this.getFuelSlotNum()]=new ItemStack(tagCompound.getCompoundTag("Fuel"));
		for(int i=0;i<symbolNum;i++){
			this.inventory[this.getSymbolSlotNum(i)]=new ItemStack(tagCompound.getCompoundTag("Symbol"+i));
			
			if(tagCompound.hasKey("Art"+i)){
				String s=tagCompound.getString("Art"+i);
				for (EntityPainting.EnumArt entitypainting$enumart : EntityPainting.EnumArt.values())
		        {
		            if (entitypainting$enumart.title.equals(s))
		            {
		                this.arts[i] = entitypainting$enumart;
		            }
		        }

		        if (this.arts[i] == null)
		        {
		            this.arts[i] = EntityPainting.EnumArt.KEBAB;
		        }
				
			}
		}
		

		this.calibre=tagCompound.getInteger("Calibre");
		this.lengthOfBarrel=tagCompound.getInteger("Barrel");
		this.engine=tagCompound.getInteger("Engine");
		this.motor=tagCompound.getInteger("Motor");
		this.armor=tagCompound.getInteger("Armor");
		this.fuelLeft=tagCompound.getInteger("FuelLeft");
		this.design=tagCompound.getInteger("Design");
		this.rotationYawHead=tagCompound.getFloat("YawHead");
		/**a110*/
		this.loader=tagCompound.getInteger("Loader");
		/**a130*/
		this.currentItemBurnTime=tagCompound.getInteger("BurnTime");
		this.updateDataWatcher();
		this.setStats();
		
		
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		{
			NBTTagList itemList = new NBTTagList();
			for (int i = 0; i < this.ammoSlot; i++) {
				ItemStack stack = this.inventory[i];
				if (stack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stack.writeToNBT(tag);
					itemList.appendTag(tag);
				}
			}
			tagCompound.setTag("AmmoList", itemList);
		}
		{
			NBTTagList itemList = new NBTTagList();
			for (int i = this.ammoSlot; i < this.ammoSlot+this.gunpowderSlot; i++) {
				ItemStack stack = this.inventory[i];
				if (stack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					stack.writeToNBT(tag);
					itemList.appendTag(tag);
				}
			}
			tagCompound.setTag("GunPowderList", itemList);
		}
		{
			/**a130*/
			if(this.inventory[this.getFuelSlotNum()]!=null){
				NBTTagCompound nbt=new NBTTagCompound();
				this.inventory[this.getFuelSlotNum()].writeToNBT(nbt);
				tagCompound.setTag("Fuel", nbt);
			}

		}
		/**a130*/
		for(int i=0;i<symbolNum;i++){
			if(this.inventory[this.getSymbolSlotNum(i)]!=null){
				NBTTagCompound nbt=new NBTTagCompound();
				this.inventory[this.getSymbolSlotNum(i)].writeToNBT(nbt);
				tagCompound.setTag("Symbol"+i, nbt);
				
				if(this.inventory[this.getSymbolSlotNum(i)].getItem() == Items.PAINTING
						&&this.arts[i]!=null){
					tagCompound.setString("Art"+i, this.arts[i].title);
				}
			}
			
		}
		
		tagCompound.setInteger("Calibre",this.calibre);
		tagCompound.setInteger("Barrel",this.lengthOfBarrel);
		tagCompound.setInteger("Engine",this.engine);
		tagCompound.setInteger("Motor",this.motor);
		tagCompound.setInteger("Armor",this.armor);
		tagCompound.setInteger("FuelLeft", this.fuelLeft);
		tagCompound.setInteger("Design",this.design);
		tagCompound.setFloat("YawHead", this.rotationYawHead);
		/**a110*/
		tagCompound.setInteger("Loader", this.loader);
		/**a130*/
		tagCompound.setInteger("BurnTime",this.currentItemBurnTime);
		
		
		return tagCompound;
	}

	public ModelCannon getThisModel(RenderCannon renderer){
		if(this.model==null){
			this.calibre=this.getDataManager().get(calibrePar);
			this.lengthOfBarrel=this.getDataManager().get(lengthPar);
			this.motor=this.getDataManager().get(motorPar);
			this.engine=this.getDataManager().get(enginePar);
			this.armor=this.getDataManager().get(armorPar);
			this.design=this.getDataManager().get(designPar);
			/**a110*/
			this.loader=this.getDataManager().get(loaderPar);
			/**a130*/
			this.fuelLeft=this.getDataManager().get(fuelPar);
			this.currentItemBurnTime=this.getDataManager().get(burnTimePar);
			for(int i=0;i<symbolNum;i++){
				this.inventory[this.getSymbolSlotNum(i)]=(ItemStack)this.getDataManager().get(symbolPar[i]);

				String s=this.getDataManager().get(artPar[i]);

				for (EntityPainting.EnumArt entitypainting$enumart : EntityPainting.EnumArt.values())
				{
					if (entitypainting$enumart.title.equals(s))
					{
						this.arts[i] = entitypainting$enumart;
					}
				}

				if (this.arts[i] == null)
				{
					this.arts[i] = EntityPainting.EnumArt.KEBAB;
				}


			}
			this.setStats();
			/**a130*/
			this.model=new ModelCannon((int)(this.lengthOfBarrel*16/10),(int)(this.calibre*16/10),0,this.design,this.hasTurret(),renderer);
		}
		return this.model;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (this.inventory[index] != null)
		{
			ItemStack itemstack = this.inventory[index];
			this.inventory[index] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}
	

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for(int i=0;i<this.inventory.length;i++){
			this.inventory[i]=null;
		}
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return new ArrayList<ItemStack>();
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return null;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {

	}

	@Override
	public EnumHandSide getPrimaryHand() {
		/**a130*/
		return EnumHandSide.RIGHT;
	}
	/**a130*/
	public int getFuelSlotNum(){
		return this.inventory.length-symbolNum-1;
	}
	/**a130*/
	public int getSymbolSlotNum(int number){
		return this.inventory.length-symbolNum+number;
	}
	
	/**a130*/
	public ItemStack getDisplayedItem(){
		return this.inventory[this.getSymbolSlotNum(0)];
	}
	
	public void setArt(int num,EntityPainting.EnumArt art){
		if(num<this.arts.length){
			this.arts[num]=art;
			this.getDataManager().set(artPar[num], art.title);
		}
	}

	@SideOnly(Side.CLIENT)
	public EntityPainting.EnumArt getArt(int num){
		String s=this.getDataManager().get(artPar[num]);

		for (EntityPainting.EnumArt entitypainting$enumart : EntityPainting.EnumArt.values())
		{
			if (entitypainting$enumart.title.equals(s))
			{
				return entitypainting$enumart;
			}
		}

		return EntityPainting.EnumArt.KEBAB;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	/*
	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName()?this.customName:"container.cannon";

	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName!=null;
	}
	 */

}
