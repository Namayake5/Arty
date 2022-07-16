 package cannonmod.core;
 import java.util.ArrayList;
import java.util.List;

import cannonmod.entity.EntityTracer;
import cannonmod.item.ItemArmorPlate;
import cannonmod.item.ItemArty;
import cannonmod.item.ItemBarrel;
import cannonmod.item.ItemCannon;
import cannonmod.item.ItemCarriage;
import cannonmod.item.ItemChassis;
import cannonmod.item.ItemEngine;
import cannonmod.item.ItemForDebug;
import cannonmod.item.ItemLoader;
import cannonmod.item.ItemTrack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
 
 
 
 
 
 
 
 
 
 @Mod(modid = "simplecannon", name = "CustomArtilleryMod", version = "Alpha1_4_0")
 public class CannonCore
 {
   @Instance("simplecannon")
   public static CannonCore instance;
   @SidedProxy(clientSide = "cannonmod.core.RenderProxyClient", serverSide = "cannonmod.core.RenderProxy")
   public static RenderProxy proxy;
   
   public static String MODID="simplecannon";
   public static double cannonHealth;
   public static double bulletSpeed;
   public static double cannonSpeed;
			public static boolean enableGunpowderRecipe;
   public static KeyBinding buttonCannonGui;
   public static Item itemArty;
   public static Item itemArmor;
   public static Item itemCannon;
   public static Item itemBarrel;
   public static Item itemCarriage;
   public static Item itemEngine;
   public static Item itemTrack;
   public static Item itemChassis;
   public static Item itemLoader;
   public static Item itemForDebug;
   public static EventCaller eventCaller;
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
   
   public static SoundEvent cannonRun;
   
   public static List<EntityTracer> tracersList = new ArrayList();
   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
     Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
     cfg.load();
     Property healthP = cfg.get("system", "Max HP of artilleries (Default:70)", 70.0D);
     Property bsP = cfg.get("system", "Bullet speed (Default:0.06)", 0.06D);
     Property csP = cfg.get("system", "Artilleries' speed (Default:300.0)", 300.0D);
     Property gunpowderP = cfg.get("system", "Enable gunpowder recipe", true);
     cfg.save();
     
     cannonHealth = healthP.getDouble();
     bulletSpeed = bsP.getDouble();
     cannonSpeed = csP.getDouble();
			enableGunpowderRecipe=gunpowderP.getBoolean();
     
     
 
     
     EntityRegistry.registerModEntity(new ResourceLocation(MODID,"Artillery"), cannonmod.entity.EntityCannon.class, "Artillery", 0, this, 80, 4, true);
     EntityRegistry.registerModEntity(new ResourceLocation(MODID,"FallingBlockEx"),cannonmod.entity.EntityFallingBlockEx.class, "FallingBlockEx", 1, this, 80, 4, true);
     EntityRegistry.registerModEntity(new ResourceLocation(MODID,"Tracer"),EntityTracer.class, "Tracer", 2, this, 80, 4, true);
     
     proxy.init();

     
     itemForDebug = (new ItemForDebug()).setUnlocalizedName("ItemForDebug").setCreativeTab(CreativeTabs.MISC);
 
     

     eventCaller = new EventCaller();
     MinecraftForge.EVENT_BUS.register(eventCaller);
     FMLCommonHandler.instance().bus().register(eventCaller);
     buttonCannonGui = new KeyBinding("key.cannongui", 19, "key.categories.cannonmod");
 
 
     
     ClientRegistry.registerKeyBinding(buttonCannonGui);
     NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
     INSTANCE.registerMessage(MessageCannonGuiHandler.class, MessageCannonGui.class, 0, Side.SERVER);
     INSTANCE.registerMessage(MessageCannonParticleHandler.class, MessageCannonParticle.class, 1, Side.CLIENT);
     
     ForgeChunkManager.setForcedChunkLoadingCallback(this, new ShellChunkManager());
   }
   @Mod.EventHandler
   //この関数でMODファイル自体をイベントの発火先にする。
   public void construct(FMLConstructionEvent event) {
       MinecraftForge.EVENT_BUS.register(this);
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {}

   public SoundEvent registerSound(String soundName,RegistryEvent.Register<SoundEvent> event) {
	   ResourceLocation soundID = new ResourceLocation(MODID, soundName);
	   SoundEvent se=(new SoundEvent(soundID)).setRegistryName(soundID);
	   event.getRegistry().register(se);
	   return se;
   }
   
   @SubscribeEvent
   public void registerSounds(RegistryEvent.Register<SoundEvent> event){
	   cannonRun = registerSound("simplecannon.run",event);
   
   }

   @SubscribeEvent
   public void registerItems(RegistryEvent.Register<Item> event){
	   //System.out.println("registring items");

	   itemArty = (new ItemArty()).setUnlocalizedName("artillery").setRegistryName("artillery");
	   event.getRegistry().register(itemArty);

	   itemArmor = (new ItemArmorPlate()).setUnlocalizedName("armorplate").setRegistryName("armorplate")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemArmor);

	   itemCannon = (new ItemCannon()).setUnlocalizedName("cannon").setRegistryName("cannon")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemCannon);

	   itemBarrel = (new ItemBarrel()).setUnlocalizedName("barrel").setRegistryName("barrel")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemBarrel);

	   itemCarriage = (new ItemCarriage()).setUnlocalizedName("carriage").setRegistryName("carriage")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemCarriage);

	   itemEngine = (new ItemEngine()).setUnlocalizedName("engine").setRegistryName("engine")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemEngine);

	   itemTrack = (new ItemTrack()).setUnlocalizedName("track").setRegistryName("track")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemTrack);

	   itemChassis = (new ItemChassis()).setUnlocalizedName("chassis").setRegistryName("chassis")
			   .setCreativeTab(CreativeTabs.MATERIALS);
	   event.getRegistry().register(itemChassis);


 itemLoader = (new ItemLoader()).setUnlocalizedName("loader").setRegistryName("loader")
				.setCreativeTab(CreativeTabs.MATERIALS);
 event.getRegistry().register(itemLoader);

}
@SubscribeEvent
@SideOnly(Side.CLIENT)
public void registerModels(ModelRegistryEvent event) {
	ModelLoader.setCustomModelResourceLocation(itemArty, 0, new ModelResourceLocation("simplecannon:arty", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemArmor, 0, new ModelResourceLocation("simplecannon:armor", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemCannon, 0, new ModelResourceLocation("simplecannon:cannon", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemBarrel, 0, new ModelResourceLocation("simplecannon:barrel", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemCarriage, 0, new ModelResourceLocation("simplecannon:carriage", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemCarriage, 1, new ModelResourceLocation("simplecannon:carriageM1", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemCarriage, 2, new ModelResourceLocation("simplecannon:carriageM2", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemCarriage, 3, new ModelResourceLocation("simplecannon:carriageM3", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemEngine, 0, new ModelResourceLocation("simplecannon:engineM1", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemEngine, 1, new ModelResourceLocation("simplecannon:engineM2", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemEngine, 2, new ModelResourceLocation("simplecannon:engineM3", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemTrack, 0, new ModelResourceLocation("simplecannon:track", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemChassis, 0, new ModelResourceLocation("simplecannon:chassis", "inventory"));
       
       ModelLoader.setCustomModelResourceLocation(itemLoader, 0, new ModelResourceLocation("simplecannon:loaderM1", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemLoader, 1, new ModelResourceLocation("simplecannon:loaderM2", "inventory"));
       ModelLoader.setCustomModelResourceLocation(itemLoader, 2, new ModelResourceLocation("simplecannon:loaderM3", "inventory"));
}
 
@SubscribeEvent
public void registerRecipes(RegistryEvent.Register<IRecipe> event){
	 //System.out.println("registring recipes");
     if (enableGunpowderRecipe) {
    	 event.getRegistry().registerAll(
       new ShapedOreRecipe(new ResourceLocation(MODID,"gunpowder1"), new ItemStack(Items.GUNPOWDER, 3), new Object[] { "XY", "YX", Character.valueOf('X'), new ItemStack(Items.COAL, 1, 32767), Character.valueOf('Y'), "dustRedstone" }).setRegistryName("gunpowder1"),
       new ShapedOreRecipe(new ResourceLocation(MODID,"gunpowder2"),new ItemStack(Items.GUNPOWDER, 3), new Object[] { "YX", "XY", Character.valueOf('X'), new ItemStack(Items.COAL, 1, 32767), Character.valueOf('Y'), "dustRedstone" }).setRegistryName("gunpowder2"));
     } 
     event.getRegistry().registerAll(
     new ShapedOreRecipe(new ResourceLocation(MODID,"loadermk1"),new ItemStack(itemLoader, 1, 0), new Object[] { "P  ", "PB ", "URU", 
    		 Character.valueOf('P'), Blocks.PISTON, Character.valueOf('B'), Items.BREWING_STAND, Character.valueOf('U'), Items.BUCKET, Character.valueOf('R'), "record" }).setRegistryName("loadermk1"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"loadermk2"),new ItemStack(itemLoader, 1, 1), new Object[] { "PBD", "UBU", "URU", 
    		 Character.valueOf('P'), Blocks.PISTON, Character.valueOf('D'), "gemDiamond", Character.valueOf('B'), Items.BREWING_STAND, Character.valueOf('U'), Items.CAULDRON, Character.valueOf('R'), "record" }).setRegistryName("loadermk2"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"loadermk3"),new ItemStack(itemLoader, 1, 2), new Object[] { "PNU", "NUR", "URD", 
    		 Character.valueOf('P'), Blocks.PISTON, Character.valueOf('D'), "gemDiamond", Character.valueOf('U'), Items.CAULDRON, Character.valueOf('R'), "record", Character.valueOf('N'), "blockRedstone" }).setRegistryName("loadermk3"),
     
     new ShapedOreRecipe(new ResourceLocation(MODID,"armorplate"),new ItemStack(itemArmor, 2), new Object[] { "XXX", "XXX", Character.valueOf('X'), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE }).setRegistryName("armorplate"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"barrel"),new ItemStack(itemBarrel, 2, 2565), new Object[] { "XXX", "   ", "XXX", Character.valueOf('X'), "ingotIron" }).setRegistryName("barrel"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"track"),new ItemStack(itemTrack, 2), new Object[] { "XXX", "XYX", "XXX", Character.valueOf('X'), Blocks.IRON_BARS, Character.valueOf('Y'), "blockIron" }).setRegistryName("track"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"carriage"),new ItemStack(itemCarriage, 1, 0), new Object[] { "X X", "YYY", Character.valueOf('X'), "ingotIron", Character.valueOf('Y'), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE }).setRegistryName("carriage"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"carriage1"),new ItemStack(itemCarriage, 1, 1), new Object[] { " X ", "LIR", "SCS", Character.valueOf('X'), new ItemStack(itemCarriage, 1, 0), Character.valueOf('L'), "blockLapis", Character.valueOf('I'), "blockIron", 
             Character.valueOf('R'), "blockRedstone", Character.valueOf('S'), Items.REPEATER, Character.valueOf('C'), Items.COMPARATOR }).setRegistryName("carriage1"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"carriage2"),new ItemStack(itemCarriage, 1, 2), new Object[] { " X ", "LIR", "SCS", Character.valueOf('X'), new ItemStack(itemCarriage, 1, 1), Character.valueOf('L'), "blockLapis", Character.valueOf('I'), "blockIron", 
             Character.valueOf('R'), "blockRedstone", Character.valueOf('S'), Items.REPEATER, Character.valueOf('C'), Items.COMPARATOR }).setRegistryName("carriage2"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"carriage3"),new ItemStack(itemCarriage, 1, 3), new Object[] { " X ", "LIR", "SCS", Character.valueOf('X'), new ItemStack(itemCarriage, 1, 2), Character.valueOf('L'), "blockLapis", Character.valueOf('I'), "blockIron", 
             Character.valueOf('R'), "blockRedstone", Character.valueOf('S'), Items.REPEATER, Character.valueOf('C'), Items.COMPARATOR }).setRegistryName("carriage3"),
     
     new ShapedOreRecipe(new ResourceLocation(MODID,"engine1"),new ItemStack(itemEngine, 1, 0), new Object[] { "GPG", "PIP", "PDP", Character.valueOf('I'), "ingotIron",
             Character.valueOf('P'), Blocks.PISTON, Character.valueOf('G'), "ingotGold", Character.valueOf('D'), Items.CAULDRON }).setRegistryName("engine1"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"engine2"),new ItemStack(itemEngine, 1, 1), new Object[] { "PDP", "PIP", "PCP", 
    		 Character.valueOf('P'), Blocks.PISTON, Character.valueOf('D'), "gemDiamond", Character.valueOf('I'), "blockIron", Character.valueOf('C'), Items.CAULDRON }).setRegistryName("engine2"),
     new ShapedOreRecipe(new ResourceLocation(MODID,"engine3"),new ItemStack(itemEngine, 1, 2), new Object[] { "FFF", "ECE", "DDD", Character.valueOf('F'), Blocks.IRON_BARS, Character.valueOf('E'), new ItemStack(itemEngine, 1, 1), 
             Character.valueOf('C'), Items.COMPARATOR, Character.valueOf('D'), "gemDiamond" }).setRegistryName("engine3")
 
);
     {
     ItemStack result = new ItemStack(itemArty);
     result.setTagCompound(new NBTTagCompound());
     event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(MODID,"cannonchassis"),result, new Object[] { itemCannon, itemChassis }).setRegistryName("cannonchassis"));
     }
 
     {
     ItemStack result = new ItemStack(itemBarrel, 1, 255);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"barrel1"),result, new Object[] { "XXX", Character.valueOf('X'), new ItemStack(itemBarrel, 1, 65535) }).setRegistryName("barrel1"));
     }
 
     {
     ItemStack result = new ItemStack(itemBarrel, 1, 255);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"barrel2"),result, new Object[] { "XX", Character.valueOf('X'), new ItemStack(itemBarrel, 1, 65535) }).setRegistryName("barrel2"));
     }
 
     {
     ItemStack result = new ItemStack(itemBarrel, 1, 65280);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"barrel3"),result, new Object[] { "X", "X", "X", Character.valueOf('X'), new ItemStack(itemBarrel, 1, 65535) }).setRegistryName("barrel3"));
     }
 
     {
     ItemStack result = new ItemStack(itemBarrel, 1, 65280);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"barrel4"),result, new Object[] { "X", "X", Character.valueOf('X'), new ItemStack(itemBarrel, 1, 65535) }).setRegistryName("barrel4"));
     }
     {
     ItemStack result = new ItemStack(itemCannon);
     result.setTagCompound(new NBTTagCompound());
     ItemStack barrel = new ItemStack(itemBarrel, 1, 65535);
     event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(MODID,"cannon"),result, new Object[] { barrel, Items.CAULDRON }).setRegistryName("cannon"));
     }
     {
     ItemStack result = new ItemStack(itemChassis, 1, 65278);
     event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(MODID,"artillery1"),result, new Object[] { itemEngine, itemChassis, itemTrack, itemTrack }).setRegistryName("artillery1"));
     }
     {
     ItemStack result = new ItemStack(itemChassis, 1, 65024);
     event.getRegistry().register(new ShapelessOreRecipe(new ResourceLocation(MODID,"artillery2"),result, new Object[] { itemChassis, itemTrack, itemTrack }).setRegistryName("artillery2"));
     }
     {
     ItemStack result = new ItemStack(itemArty);
     NBTTagCompound nbt = new NBTTagCompound();
     nbt.setInteger("Calibre", 254);
     result.setTagCompound(nbt);
     ItemStack arty = new ItemStack(itemArty);
     NBTTagCompound nbt1 = new NBTTagCompound();
     nbt1.setInteger("Calibre", 255);
     nbt1.setInteger("Design", 255);
     arty.setTagCompound(nbt1);
     event.getRegistry().registerAll(
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor1"),result, new Object[] { arty, itemArmor }).setRegistryName("armor1"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor2"),result, new Object[] { arty, itemArmor, itemArmor }).setRegistryName("armor2"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor3"),result, new Object[] { arty, itemArmor, itemArmor, itemArmor }).setRegistryName("armor3"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor4"),result, new Object[] { arty, itemArmor, itemArmor, itemArmor, itemArmor }).setRegistryName("armor4"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor5"),result, new Object[] { arty, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor }).setRegistryName("armor5"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor6"),result, new Object[] { arty, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor }).setRegistryName("armor6"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor7"),result, new Object[] { arty, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor }).setRegistryName("armor7"),
    		 new ShapelessOreRecipe(new ResourceLocation(MODID,"armor8"),result, new Object[] { arty, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor, itemArmor }).setRegistryName("armor8")
    		 );
     }
     {
     ItemStack result = new ItemStack(itemArty);
     NBTTagCompound nbt = new NBTTagCompound();
     nbt.setInteger("Calibre", 255);
     nbt.setInteger("Design", 0);
     result.setTagCompound(nbt);
     ItemStack arty = new ItemStack(itemArty);
     NBTTagCompound nbt1 = new NBTTagCompound();
     nbt1.setInteger("Calibre", 255);
     nbt1.setInteger("Design", 255);
     arty.setTagCompound(nbt1);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo1"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), "dyeGray", Character.valueOf('A'), arty }).setRegistryName("camo1"));
     }
     {
     ItemStack result = new ItemStack(itemArty);
     NBTTagCompound nbt = new NBTTagCompound();
     nbt.setInteger("Calibre", 255);
     nbt.setInteger("Design", 1);
     result.setTagCompound(nbt);
     ItemStack arty = new ItemStack(itemArty);
     NBTTagCompound nbt1 = new NBTTagCompound();
     nbt1.setInteger("Calibre", 255);
     nbt1.setInteger("Design", 255);
     arty.setTagCompound(nbt1);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo2"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), "treeLeaves", Character.valueOf('A'), arty }).setRegistryName("camo2"));
     }
     {
     ItemStack result = new ItemStack(itemArty);
     NBTTagCompound nbt = new NBTTagCompound();
     nbt.setInteger("Calibre", 255);
     nbt.setInteger("Design", 2);
     result.setTagCompound(nbt);
     ItemStack arty = new ItemStack(itemArty);
     NBTTagCompound nbt1 = new NBTTagCompound();
     nbt1.setInteger("Calibre", 255);
     nbt1.setInteger("Design", 255);
     arty.setTagCompound(nbt1);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo3-1"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), "sandstone", Character.valueOf('A'), arty }).setRegistryName("camo3-1"));
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo3-2"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), Blocks.SAND, Character.valueOf('A'), arty }).setRegistryName("camo3-2"));
     }
     {
     ItemStack result = new ItemStack(itemArty);
     NBTTagCompound nbt = new NBTTagCompound();
     nbt.setInteger("Calibre", 255);
     nbt.setInteger("Design", 3);
     result.setTagCompound(nbt);
     ItemStack arty = new ItemStack(itemArty);
     NBTTagCompound nbt1 = new NBTTagCompound();
     nbt1.setInteger("Calibre", 255);
     nbt1.setInteger("Design", 255);
     arty.setTagCompound(nbt1);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo4"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), "stone", Character.valueOf('A'), arty }).setRegistryName("camo4"));
     }
     {
     ItemStack result = new ItemStack(itemArty);
     NBTTagCompound nbt = new NBTTagCompound();
     nbt.setInteger("Calibre", 255);
     nbt.setInteger("Design", 4);
     result.setTagCompound(nbt);
     ItemStack arty = new ItemStack(itemArty);
     NBTTagCompound nbt1 = new NBTTagCompound();
     nbt1.setInteger("Calibre", 255);
     nbt1.setInteger("Design", 255);
     arty.setTagCompound(nbt1);
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo5-1"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), Items.SNOWBALL, Character.valueOf('A'), arty }).setRegistryName("camo5-1"));
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo5-2"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), Blocks.SNOW, Character.valueOf('A'), arty }).setRegistryName("camo5-2"));
     event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID,"camo5-3"),result, new Object[] { "XXX", "XAX", "XXX", Character.valueOf('X'), Blocks.SNOW_LAYER, Character.valueOf('A'), arty }).setRegistryName("camo5-3"));
     }
     /*
     GameRegistry.addRecipe(new RecipeArty());
     RecipeSorter.register("cannonmod:arty", RecipeArty.class, RecipeSorter.Category.SHAPELESS, "");
     GameRegistry.addRecipe(new RecipeBarrel());
     RecipeSorter.register("cannonmod:barrel", RecipeBarrel.class, RecipeSorter.Category.SHAPED, "");
     GameRegistry.addRecipe(new RecipeBarrelExpand());
     RecipeSorter.register("cannonmod:barrelexpand", RecipeBarrelExpand.class, RecipeSorter.Category.SHAPED, "");
     GameRegistry.addRecipe(new RecipeCannon());
     RecipeSorter.register("cannonmod:cannon", RecipeCannon.class, RecipeSorter.Category.SHAPELESS, "");
     GameRegistry.addRecipe(new RecipeChassis());
     RecipeSorter.register("cannonmod:chassis", RecipeChassis.class, RecipeSorter.Category.SHAPELESS, "");
     GameRegistry.addRecipe(new RecipeArtyArmor());
     RecipeSorter.register("cannonmod:artyarmor", RecipeArty.class, RecipeSorter.Category.SHAPELESS, "");
     GameRegistry.addRecipe(new RecipeArtyCamo());
     RecipeSorter.register("cannonmod:artycamo", RecipeArtyCamo.class, RecipeSorter.Category.SHAPED, "");
     */

}
}

/* Location:              C:\Users\adamh\Downloads\CustomizableArtilleryMODv1_3_0forMC1.10.2-deobf.jar!\cannonmod\core\CannonCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */