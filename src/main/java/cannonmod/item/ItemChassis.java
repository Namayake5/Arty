package cannonmod.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemChassis extends Item {
	public ItemChassis(){
		super();
		this.setMaxDamage(1);
        this.setHasSubtypes(true);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> list,ITooltipFlag flagIn) {
		if(((itemStack.getItemDamage()&0x0000ff00)>>8)==0xfe){
			list.add("Motor : crafted motor of the carriage.");
		}else if(((itemStack.getItemDamage()&0x0000ff00)>>8)==0xff){
			list.add("Motor : Any");
		}else if(((itemStack.getItemDamage()&0x0000ff00)>>8)>0){
			list.add("Motor Mk."+((itemStack.getItemDamage()&0x0000ff00)>>8));
		}
		if((itemStack.getItemDamage()&0x000000ff)==0xfe){
			list.add("Engine : crafted engine of the carriage.");
		}else if((itemStack.getItemDamage()&0x000000ff)==0xff){
			list.add("Engine : Any");
		}else if((itemStack.getItemDamage()&0x000000ff)>0){
			list.add("Engine Mk."+(itemStack.getItemDamage()&0x000000ff));
		}
		
	}
}
