package cannonmod.item.recipe;

import cannonmod.core.CannonCore;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeCannon extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public ItemStack result;
	public RecipeCannon() {
		result=new ItemStack(CannonCore.itemCannon);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World p_77569_2_) {
		int barrel=0,cauldron=0,loader=0;
		int calibre=0,length=0,loaderlvl=0;
		for(int i=0;i<inv.getSizeInventory();i++){
			ItemStack stack=inv.getStackInSlot(i);
			if(stack==null)continue;
			if(stack.getItem()==CannonCore.itemBarrel){
				if(barrel>0){
					return false;
				}else{
					calibre=(stack.getItemDamage()&0x000000ff);
					length=(stack.getItemDamage()&0x0000ff00)>>8;
					barrel++;
				}
			}else if(stack.getItem()==Items.CAULDRON){
				if(cauldron>0){
					return false;
				}else{
					cauldron++;
				}
				/**a110*/
			}else if(stack.getItem()==CannonCore.itemLoader){
				if(loader>0){
					return false;
				}else{
					loader++;
					loaderlvl=stack.getItemDamage()+1;
				}
			
			}
			
		}
		if(barrel<1||cauldron<1)return false;
		this.result=new ItemStack(CannonCore.itemCannon);
		NBTTagCompound nbt=new NBTTagCompound();
		nbt.setInteger("Barrel", length);
		nbt.setInteger("Calibre",calibre);
		/**a110*/
		/**a120*/
		nbt.setInteger("Loader", loaderlvl==0?-1:loaderlvl);
		this.result.setTagCompound(nbt);
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
		return width*height>=2;
	}
	
}
