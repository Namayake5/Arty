package cannonmod.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemForDebug extends Item {
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			for(int i=0;i<100;i++){
				EntityTippedArrow entity=new EntityTippedArrow(worldIn,pos.getX()+0.5+facing.getFrontOffsetX()
						, pos.getY()+0.5+facing.getFrontOffsetY()
						,pos.getZ()+0.5+facing.getFrontOffsetZ());
				entity.setVelocity(worldIn.rand.nextDouble(), worldIn.rand.nextDouble(), worldIn.rand.nextDouble());
				worldIn.spawnEntity(entity);
			}
		}
		return EnumActionResult.SUCCESS;
	}
}
