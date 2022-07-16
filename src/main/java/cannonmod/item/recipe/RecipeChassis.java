package cannonmod.item.recipe;

import cannonmod.core.CannonCore;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeChassis extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	public ItemStack result;
	
	public RecipeChassis() {
		result=new ItemStack(CannonCore.itemChassis);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World p_77569_2_) {
		int carriage=0,track=0,engine=0;
		int carriageLvl=-1,engineLvl=0;
		for(int i=0;i<inv.getSizeInventory();i++){
			ItemStack stack=inv.getStackInSlot(i);
			if(stack==null)continue;
			if(stack.getItem()==CannonCore.itemCarriage){
				if(carriage>0){
					return false;
				}else{
					carriageLvl=stack.getItemDamage();
					carriage++;
				}
			}else if(stack.getItem()==CannonCore.itemEngine){
				if(engine>0){
					return false;
				}else{
					engineLvl=stack.getItemDamage()+1;
					engine++;
				}
			}else if(stack.getItem()==CannonCore.itemTrack){
				if(track>1){
					return false;
				}else{
					track++;
				}
			}else {
				return false;
			}
		}
		
		if(carriage<1||track<2)return false;
		this.result=new ItemStack(CannonCore.itemChassis,1,(carriageLvl<<8)+engineLvl);
		
		return true;
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
		return width>=3 && height>=2;
	}
	
}
