package cannonmod.core;

import cannonmod.entity.EntityCannon;
import cannonmod.gui.ContainerCannon;
import cannonmod.gui.GuiCannon;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler{
	public static final int MOD_TILE_ENTITY_GUI = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int entityId, int y, int z) {
		//System.out.println("openGUIServer");
	    if (ID == MOD_TILE_ENTITY_GUI)
	        return new ContainerCannon(player.inventory, (EntityCannon)world.getEntityByID(entityId));

	    return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int entityId, int y, int z) {
		//System.out.println("openGUIClient");
	    if (ID == MOD_TILE_ENTITY_GUI)
	    	return new GuiCannon(player.inventory, (EntityCannon)world.getEntityByID(entityId));

	    return null;
	}
}
