package cannonmod.item.recipe;

import cannonmod.core.CannonCore;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeArty extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public ItemStack result;
	
	public RecipeArty()
	{
		this.result=new ItemStack(CannonCore.itemArty);
	}
	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean cannon = false,chassis = false;
		NBTTagCompound nbt=new NBTTagCompound();
		for(int i=0;i<inv.getSizeInventory();i++){
			ItemStack stack=inv.getStackInSlot(i);
			if(stack==null)continue;
			if(!cannon&&stack.getItem()==CannonCore.itemCannon){
				cannon=true;
				nbt.setInteger("Calibre", stack.getTagCompound().getInteger("Calibre"));
				nbt.setInteger("Barrel", stack.getTagCompound().getInteger("Barrel"));
				/**a110*/
				/**a120*/
				int loaderlvl=stack.getTagCompound().getInteger("Loader");
				nbt.setInteger("Loader",loaderlvl==-1?0:loaderlvl);
			}else if(!chassis&&stack.getItem()==CannonCore.itemChassis){
				chassis=true;
				nbt.setInteger("Engine",(stack.getItemDamage()&0x000000ff));
				nbt.setInteger("Motor", ((stack.getItemDamage()&0x0000ff00)>>8));
			}else{
				return false;
			}
		}
		if(!(cannon&&chassis))return false;
		this.result=new ItemStack(CannonCore.itemArty);
		this.result.setTagCompound(nbt);
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return result.copy();
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
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
		return width*height>=2;
	}
}
