package cannonmod.core;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;

public class CannonDamageSource extends DamageSource {
	public Entity damageSourceEntity;
	public Entity damager;
	public CannonDamageSource(Entity damager,Entity damageSourceEntity) {
		super("cannon");
		this.damager=damager;
		this.damageSourceEntity=damageSourceEntity;
	}
	@Override
	public Entity getImmediateSource(){
		return this.damager;
	}
	
	@Override
	public Entity getTrueSource(){
		return this.damageSourceEntity;
	}
	
	@Nullable
    public Vec3d getDamageLocation()
    {
        return new Vec3d(this.damager.posX, this.damager.posY, this.damager.posZ);
    }

}
