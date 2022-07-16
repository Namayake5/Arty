package cannonmod.gui;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import cannonmod.entity.EntityCannon;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotSymbol extends Slot {
	public EntityCannon entityCannon;
	public int symbolNum;
	public SlotSymbol(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_,
			int p_i1824_4_,int symbolNum) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		this.entityCannon=(EntityCannon)p_i1824_1_;
		this.symbolNum=symbolNum;
	}
	
	/**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit()
    {
        return 1;
    }
    
    @Override
    public void putStack(@Nullable ItemStack stack){
    	if(stack!=null && stack.getItem()==Items.PAINTING && !(this.getStack()!=null && this.getStack().getItem()==Items.PAINTING)){
    		
    		List<EntityPainting.EnumArt> list = Lists.<EntityPainting.EnumArt>newArrayList();
    		for (EntityPainting.EnumArt entitypainting$enumart : EntityPainting.EnumArt.values()){
    			list.add(entitypainting$enumart);
    		}
    		EntityPainting.EnumArt art=(EntityPainting.EnumArt)list.get(this.entityCannon.world.rand.nextInt(list.size()));
    		entityCannon.setArt(this.symbolNum, art);
    		//System.out.println("Painting put "+art.title);
    	}
    	
    	super.putStack(stack);
    	
    }

}
