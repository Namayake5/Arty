package cannonmod.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCannon extends Item {
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> list,ITooltipFlag flagIn) {
		NBTTagCompound nbt=itemStack.getTagCompound();
		int calibre=0;
		int barrel=0;
		int loader=0;
		if(nbt!=null){
			calibre=nbt.getInteger("Calibre");
			barrel=nbt.getInteger("Barrel");
			loader=nbt.getInteger("Loader");
			//list.add("Calibre "+calibre*10+"cm");
			//list.add("Length "+barrel*10+"cm");
		}
		if(barrel==0){
			list.add("Barrel : crafted length of the barrel.");
		}else if(barrel==0xff){
			list.add("Barrel : Any");
		}else{
			list.add("Barrel "+barrel*10+"cm");
		}
		if(calibre==0){
			list.add("Calibre : crafted calibre of barrel.");
		}else if(calibre==0xff){
			list.add("Calibre : Any");
		}else{
			list.add("Calibre "+calibre*10+"cm");
		}
		/**a110*/
		/**a120*/
		if(loader==0){
			list.add("AutoLoader : crafted level of loader.");
		}else if(loader==0xff){
			list.add("AutoLoader : Any");
		}else if(loader!=-1){
			list.add("AutoLoader mk."+loader);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> par3List) {
			ItemStack stack=new ItemStack(this);
		NBTTagCompound nbt=new NBTTagCompound();
		nbt.setInteger("Calibre", 5);
		nbt.setInteger("Barrel", 10);
		/**a120*/
		nbt.setInteger("Loader",-1);
		stack.setTagCompound(nbt);
		par3List.add(stack);
	}
}
