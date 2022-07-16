package cannonmod.core;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import cannonmod.entity.EntityCannon;
import cannonmod.entity.EntityTracer;
import cannonmod.entity.RenderCannon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EventCaller {
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent.Pre e){
		if(e.getType()==RenderGameOverlayEvent.ElementType.CROSSHAIRS){
			
			Entity entity=Minecraft.getMinecraft().player.getRidingEntity();
			if(Minecraft.getMinecraft().gameSettings.thirdPersonView==0&&
					Minecraft.getMinecraft().player.getRidingEntity()!=null &&
					entity instanceof EntityCannon){
				int f1=Math.min(Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().displayWidth);
				Minecraft.getMinecraft().getTextureManager().bindTexture(RenderCannon.textureCrossbar);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(false);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableAlpha();
				//GL11.glDisable(GL11.GL_ALPHA_TEST);
				Tessellator tessellator = Tessellator.getInstance();
				 BufferBuilder worldrenderer = tessellator.getBuffer();
				int widthDiff=(Minecraft.getMinecraft().displayWidth-f1)/2;
				int heightDiff=(Minecraft.getMinecraft().displayHeight-f1)/2;
				f1/=2;
				//System.out.println("widthdiff:"+widthDiff+" heightDiff:"+heightDiff+" f1:"+f1);
				EntityPlayer player=Minecraft.getMinecraft().player;
				EntityCannon cannon=(EntityCannon)entity;
				
				float yaw=player.rotationYawHead-cannon.rotationYawHead;
				float pitch=player.rotationPitch-cannon.rotationPitch;
				
				widthDiff/=2;
				heightDiff/=2;
				worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
				worldrenderer.pos(widthDiff-yaw*3, heightDiff+(double)f1-pitch*3, 0).tex( 0.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				worldrenderer.pos(widthDiff+(double)f1-yaw*3, heightDiff+(double)f1-pitch*3, 0).tex(1.0D, 1.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				worldrenderer.pos(widthDiff+(double)f1-yaw*3, heightDiff-pitch*3, 0).tex( 1.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				worldrenderer.pos(widthDiff-yaw*3, heightDiff-pitch*3, 0).tex( 0.0D, 0.0D).normal(0.0F, 1.0F, 0.0F).endVertex();
				tessellator.draw();
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				//GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(CannonCore.buttonCannonGui.isPressed()){
			Entity e=Minecraft.getMinecraft().player.getRidingEntity();
			if(e instanceof EntityCannon){
				((EntityCannon)e).openGUI();
			}
		}
		/*
		if(CannonCore.buttonCannonFire.isPressed()){
			Entity e=Minecraft.getMinecraft().thePlayer.ridingEntity;
			if(e instanceof EntityCannon){
				((EntityCannon)e).openFire();
			}
		}
		*/
	}
	
	@SubscribeEvent
	public void onPlayerRightClick(RightClickEmpty event){
		if(event.getEntityPlayer().getRidingEntity() instanceof EntityCannon){
			/**a120*/
			((EntityCannon)event.getEntityPlayer().getRidingEntity()).sendFireCommand();
		}
	}
	/**a120*/
	@SubscribeEvent
	public void onEntityDamaged(LivingHurtEvent event){
		if(!event.getSource().isUnblockable() &&
				event.getEntityLiving().getRidingEntity() instanceof EntityCannon
				&&((EntityCannon)event.getEntityLiving().getRidingEntity()).hasTurret()){
			EntityCannon cannon=(EntityCannon)event.getEntityLiving().getRidingEntity();
			event.setAmount((float) (event.getAmount()*(1-cannon.damageReduction)));
		}
		/**a130*/
		if(event.getSource() instanceof EntityDamageSource){
			
			EntityDamageSource src=(EntityDamageSource) event.getSource();
			Entity attacker=src.getTrueSource();
			//System.out.println("damaged by "+attacker.getClass().getSimpleName()+" tracer:"+CannonCore.tracersList.size());
			for(EntityTracer tracer:CannonCore.tracersList){
				if(attacker==tracer.traceTarget){
					Field f=ReflectionHelper.findField(EntityDamageSource.class, 
							ObfuscationReflectionHelper.remapFieldNames(EntityDamageSource.class.getName(),"damageSourceEntity","field_76386_o"));
					f.setAccessible(true);
					try {
						f.set(src,tracer.entityShooter);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}else{

			DamageSource src=(DamageSource) event.getSource();
			Entity attacker=src.getTrueSource();
			//System.out.println("damaged by "+attacker.getClass().getSimpleName()+" tracer:"+CannonCore.tracersList.size());
			for(EntityTracer tracer:CannonCore.tracersList){
				if(attacker==tracer.traceTarget){
					Field f=ReflectionHelper.findField(DamageSource.class, 
							ObfuscationReflectionHelper.remapFieldNames(DamageSource.class.getName(),"damageSourceEntity","field_76386_o"));
					f.setAccessible(true);
					try {
						f.set(src,tracer.entityShooter);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
