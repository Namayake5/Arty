package cannonmod.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBarrel extends Item{
	public ItemBarrel(){
		super();
		this.setMaxDamage(1);
        this.setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> list,ITooltipFlag flagIn) {
		
		if(((itemStack.getItemDamage()&0x0000ff00)>>8)==0){
			list.add("Barrel : sum of the crafted length of barrels.");
			list.add("You must craft with ones having same size of calibre.");
		}else if(((itemStack.getItemDamage()&0x0000ff00)>>8)==0xff){
			list.add("Barrel : Any");
		}else{
			list.add("Barrel "+((itemStack.getItemDamage()&0x0000ff00)>>8)*10+"cm");
		}
		if((itemStack.getItemDamage()&0x000000ff)==0){
			list.add("Calibre : sum of the crafted calibre of barrels.");
			list.add("You must craft with ones having same length of barrel.");
		}else if((itemStack.getItemDamage()&0x000000ff)==0xff){
			list.add("Calibre : Any");
		}else{
			list.add("Calibre "+(itemStack.getItemDamage()&0x000000ff)*10+"cm");
		}
		
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> par3List) {
		par3List.add(new ItemStack(this, 1, 0x0a05));
		par3List.add(new ItemStack(this, 1, 0x0a1e));
		par3List.add(new ItemStack(this, 1, 0xa005));
	}
}
