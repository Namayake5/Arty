package cannonmod.item.recipe;

import cannonmod.core.CannonCore;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeBarrelExpand extends IForgeRegistryEntry.Impl<IRecipe>implements IRecipe{
	public ItemStack result;
	
	public RecipeBarrelExpand() {
		result=new ItemStack(CannonCore.itemBarrel);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World p_77569_2_) {
		/*
		for(int i=0;i<3;i++){
			ItemStack stack=inv.getStackInRowAndColumn(i, 0);
			if(stack==null||stack.getItem()!=Items.iron_ingot){
				return false;
			}
		}
		for(int i=0;i<3;i++){
			ItemStack stack=inv.getStackInRowAndColumn(i, 2);
			if(stack==null||stack.getItem()!=Items.iron_ingot){
				return false;
			}
		}
		if(inv.getStackInRowAndColumn(0, 1)!=null){
			return false;
		}
		if(inv.getStackInRowAndColumn(2, 1)!=null){
			return false;
		}
		ItemStack stackBarrel=inv.getStackInRowAndColumn(1, 1);
		if(inv.getStackInRowAndColumn(1, 1)==null||stackBarrel.getItem()!=CannonCore.itemBarrel){
			return false;
		}
		if((stackBarrel.getItemDamage()&0x000000ff)>=30){
			return false;
		}
		this.result=new ItemStack(CannonCore.itemBarrel,1,stackBarrel.getItemDamage()+5);
		return true;
		*/
		int barrel=-1;
		int calibre=0;
		int numBarrel=0;
		
		NBTTagCompound nbt=new NBTTagCompound();
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				ItemStack stack=inv.getStackInRowAndColumn(i, j);
				if(stack==null)continue;
				if(stack.getItem()==CannonCore.itemBarrel){
					if(barrel==-1){
						barrel=(stack.getItemDamage()&0x0000ff00)>>8;
					}else if(barrel!=((stack.getItemDamage()&0x0000ff00)>>8)){
						return false;
					}
					calibre+=(stack.getItemDamage()&0x000000ff);
					if(calibre>30){
						return false;
					}
					numBarrel++;
				}
			}
			if(barrel!=-1){
				break;
			}
		}
		/*
		for(int i=0;i<inv.getSizeInventory();i++){
			ItemStack stack=inv.getStackInSlot(i);
			if(stack==null){
				continue;
					
			}
			if(stack.getItem()==CannonCore.itemBarrel){
				if(calibre==-1){
					calibre=(stack.getItemDamage()&0x000000ff);
				}else if(calibre!=(stack.getItemDamage()&0x000000ff)){
					return false;
				}
				barrel+=((stack.getItemDamage()&0x0000ff00)>>8);
				if(barrel>120){
					return false;
				}
				numBarrel++;
			}else{
				return false;
			}
		}
		*/
		if(numBarrel>1){
			this.result=new ItemStack(CannonCore.itemBarrel,1,calibre+(barrel<<8));
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
		return this.result.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return this.result;
	}
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> aitemstack = NonNullList.create();

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            aitemstack.add(net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return aitemstack;
	}
	@Override
	public boolean canFit(int width, int height) {
		return width+height>=2;
	}
}
