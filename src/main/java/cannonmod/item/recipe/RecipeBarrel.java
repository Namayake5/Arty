package cannonmod.item.recipe;

import com.google.gson.JsonObject;

import cannonmod.core.CannonCore;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeBarrel extends IForgeRegistryEntry.Impl<IRecipe>
implements IRecipe {
	public ItemStack result;

	public RecipeBarrel() {
		this.result=new ItemStack(CannonCore.itemBarrel);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World p_77569_2_) {
		System.out.println("called matches");
		int calibre = -1;
		int barrel = 0;
		int numBarrel = 0;

		NBTTagCompound nbt = new NBTTagCompound();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {

				ItemStack stack = inv.getStackInRowAndColumn(j, i);
				if (stack != null && 
						stack.getItem() == CannonCore.itemBarrel) {
					if (calibre == -1) {
						calibre = stack.getItemDamage() & 0xFF;
					} else if (calibre != (stack.getItemDamage() & 0xFF)) {
						return false;
					} 
					barrel += ((stack.getItemDamage() & 0xFF00) >> 8);
					if (barrel > 160) {
						return false;
					}
					numBarrel++;
				} 
			} 
			if (calibre != -1) {
				break;
			}
		} 























		if (numBarrel > 1) {
			this.result = new ItemStack(CannonCore.itemBarrel, 1, (barrel << 8) + calibre);
			return true;
		} 
		return false;
	}



	public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) { 
		//System.out.println("crafting result was called! the result is "+this.result.copy());
		return this.result.copy();
		}






	public ItemStack getRecipeOutput() {
		//System.out.println("recipe output was called! the result is "+this.result.copy());
		return this.result;
		}


	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> aitemstack = NonNullList.create();

		for (int i = 0; i < inv.getSizeInventory(); i++) {

			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack.add( ForgeHooks.getContainerItem(itemstack));
		} 

		return aitemstack;
	}



	public static class Factory implements IRecipeFactory {
		
		public Factory() {
			//System.out.println("factory called");
		}

		@Override
		public IRecipe parse(final JsonContext context, final JsonObject json) {

			return new RecipeBarrel();
		}
	}



	@Override
	public boolean canFit(int width, int height) {
		return width>=2 && height>=1;
	}

}

