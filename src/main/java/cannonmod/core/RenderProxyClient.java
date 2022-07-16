package cannonmod.core;

import cannonmod.entity.EntityCannon;
import cannonmod.entity.EntityFallingBlockEx;
import cannonmod.entity.EntityTracer;
import cannonmod.entity.RenderCannon;
import cannonmod.entity.RenderFallingBlockEx;
import cannonmod.entity.RenderTracer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderProxyClient extends RenderProxy{
	public void init(){
		RenderingRegistry.registerEntityRenderingHandler(EntityCannon.class, new IRenderFactory(){
			@Override
			public Render createRenderFor(RenderManager manager) {
				return new RenderCannon(manager,Minecraft.getMinecraft().getRenderItem());
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityFallingBlockEx.class, new IRenderFactory(){
			@Override
			public Render createRenderFor(RenderManager manager) {
				return new RenderFallingBlockEx(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityTracer.class, new IRenderFactory(){
			@Override
			public Render createRenderFor(RenderManager manager) {
				return new RenderTracer(manager);
			}
		});
	};
}
