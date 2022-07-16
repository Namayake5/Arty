package cannonmod.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cannonmod.core.CannonCore;
import cannonmod.core.MessageCannonGui;
import cannonmod.entity.EntityCannon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCannon extends GuiContainer {

	public ResourceLocation gui;
	public EntityCannon cannon;
	public ButtonInfo buttonInfo;
	public ButtonDrop buttonDrop;

	public GuiCannon (InventoryPlayer inventoryPlayer,
			EntityCannon tileEntity) {
		//the container is instanciated and passed to the superclass for handling
		super(new ContainerCannon(inventoryPlayer, tileEntity));
		this.gui=new ResourceLocation("simplecannon:textures/gui/cannon.png");
		this.xSize=227;
		this.ySize=192;
		this.cannon=tileEntity;
	}
	
	@Override
	public void initGui()
    {
		super.initGui();
        this.buttonList.clear();
        this.buttonInfo=new ButtonInfo(this.buttonList.size(),148,8,33,20,"Info1",this);
        this.buttonDrop=new ButtonDrop(this.buttonList.size()+1,148,33,33,20,"Drop",this);
        this.buttonList.add(this.buttonInfo);
        this.buttonList.add(this.buttonDrop);
    }

	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		/**a130*/
		//draws "Inventory" or your regional equivalent
		/*
		fontRendererObj.drawString(I18n.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
		fontRendererObj.drawString("HP "+cannon.getHealth(),120,5, 4210752);
		fontRendererObj.drawString("Calibre "+cannon.calibre*10+"cm",120,15, 4210752);
		fontRendererObj.drawString("Barrel "+cannon.lengthOfBarrel*10+"cm",120,25, 4210752);
		fontRendererObj.drawString("Armor "+(int)(cannon.armor*10/this.cannon.size)+"mm",120,35, 4210752);
		if(cannon.engine>0){
			fontRendererObj.drawString("Engine mk."+cannon.engine,120,45, 4210752);
		}
		if(cannon.motor>0){
			fontRendererObj.drawString("Motor mk."+cannon.motor,120,55, 4210752);
		}
		
		if(cannon.loader>0){
			fontRendererObj.drawString("AutoLoader mk."+cannon.loader,120,65, 4210752);
		}
		*/
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2,
			int par3) {
		//draw your Gui here, only thing you need to change is the path
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(this.gui);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		int fuelleft=this.cannon.getBurnTimeRemainingScaled(13);
		this.drawTexturedModalRect(x+153,y+66,232,12-fuelleft,14,1+fuelleft);
		
		int reloadPro=this.cannon.getReloadTimeProceedingScaled(24);
		this.drawTexturedModalRect(x+45+reloadPro,y+10,231+reloadPro,31,25-reloadPro,19);
		
	}
	/**a130*/
	public static class ButtonInfo extends GuiButton{
		public GuiCannon guiCannon;
		public int xBase,yBase;
		public int mode=0;
		public ButtonInfo(int buttonId, int x, int y, int widthIn,
				int heightIn, String buttonText,GuiCannon guiCannon) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
			this.guiCannon=guiCannon;
			this.xBase=x;
			this.yBase=y;
		}
		
		@Override
		public boolean mousePressed(Minecraft mc,int mouseX,int mouseY){
			if (super.mousePressed(mc, mouseX, mouseY)){
				if(this.mode==0){
					this.mode=1;
					this.displayString="Info2";
				}else if(this.mode==1){
					this.mode=0;
					this.displayString="Info1";
				}
				return true;
			}
			return false;
        }
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY,float partial){
			int x = (this.guiCannon.width - this.guiCannon.xSize) / 2;
			int y = (this.guiCannon.height - this.guiCannon.ySize) / 2;
			this.x=this.xBase+x;
			this.y=this.yBase+y;
			super.drawButton(mc, mouseX, mouseY, partial);
			if(this.hovered){
				EntityCannon cannon=this.guiCannon.cannon;
				List<String> list=new ArrayList();
				if(this.mode==0){
					list.add("HP "+cannon.getHealth());
					list.add("Calibre "+cannon.calibre*10+"cm");
					list.add("Barrel "+cannon.lengthOfBarrel*10+"cm");
					list.add("Armor "+(int)(cannon.armor*10/cannon.size)+"mm");
					if(cannon.engine>0){
						list.add("Engine mk."+cannon.engine);
					}
					if(cannon.motor>0){
						list.add("Motor mk."+cannon.motor);
					}
					if(cannon.loader>0){
						list.add("AutoLoader mk."+cannon.loader);
					}
					
				}else if(this.mode==1){
					list.add("MaxSpeed "+String.format("%1$.2f m/sec", cannon.speedMultiplier*20));
					list.add("TurretTurningSpeed "+String.format("%1$.2f degree/sec", cannon.turretTurning*20));
					if(cannon.hasTurret()){
						list.add("DamageReduction "+String.format("%1$.1f ", cannon.damageReduction*100)+"%");
					}else{
						list.add("DamageReduction 0%");
					}
					
					list.add("FuelLeft "+(int)(cannon.fuelLeft/20)+"secs");
				}
				this.guiCannon.drawHoveringText(list,mouseX,mouseY);
			}
		}
		
	}
	
	public static class ButtonDrop extends GuiButton{
		public GuiCannon guiCannon;
		public int xBase,yBase;
		public ButtonDrop(int buttonId, int x, int y, int widthIn,
				int heightIn, String buttonText,GuiCannon guiCannon) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
			this.guiCannon=guiCannon;
			this.xBase=x;
			this.yBase=y;
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY,float partial){
			int x = (this.guiCannon.width - this.guiCannon.xSize) / 2;
			int y = (this.guiCannon.height - this.guiCannon.ySize) / 2;
			this.x=this.xBase+x;
			this.y=this.yBase+y;
			super.drawButton(mc, mouseX, mouseY, partial);
			if(this.hovered){
				List<String> list=new ArrayList();
				list.add("Drop this arty as an item");
				this.guiCannon.drawHoveringText(list,mouseX,mouseY);
			}
		}
		@Override
		public boolean mousePressed(Minecraft mc,int mouseX,int mouseY){
			if (super.mousePressed(mc, mouseX, mouseY)){
				CannonCore.INSTANCE.sendToServer(new MessageCannonGui(this.guiCannon.cannon.getEntityId(),(byte)2));
				this.guiCannon.onGuiClosed();
				return true;
			}
			return false;
        }
		
	}

}