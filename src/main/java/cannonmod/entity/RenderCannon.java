 package cannonmod.entity;
 
 import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
 
 public class RenderCannon extends RenderLivingBase<EntityCannon> {
   public static ResourceLocation[] textures = new ResourceLocation[5];
   
   public static ResourceLocation textureTrack = new ResourceLocation("simplecannon:textures/entity/track.png");
   
   public static ResourceLocation textureCrossbar = new ResourceLocation("simplecannon:textures/crossbars/crossbar.png");
   
   public static ResourceLocation textureWheel = new ResourceLocation("simplecannon:textures/entity/wheel.png");
   
   public static ResourceLocation textureCrawler = new ResourceLocation("simplecannon:textures/entity/crawler.png");
   
   private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");
   
   private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
   
   private final Minecraft mc = Minecraft.getMinecraft();
   
   private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
   
   private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
   
   private final RenderItem itemRenderer;
   
   private final ModelBanner bannerModel = new ModelBanner();
   
   static  {
     for (int i = 0; i < textures.length; i++)
       textures[i] = new ResourceLocation("simplecannon:textures/entity/cannon_" + i + ".png"); 
   }
   
   public RenderCannon(RenderManager renderManager, RenderItem itemRenderer) {
     super(renderManager, new ModelDummy(), 1.0F);
     ModelDummy model = new ModelDummy();
     model.renderer = this;
     this.mainModel = model;
     this.itemRenderer = itemRenderer;
   }
   
   public void doRender(EntityCannon entity, double x, double y, double z, float entityYaw, float partialTicks) {
     float yaw = interpolateRotation(entity.prevRotationYaw + 180.0F, entity.rotationYaw + 180.0F, partialTicks);
     float yawHead = interpolateRotation(entity.prevRotationYawHead + 180.0F, entity.rotationYawHead + 180.0F, partialTicks);
     float pitch = interpolateRotation(entity.prevRotationPitch, entity.rotationPitch, partialTicks);
     entity.getThisModel(this).setRotationAngles(yaw, yawHead - yaw, pitch, entity, partialTicks);
     super.doRender(entity, x, y, z, entityYaw, partialTicks);
     if (entity.getPassengers().isEmpty() || entity.getPassengers().get(0) != (Minecraft.getMinecraft()).player || 
       (Minecraft.getMinecraft()).gameSettings.thirdPersonView != 0) {
       GlStateManager.pushMatrix();
       double l = entity.calibre / 5.0D + entity.lengthOfBarrel / 20.0D + 1.0D;
       GlStateManager.translate(x, y + l / 4.0D, z);
       this.renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
       GlStateManager.pushMatrix();
       GlStateManager.translate(0.0D, entity.calibre * 3.0D / 20.0D, 0.0D);
       GlStateManager.rotate(-yawHead, 0.0F, 1.0F, 0.0F);
       GlStateManager.rotate(-pitch, 1.0F, 0.0F, 0.0F);
       GlStateManager.translate(0.0D, 0.0D, 0.3D - entity.lengthOfBarrel / 10.0D + entity.calibre * 3.0D / 80.0D);
       GlStateManager.pushMatrix();
       GlStateManager.translate(-entity.calibre / 20.0D - 0.125D, 0.0D, 0.0D);
       GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
       GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
       renderItem(entity, 0, true, true);
       GlStateManager.popMatrix();
       GlStateManager.pushMatrix();
       GlStateManager.translate(entity.calibre / 20.0D + 0.125D, 0.0D, 0.0D);
       GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
       GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
       renderItem(entity, 1, false, true);
       GlStateManager.popMatrix();
       GlStateManager.popMatrix();
       GlStateManager.pushMatrix();
       GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
       GlStateManager.pushMatrix();
       GlStateManager.translate((-entity.calibre / 20.0F) - 0.8D, 0.0D, 0.0D);
       GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
       renderItem(entity, 2, false, false);
       GlStateManager.popMatrix();
       GlStateManager.pushMatrix();
       GlStateManager.translate((entity.calibre / 20.0F) + 0.8D, 0.0D, 0.0D);
       GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
       renderItem(entity, 3, false, false);
       GlStateManager.popMatrix();
       GlStateManager.popMatrix();
       GlStateManager.popMatrix();
     } 
   }
   
   private void renderItem(EntityCannon entity, int symbolNum, boolean mirror, boolean laid) {
     ItemStack itemstack = entity.inventory[entity.getSymbolSlotNum(symbolNum)];
     if (itemstack != null) {
       double l = entity.calibre / 5.0D + entity.lengthOfBarrel / 20.0D + 1.0D;
       EntityItem entityitem = new EntityItem(entity.world, 0.0D, 0.0D, 0.0D, itemstack);
       Item item = entityitem.getItem().getItem();
       (entityitem.getItem()).setCount(1);
       entityitem.hoverStart = 0.0F;
       GlStateManager.pushMatrix();
       GlStateManager.disableLighting();
       if (item == Items.FILLED_MAP)
         GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F); 
       if (item instanceof net.minecraft.item.ItemMap) {
         this.renderManager.renderEngine.bindTexture(MAP_BACKGROUND_TEXTURES);
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         float f = 0.0078125F;
         GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
         GlStateManager.translate(-64.0F, -64.0F, 0.0F);
         MapData mapdata = Items.FILLED_MAP.getMapData(entityitem.getItem(), entity.world);
         GlStateManager.translate(0.0F, 0.0F, -1.0F);
         if (mapdata != null)
           this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true); 
       } else if (item == Items.PAINTING) {
         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         this.renderManager.renderEngine.bindTexture(KRISTOFFER_PAINTING_TEXTURE);
         EntityPainting.EnumArt art = entity.getArt(symbolNum);
         float f = 0.0625F;
         if (laid) {
           boolean isWidthLonger = (art.sizeX > art.sizeY);
           if (isWidthLonger) {
             GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
             float scale = entity.calibre / 10.0F / art.sizeY;
             GlStateManager.scale(scale, scale, scale);
           } else {
             float scale = entity.calibre / 10.0F / art.sizeX;
             GlStateManager.scale(scale, scale, scale);
           } 
         } else {
           double scale = l / 4.0D / art.sizeY;
           GlStateManager.scale(scale, scale, scale);
         } 
         if (this.renderOutlines) {
           GlStateManager.enableColorMaterial();
           GlStateManager.enableOutlineMode(getTeamColor(entity));
         } 
         renderPainting(entity, art.sizeX, art.sizeY, art.offsetX, art.offsetY, mirror);
         if (this.renderOutlines) {
           GlStateManager.disableOutlineMode();
           GlStateManager.disableColorMaterial();
         } 
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
       } else if (item == Items.BANNER) {
         boolean flag = (entity.world != null);
         int i = 0;
         long j = flag ? entity.world.getTotalWorldTime() : 0L;
         GlStateManager.pushMatrix();
         float f = 0.6666667F;
         float f3 = (float)(entity.posX * 7.0D + entity.posY * 9.0D + entity.posZ * 13.0D) + (float)j;
         this.bannerModel.bannerSlate.rotateAngleX = (-0.01F + 0.01F * MathHelper.cos(f3 * 3.1415927F * 0.02F)) * 3.1415927F;
         GlStateManager.enableRescaleNormal();
         ResourceLocation resourcelocation = getBannerResourceLocation(itemstack, entity.bannerDatas[symbolNum]);
         if (resourcelocation != null) {
           bindTexture(resourcelocation);
           GlStateManager.pushMatrix();
           if (laid) {
             GlStateManager.translate(0.0D, 0.0D, entity.calibre * 0.01D);
             if (entity.lengthOfBarrel > entity.calibre * 4) {
               float scale = entity.calibre / 10.0F * 16.0F / 20.0F;
               GlStateManager.scale(scale, scale, scale);
             } else {
               float scale = entity.calibre / 10.0F * 16.0F / 20.0F;
               GlStateManager.scale(scale, entity.lengthOfBarrel / 20.0F * 16.0F / 40.0F, scale);
             } 
           } else {
             GlStateManager.translate(0.0D, l / 8.0D, 0.15D);
             GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
             float scale = (float)l / 4.0F * 16.0F / 40.0F;
             GlStateManager.scale(scale, scale, scale);
           } 
           this.bannerModel.bannerSlate.render(0.0625F);
           GlStateManager.popMatrix();
         } 
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
       } else {
         float scale = entity.calibre / 10.0F;
         GlStateManager.scale(scale, scale, scale);
         if (laid)
           if (mirror) {
             GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
           } else {
             GlStateManager.rotate(-180.0F, 0.0F, 0.0F, 1.0F);
           }  
         GlStateManager.pushAttrib();
         RenderHelper.enableStandardItemLighting();
         this.itemRenderer.renderItem(entityitem.getItem(), ItemCameraTransforms.TransformType.FIXED);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.popAttrib();
       } 
       GlStateManager.enableLighting();
       GlStateManager.popMatrix();
     } 
   }
   
   private void renderPainting(EntityCannon painting, int width, int height, int textureU, int textureV, boolean mirror) {
     float f = -width / 2.0F;
     float f1 = -height / 2.0F;
     float f2 = 0.5F;
     float f3 = 0.75F;
     float f4 = 0.8125F;
     float f5 = 0.0F;
     float f6 = 0.0625F;
     float f7 = 0.75F;
     float f8 = 0.8125F;
     float f9 = 0.001953125F;
     float f10 = 0.001953125F;
     float f11 = 0.7519531F;
     float f12 = 0.7519531F;
     float f13 = 0.0F;
     float f14 = 0.0625F;
     Tessellator tessellator = Tessellator.getInstance();
     BufferBuilder vertexbuffer = tessellator.getBuffer();
     vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
     if (!mirror) {
       vertexbuffer.pos(f + width, f1, -0.5D).tex((textureU + width) / 256.0D, (textureV + height) / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
       vertexbuffer.pos(f, f1, -0.5D).tex(textureU / 256.0D, (textureV + height) / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
       vertexbuffer.pos(f, f1 + height, -0.5D).tex(textureU / 256.0D, textureV / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
       vertexbuffer.pos(f + width, f1 + height, -0.5D).tex((textureU + width) / 256.0D, textureV / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     } else {
       vertexbuffer.pos(f + width, f1, -0.5D).tex((textureU + width) / 256.0D, textureV / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
       vertexbuffer.pos(f, f1, -0.5D).tex(textureU / 256.0D, textureV / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
       vertexbuffer.pos(f, f1 + height, -0.5D).tex(textureU / 256.0D, (textureV + height) / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
       vertexbuffer.pos(f + width, f1 + height, -0.5D).tex((textureU + width) / 256.0D, (textureV + height) / 256.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     } 
     tessellator.draw();
   }
   
   @Nullable
   private ResourceLocation getBannerResourceLocation(ItemStack stack, EntityCannon.BannerData data) {
     if (data == null) {
       data = new EntityCannon.BannerData();
       data.patterns = null;
       if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", 10)) {
         NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("BlockEntityTag");
         if (nbttagcompound.hasKey("Patterns"))
           data.patterns = nbttagcompound.getTagList("Patterns", 10).copy(); 
         if (nbttagcompound.hasKey("Base", 99)) {
           data.baseColor = nbttagcompound.getInteger("Base");
         } else {
           data.baseColor = stack.getMetadata() & 0xF;
         } 
       } else {
         data.baseColor = stack.getMetadata() & 0xF;
       } 
       data.patternList = null;
       data.colorList = null;
       data.patternResourceLocation = "";
       data.patternDataSet = true;
       data.patternList = Lists.newArrayList();
       data.colorList = Lists.newArrayList();
       data.patternList.add(BannerPattern.BASE);
       data.colorList.add(EnumDyeColor.byDyeDamage(data.baseColor));
       data.patternResourceLocation = "b" + data.baseColor;
       if (data.patterns != null)
         for (int i = 0; i < data.patterns.tagCount(); i++) {
           NBTTagCompound nbttagcompound = data.patterns.getCompoundTagAt(i);
           BannerPattern tileentitybanner$enumbannerpattern = BannerPattern.byHash(nbttagcompound.getString("Pattern"));
           if (tileentitybanner$enumbannerpattern != null) {
             data.patternList.add(tileentitybanner$enumbannerpattern);
             int j = nbttagcompound.getInteger("Color");
             data.colorList.add(EnumDyeColor.byDyeDamage(j));
             data.patternResourceLocation += tileentitybanner$enumbannerpattern.getHashname() + j;
           } 
         }  
     } 
     return BannerTextures.BANNER_DESIGNS.getResourceLocation(data.patternResourceLocation, data.patternList, data.colorList);
   }
   
   protected ResourceLocation getEntityTexture(EntityCannon entity) { return textures[entity.design]; }
   
   protected void rotateCorpse(EntityCannon entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {}
   
   protected void renderLivingLabel(EntityCannon entityIn, String str, double x, double y, double z, int maxDistance) {
     double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);
     if (d0 <= (maxDistance * maxDistance)) {
       boolean flag = entityIn.isSneaking();
       float f = this.renderManager.playerViewY;
       float f1 = this.renderManager.playerViewX;
       boolean flag1 = (this.renderManager.options.thirdPersonView == 2);
       float f2 = entityIn.height + 0.5F + (entityIn.calibre / 10 * 2) - (flag ? 0.25F : 0.0F);
       int i = "deadmau5".equals(str) ? -10 : 0;
       EntityRenderer.drawNameplate(getFontRendererFromRenderManager(), str, (float)x, (float)y + f2, (float)z, i, f, f1, flag1, flag);
     } 
   }
 }


/* Location:              C:\Users\adamh\Downloads\CustomizableArtilleryMODv1_3_0forMC1.10.2-deobf.jar!\cannonmod\entity\RenderCannon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */