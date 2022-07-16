package cannonmod.entity;


import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFallingBlockEx extends Entity
{
	public IBlockState fallTile;
    public int fallTime;
    public boolean shouldDropItem;
    private boolean dontSetBlock;
    private boolean doHurtEntity;
    private int fallDamageMax;
    private float falldamage;
    public NBTTagCompound tileEntityData;
    protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos>createKey(EntityFallingBlock.class, DataSerializers.BLOCK_POS);

    public EntityFallingBlockEx(World p_i1706_1_)
    {
        super(p_i1706_1_);
        /**a110*/
    }

    public void setOrigin(BlockPos p_184530_1_)
    {
        this.dataManager.set(ORIGIN, p_184530_1_);
    }
    
    @SideOnly(Side.CLIENT)
    public BlockPos getOrigin()
    {
        return (BlockPos)this.dataManager.get(ORIGIN);
    }

    
    
    public EntityFallingBlockEx(World p_i45319_1_, double p_i45319_2_, double p_i45319_4_, double p_i45319_6_, IBlockState p_i45319_8_)
    {
        super(p_i45319_1_);
        this.shouldDropItem = true;
        this.fallDamageMax = 40;
        this.falldamage = 2.0F;
        this.fallTile=p_i45319_8_;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
        //this. = this.height / 2.0F;
        this.setPosition(p_i45319_2_, p_i45319_4_, p_i45319_6_);
        /**a110*/
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = p_i45319_2_;
        this.prevPosY = p_i45319_4_;
        this.prevPosZ = p_i45319_6_;
        this.setOrigin(new BlockPos(this));
        
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit() {
    	this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        Block block = this.fallTile.getBlock();

        if (this.fallTile.getMaterial() == Material.AIR)
        {
            this.setDead();
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.fallTime++ == 0)
            {
                BlockPos blockpos = new BlockPos(this);

                if (this.world.getBlockState(blockpos).getBlock() == block)
                {
                    this.world.setBlockToAir(blockpos);
                }
                else if (!this.world.isRemote)
                {
                    this.setDead();
                    return;
                }
            }

            if (!this.hasNoGravity())
            {
                this.motionY -= 0.03999999910593033D;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

            if (!this.world.isRemote)
            {
                BlockPos blockpos1 = new BlockPos(this);
                boolean flag = this.fallTile.getBlock() == Blocks.CONCRETE_POWDER;
                boolean flag1 = flag && this.world.getBlockState(blockpos1).getMaterial() == Material.WATER;
                double d0 = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;

                if (flag && d0 > 1.0D)
                {
                    RayTraceResult raytraceresult = this.world.rayTraceBlocks(new Vec3d(this.prevPosX, this.prevPosY, this.prevPosZ), new Vec3d(this.posX, this.posY, this.posZ), true);

                    if (raytraceresult != null && this.world.getBlockState(raytraceresult.getBlockPos()).getMaterial() == Material.WATER)
                    {
                        blockpos1 = raytraceresult.getBlockPos();
                        flag1 = true;
                    }
                }

                if (!this.onGround && !flag1)
                {
                    if (this.fallTime > 100 && !this.world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || this.fallTime > 600)
                    {
                        if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
                        {
                            this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
                        }

                        this.setDead();
                    }
                }
                else
                {
                    IBlockState iblockstate = this.world.getBlockState(blockpos1);

                    if (this.world.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))) //Forge: Don't indent below.
                    if (!flag1 && BlockFalling.canFallThrough(this.world.getBlockState(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ))))
                    {
                        this.onGround = false;
                        return;
                    }

                    this.motionX *= 0.699999988079071D;
                    this.motionZ *= 0.699999988079071D;
                    this.motionY *= -0.5D;

                    if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION)
                    {
                        this.setDead();

                        if (!this.dontSetBlock)
                        {
                            if (this.world.mayPlace(block, blockpos1, true, EnumFacing.UP, (Entity)null) && (flag1 || !BlockFalling.canFallThrough(this.world.getBlockState(blockpos1.down()))) && this.world.setBlockState(blockpos1, this.fallTile, 3))
                            {
                                if (block instanceof BlockFalling)
                                {
                                    ((BlockFalling)block).onEndFalling(this.world, blockpos1, this.fallTile, iblockstate);
                                }

                                if (this.tileEntityData != null && block.hasTileEntity(this.fallTile))
                                {
                                    TileEntity tileentity = this.world.getTileEntity(blockpos1);

                                    if (tileentity != null)
                                    {
                                        NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());

                                        for (String s : this.tileEntityData.getKeySet())
                                        {
                                            NBTBase nbtbase = this.tileEntityData.getTag(s);

                                            if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s))
                                            {
                                                nbttagcompound.setTag(s, nbtbase.copy());
                                            }
                                        }

                                        tileentity.readFromNBT(nbttagcompound);
                                        tileentity.markDirty();
                                    }
                                }
                            }
                            else if (this.shouldDropItem && this.world.getGameRules().getBoolean("doEntityDrops"))
                            {
                                this.entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
                            }
                        }
                        else if (block instanceof BlockFalling)
                        {
                            ((BlockFalling)block).onBroken(this.world, blockpos1);
                        }
                    }
                }
            }

            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    public void fall(float distance, float damageMultiplier)
    {
        if (this.doHurtEntity)
        {
            int i = MathHelper.ceil(distance - 1.0F);
            Block block = this.fallTile.getBlock();
            if (i > 0)
            {
                ArrayList arraylist = new ArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));
                boolean flag = block == Blocks.ANVIL;
                DamageSource damagesource = flag ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext())
                {
                    Entity entity = (Entity)iterator.next();
                    entity.attackEntityFrom(damagesource, (float)Math.min(MathHelper.floor((float)i * this.falldamage), this.fallDamageMax));
                }

                if (flag && (double)this.rand.nextFloat() < 0.05000000074505806D + (double)i * 0.05D)
                {
                    int j = ((Integer)this.fallTile.getValue(BlockAnvil.DAMAGE)).intValue();
                    ++j;

                    if (j > 2)
                    {
                        this.dontSetBlock = false;
                    }
                    else
                    {
                        this.fallTile = this.fallTile.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(j));
                    }
                }
            }
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {
    	Block block = this.fallTile != null ? this.fallTile.getBlock() : Blocks.AIR;
        ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(block);
        p_70014_1_.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());
        p_70014_1_.setByte("Data", (byte)block.getMetaFromState(this.fallTile));
        p_70014_1_.setByte("Time", (byte)this.fallTime);
        p_70014_1_.setBoolean("DropItem", this.shouldDropItem);
        p_70014_1_.setBoolean("HurtEntities", this.doHurtEntity);
        p_70014_1_.setFloat("FallHurtAmount", this.falldamage);
        p_70014_1_.setInteger("FallHurtMax", this.fallDamageMax);

        if (this.tileEntityData != null)
        {
            p_70014_1_.setTag("TileEntityData", this.tileEntityData);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
    	 int i = p_70037_1_.getByte("Data") & 255;

         if (p_70037_1_.hasKey("Block", 8))
         {
             this.fallTile = Block.getBlockFromName(p_70037_1_.getString("Block")).getStateFromMeta(i);
         }
         else if (p_70037_1_.hasKey("TileID", 99))
         {
             this.fallTile = Block.getBlockById(p_70037_1_.getInteger("TileID")).getStateFromMeta(i);
         }
         else
         {
             this.fallTile = Block.getBlockById(p_70037_1_.getByte("Tile") & 255).getStateFromMeta(i);
         }
        this.fallTime = p_70037_1_.getByte("Time") & 255;
        Block block = this.fallTile.getBlock();

        if (p_70037_1_.hasKey("HurtEntities", 99))
        {
            this.doHurtEntity = p_70037_1_.getBoolean("HurtEntities");
            this.falldamage = p_70037_1_.getFloat("FallHurtAmount");
            this.fallDamageMax = p_70037_1_.getInteger("FallHurtMax");
        }
        else if (block == Blocks.ANVIL)
        {
            this.doHurtEntity = true;
        }

        if (p_70037_1_.hasKey("DropItem", 99))
        {
            this.shouldDropItem = p_70037_1_.getBoolean("DropItem");
        }

        if (p_70037_1_.hasKey("TileEntityData", 10))
        {
            this.tileEntityData = p_70037_1_.getCompoundTag("TileEntityData");
        }

        if (block.getMaterial(fallTile) == Material.AIR)
        {
            block = Blocks.SAND;
        }
    }

    public void func_145806_a(boolean p_145806_1_)
    {
        this.doHurtEntity = p_145806_1_;
    }

    public void addEntityCrashInfo(CrashReportCategory p_85029_1_)
    {
        super.addEntityCrashInfo(p_85029_1_);
        Block block = this.fallTile.getBlock();
        p_85029_1_.addCrashSection("Immitating block ID", Integer.valueOf(Block.getIdFromBlock(block)));
        p_85029_1_.addCrashSection("Immitating block data", Integer.valueOf(block.getMetaFromState(this.fallTile)));
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    @SideOnly(Side.CLIENT)
    public World func_145807_e()
    {
        return this.world;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @SideOnly(Side.CLIENT)
    public boolean canRenderOnFire()
    {
        return false;
    }

    public Block func_145805_f()
    {
        return this.fallTile.getBlock();
    }
    
    public IBlockState getBlock(){
    	return this.fallTile;
    }
}