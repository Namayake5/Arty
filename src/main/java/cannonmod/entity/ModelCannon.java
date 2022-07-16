 package cannonmod.entity;
 
 import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
 
 public class ModelCannon extends ModelBase {
   ModelRenderer carriage;
   
   ModelRenderer cannon;
   
   ModelRenderer tracks;
   
   ModelRenderer body;
   
   RenderCannon renderer;
   
   public int wheelNum;
   
   ModelRenderer[] wheelsL;
   
   ModelRenderer[] wheelsR;
   
   public int vehicleLengthx16;
   
   public int vehicleWidthx16;
   
   public float trackScale;
   
   public float prevPartialTicks;
   
   public double crawlerAnimeL;
   
   public double crawlerAnimeR;
   
   public ModelCannon(int barrel, int calibre, int posOfBarrel, int design, boolean hasTurret, RenderCannon renderer) {
     this.crawlerAnimeL = 0.0D;
     this.crawlerAnimeR = 0.0D;
     this.textureHeight = 128;
     this.textureWidth = 256;
     this.renderer = renderer;
     setTextureOffset("carriage.UcarriageL", 0, 0);
     setTextureOffset("carriage.Lcarriage", 0, 0);
     setTextureOffset("carriage.UcarriageR", 0, 0);
     setTextureOffset("carriage.carriageBox", 0, 0);
     setTextureOffset("carriage.cupola", 0, 0);
     setTextureOffset("cannon.barrelB", 0, 0);
     setTextureOffset("cannon.barrelL", 0, 0);
     setTextureOffset("cannon.barrelR", 0, 0);
     setTextureOffset("cannon.barrelT", 0, 0);
     setTextureOffset("cannon.breech", 0, 0);
     setTextureOffset("tracks.trackL", 0, 0);
     setTextureOffset("tracks.trackR", 0, 0);
     setTextureOffset("body.stand", 0, 0);
     setTextureOffset("body.chassis", 0, 0);
     setTextureOffset("body.engine", 0, 0);
     setTextureOffset("body.bottom", 0, 0);
     setTextureOffset("body.schurzenL", 0, 0);
     setTextureOffset("body.schurzenR", 0, 0);
     int l = 2 * calibre + barrel / 2 + 16;
     this.vehicleLengthx16 = l;
     this.vehicleWidthx16 = calibre + 16;
     this.wheelNum = MathHelper.ceil((this.vehicleLengthx16 / this.vehicleWidthx16)) * 2 + 3;
     this.wheelsL = new ModelRenderer[this.wheelNum];
     this.wheelsR = new ModelRenderer[this.wheelNum];
     for (int i = 1; i < this.wheelNum - 1; i++) {
       setTextureOffset("wheelL" + i + ".box", 0, 0);
       setTextureOffset("wheelR" + i + ".box", 0, 0);
     } 
     setTextureOffset("wheelL0.box", 0, 64);
     setTextureOffset("wheelR0.box", 0, 64);
     setTextureOffset("wheelL" + (this.wheelNum - 1) + ".box", 0, 64);
     setTextureOffset("wheelR" + (this.wheelNum - 1) + ".box", 0, 64);
     this.trackScale = l / 32.0F * (this.wheelNum - 1);
     this.tracks = new ModelRenderer(this, "tracks");
     this.tracks.setRotationPoint(0.0F, (24.0F - (l / 4)) / this.trackScale, 0.0F);
     setRotation(this.tracks, 0.0F, 0.0F, 0.0F);
     this.tracks.mirror = true;
     this.tracks.addBox("trackL", ((calibre + 16) / 2) / this.trackScale - 8.0F, 0.0F, -32.0F, 8, 16, 64);
     this.tracks.addBox("trackR", -((calibre + 16) / 2) / this.trackScale, 0.0F, -32.0F, 8, 16, 64);
     int center = (this.wheelNum - 1) / 2;
     for (int i = 0; i < this.wheelNum; i++) {
       this.wheelsL[i] = new ModelRenderer(this, "wheelL" + i);
       this.wheelsR[i] = new ModelRenderer(this, "wheelR" + i);
     } 
     for (int i = 1; i < this.wheelNum - 1; i++) {
       this.wheelsL[i].setRotationPoint(0.0F, 24.0F / this.trackScale - 16.0F, ((i - center) * 32));
       this.wheelsR[i].setRotationPoint(0.0F, 24.0F / this.trackScale - 16.0F, ((i - center) * 32));
       this.wheelsL[i].addBox("box", ((calibre + 16) / 2) / this.trackScale - 16.0F, -16.0F, -16.0F, 16, 32, 32);
       this.wheelsR[i].addBox("box", -((calibre + 16) / 2) / this.trackScale, -16.0F, -16.0F, 16, 32, 32);
     } 
     this.wheelsL[0].setRotationPoint(0.0F, 24.0F / this.trackScale - 24.0F, (-center * 32 + 8));
     this.wheelsR[0].setRotationPoint(0.0F, 24.0F / this.trackScale - 24.0F, (-center * 32 + 8));
     this.wheelsL[0].addBox("box", ((calibre + 16) / 2) / this.trackScale - 16.0F, -8.0F, -8.0F, 16, 16, 16);
     this.wheelsR[0].addBox("box", -((calibre + 16) / 2) / this.trackScale, -8.0F, -8.0F, 16, 16, 16);
     this.wheelsL[this.wheelNum - 1].setRotationPoint(0.0F, 24.0F / this.trackScale - 24.0F, (center * 32 - 8));
     this.wheelsR[this.wheelNum - 1].setRotationPoint(0.0F, 24.0F / this.trackScale - 24.0F, (center * 32 - 8));
     this.wheelsL[this.wheelNum - 1].addBox("box", ((calibre + 16) / 2) / this.trackScale - 16.0F, -8.0F, -8.0F, 16, 16, 16);
     this.wheelsR[this.wheelNum - 1].addBox("box", -((calibre + 16) / 2) / this.trackScale, -8.0F, -8.0F, 16, 16, 16);
     this.body = new ModelRenderer(this, "body");
     this.body.setRotationPoint(0.0F, 24.0F - (l / 4), 0.0F);
     setRotation(this.body, 0.0F, 0.0F, 0.0F);
     this.body.mirror = true;
     this.body.addBox("stand", -(calibre + 16) / 2.0F, (-calibre / 2 - calibre / 4), (-(calibre + 6) / 2), calibre + 16, calibre / 4, calibre + 16);
     this.body.addBox("chassis", (-(calibre + 16) / 2), (-calibre / 2), (-(l - 2) / 2), calibre + 16, calibre / 2 + l / 4 - (int)(32.0F * this.trackScale), l - 2);
     this.body.addBox("engine", (-calibre / 2), (-calibre / 2 - calibre / 8), (-calibre - 3), calibre, calibre / 8, calibre / 2);
     this.body.addBox("bottom", (-(calibre + 16) / 2 + (int)(32.0F * this.trackScale) / 2 + 1), (l / 4 - (int)(32.0F * this.trackScale)), (-(l - 4) / 2), calibre + 16 - (int)(32.0F * this.trackScale) - 2, l / 16, l - 4);
     if (design != 0) {
       this.body.addBox("schurzenL", (-(calibre + 16) / 2 - l / 8 + l / 8 - 3), (-l / 8), (-(l - 4 - 4) / 2), 1, l / 4, l - 4 - 4);
       this.body.addBox("schurzenR", ((calibre + 16) / 2 + l / 8 - l / 8 + 2), (-l / 8), (-(l - 4 - 4) / 2), 1, l / 4, l - 4 - 4);
     } 
     this.carriage = new ModelRenderer(this, "carriage");
     this.carriage.setRotationPoint(0.0F, (24 - l / 4 + calibre / 2 + calibre / 4 + calibre / 4 + calibre / 2), 0.0F);
     setRotation(this.carriage, 0.0F, 0.0F, 0.0F);
     this.carriage.mirror = true;
     if (hasTurret) {
       this.carriage.addBox("carriageBox", (-(calibre + 6) / 2), (-calibre * 3 / 4), (-(calibre + 6) / 2), calibre + 6, calibre * 3 / 2, calibre + 6);
       this.carriage.addBox("cupola", (-calibre / 4), -calibre, (-calibre / 4), calibre / 2, calibre / 4, calibre / 2);
     } else {
       this.carriage.addBox("Lcarriage", (-(calibre + 6) / 2), (calibre / 8 * 9), (-(calibre + 6) / 2), calibre + 6, calibre / 4, calibre + 6);
       this.carriage.addBox("UcarriageL", ((calibre + 6) / 2 - 2), (-calibre / 8), (-calibre * 3 / 8), 2, calibre * 5 / 4, calibre * 3 / 4);
       this.carriage.addBox("UcarriageR", (-(calibre + 6) / 2), (-calibre / 8), (-calibre * 3 / 8), 2, calibre * 5 / 4, calibre * 3 / 4);
     } 
     this.cannon = new ModelRenderer(this, "cannon");
     this.cannon.setRotationPoint(0.0F, (24 - l / 4 + calibre / 2 + calibre / 4 + calibre / 4 + calibre / 2), 0.0F);
     setRotation(this.cannon, 0.0F, 0.0F, 0.0F);
     this.cannon.mirror = true;
     this.cannon.addBox("barrelB", -(calibre / 2 - 1), (calibre / 2 - 1), (-barrel + calibre * 3 / 8 + posOfBarrel), calibre - 2, 2, barrel);
     this.cannon.addBox("barrelL", (calibre / 2 - 1), -(calibre / 2 - 1), (-barrel + calibre * 3 / 8 + posOfBarrel), 2, calibre - 2, barrel);
     this.cannon.addBox("barrelR", (-calibre / 2 - 1), -(calibre / 2 - 1), (-barrel + calibre * 3 / 8 + posOfBarrel), 2, calibre - 2, barrel);
     this.cannon.addBox("barrelT", -(calibre / 2 - 1), (-calibre / 2 - 1), (-barrel + calibre * 3 / 8 + posOfBarrel), calibre - 2, 2, barrel);
     this.cannon.addBox("breech", -(calibre / 2 - 1), (-calibre / 2 + 1), (calibre * 3 / 8 + posOfBarrel - 2), calibre - 2, calibre - 2, 2);
   }
   
   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
     super.render(entity, f, f1, f2, f3, f4, f5);
     GlStateManager.pushMatrix();
     GlStateManager.rotate(entity.rotationYaw + 180.0F, 0.0F, 1.0F, 0.0F);
     if (!entity.getPassengers().isEmpty() && entity.getPassengers().get(0) == (Minecraft.getMinecraft()).player && 
       (Minecraft.getMinecraft()).gameSettings.thirdPersonView == 0) {
       this.body.render(f5);
       this.renderer.bindTexture(RenderCannon.textureWheel);
       for (int i = 0; i < this.wheelNum; i++) {
         this.wheelsL[i].render(f5 * this.trackScale);
         this.wheelsR[i].render(f5 * this.trackScale);
       } 
       this.renderer.bindTexture(RenderCannon.textureCrawler);
       renderCrawler(this.crawlerAnimeL, this.crawlerAnimeR);
     } else {
       this.cannon.render(f5);
       this.carriage.render(f5);
       this.body.render(f5);
       this.renderer.bindTexture(RenderCannon.textureWheel);
       for (int i = 0; i < this.wheelNum; i++) {
         this.wheelsL[i].render(f5 * this.trackScale);
         this.wheelsR[i].render(f5 * this.trackScale);
       } 
       this.renderer.bindTexture(RenderCannon.textureCrawler);
       renderCrawler(this.crawlerAnimeL, this.crawlerAnimeR);
     } 
     GlStateManager.popMatrix();
   }
   
   public void renderCrawler(double animeL, double animeR) {
     Tessellator tessellator = Tessellator.getInstance();
     BufferBuilder vertexbuffer = tessellator.getBuffer();
     vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
     double xM = this.vehicleWidthx16 / 32.0D;
     double xm = -xM;
     double trackWidth = this.trackScale;
     double zBM = this.vehicleLengthx16 / 32.0D - (this.trackScale * 2.0F);
     double zBm = -zBM;
     double zTM = this.vehicleLengthx16 / 32.0D;
     double zTm = -zTM;
     double yT = 1.5D - (this.trackScale * 2.0F);
     double yB = 1.5D;
     double texU = zTM / 2.0D;
     double texV = trackWidth;
     vertexbuffer.pos(xM, yT, zTm).tex(animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yT, zTM).tex(texU + animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTM).tex(texU + animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTm).tex(animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTm).tex(animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTM).tex(texU + animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yT, zTM).tex(texU + animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yT, zTm).tex(animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTm).tex(animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTM).tex(texU + animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTM).tex(texU + animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTm).tex(animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTm).tex(animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTM).tex(texU + animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTM).tex(texU + animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTm).tex(animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
      texU = zBM / 2.0D;
      texV = trackWidth;
     vertexbuffer.pos(xM, yB, zBm).tex(-animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yB, zBM).tex(texU - animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBM).tex(texU - animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBm).tex(-animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBm).tex(-animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBM).tex(texU - animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yB, zBM).tex(texU - animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yB, zBm).tex(-animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBm).tex(-animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBM).tex(texU - animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBM).tex(texU - animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBm).tex(-animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBm).tex(-animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBM).tex(texU - animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBM).tex(texU - animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBm).tex(-animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
      texU = (yB - yT) / 8.0D;
      texV = trackWidth;
     vertexbuffer.pos(xM, yT, zTM).tex(animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTM).tex(texU + animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTM).tex(texU + animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTM).tex(animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTM).tex(animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTM).tex(texU + animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTM).tex(texU + animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yT, zTM).tex(animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTM).tex(animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTM).tex(texU + animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTM).tex(texU + animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTM).tex(animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTM).tex(animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTM).tex(texU + animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTM).tex(texU + animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTM).tex(animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
      texU = (yB - yT) / 8.0D;
      texV = trackWidth;
     vertexbuffer.pos(xM, yT, zTm).tex(-animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTm).tex(texU - animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTm).tex(texU - animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTm).tex(-animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yT, zTm).tex(-animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTm).tex(texU - animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTm).tex(texU - animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yT, zTm).tex(-animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTm).tex(-animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTm).tex(texU - animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTm).tex(texU - animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTm).tex(-animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yT, zTm).tex(-animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTm).tex(texU - animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTm).tex(texU - animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yT, zTm).tex(-animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
      texU = (MathHelper.sqrt((zTM - zBM) * (zTM - zBM) + (yB - yT) * (yB - yT) / 4.0D) / 4.0F);
      texV = trackWidth;
     vertexbuffer.pos(xM, yB, zBM).tex(-animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTM).tex(texU - animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTM).tex(texU - animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBM).tex(-animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBM).tex(-animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTM).tex(texU - animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTM).tex(texU - animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yB, zBM).tex(-animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBM).tex(-animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTM).tex(texU - animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTM).tex(texU - animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBM).tex(-animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBM).tex(-animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTM).tex(texU - animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTM).tex(texU - animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBM).tex(-animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
      texU = (MathHelper.sqrt((zTM - zBM) * (zTM - zBM) + (yB - yT) * (yB - yT) / 4.0D) / 4.0F);
      texV = trackWidth;
     vertexbuffer.pos(xM, yB, zBm).tex(animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTm).tex(texU + animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTm).tex(texU + animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBm).tex(animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, yB, zBm).tex(animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM - trackWidth, (yT + yB) / 2.0D, zTm).tex(texU + animeL, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, (yT + yB) / 2.0D, zTm).tex(texU + animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xM, yB, zBm).tex(animeL, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBm).tex(animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTm).tex(texU + animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTm).tex(texU + animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBm).tex(animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, yB, zBm).tex(animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm + trackWidth, (yT + yB) / 2.0D, zTm).tex(texU + animeR, texV).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, (yT + yB) / 2.0D, zTm).tex(texU + animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     vertexbuffer.pos(xm, yB, zBm).tex(animeR, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
     tessellator.draw();
   }
   
   private void setRotation(ModelRenderer model, float x, float y, float z) {
     model.rotateAngleX = x;
     model.rotateAngleY = y;
     model.rotateAngleZ = z;
   }
   
   public void setRotationAngles(float yaw, float yawHead, float pitch, EntityCannon entityIn, float partialTicks) {
     this.cannon.rotateAngleX = pitch / 180.0F * 3.1415927F;
     this.cannon.rotateAngleY = yawHead / 180.0F * 3.1415927F;
     this.carriage.rotateAngleY = yawHead / 180.0F * 3.1415927F;
     if (!entityIn.getPassengers().isEmpty()) {
       Entity riddenByEntity = (Entity)entityIn.getPassengers().get(0);
       if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase && (entityIn.engine == 0 || entityIn.fuelLeft > 0)) {
         EntityLivingBase entitylivingbase = (EntityLivingBase)riddenByEntity;
         double turnAmountL = 0.0D;
         double turnAmountR = 0.0D;
         float pt = partialTicks;
         if (partialTicks < this.prevPartialTicks)
           pt++; 
         if (entitylivingbase.moveForward > 0.001D) {
           turnAmountL = entityIn.speedMultiplier / this.trackScale * (pt - this.prevPartialTicks);
           turnAmountR = turnAmountL;
         } else if (entitylivingbase.moveForward < -0.001D) {
           turnAmountL = entityIn.speedMultiplier / this.trackScale * (pt - this.prevPartialTicks) * -0.5D;
           turnAmountR = turnAmountL;
         } 
         if (entitylivingbase.moveStrafing > 0.001D) {
           double v = (entityIn.turningSpeed / 180.0F) * Math.PI * entityIn.vehicleWidth / 2.0D / this.trackScale * (pt - this.prevPartialTicks);
           turnAmountL -= v;
           turnAmountR += v;
         } else if (entitylivingbase.moveStrafing < -0.001D) {
           double v = (entityIn.turningSpeed / 180.0F) * Math.PI * entityIn.vehicleWidth / 2.0D / this.trackScale * (pt - this.prevPartialTicks);
           turnAmountL += v;
           turnAmountR -= v;
         } 
         if (!(entitylivingbase instanceof net.minecraft.entity.player.EntityPlayer)) {
           float rotationYaw = entityIn.rotationYaw;
           if (entityIn.rotationYaw - entitylivingbase.rotationYaw > 180.0F) {
             rotationYaw -= 360.0F;
           } else if (entityIn.rotationYaw - entitylivingbase.rotationYaw < -180.0F) {
             rotationYaw += 360.0F;
           } 
           if (rotationYaw - entitylivingbase.rotationYaw > entityIn.turningSpeed) {
             double v = (entityIn.turningSpeed / 180.0F) * Math.PI * entityIn.vehicleWidth / 2.0D / this.trackScale * (pt - this.prevPartialTicks);
             turnAmountL -= v;
             turnAmountR += v;
           } else if (rotationYaw - entitylivingbase.rotationYaw < -entityIn.turningSpeed) {
             double v = (entityIn.turningSpeed / 180.0F) * Math.PI * entityIn.vehicleWidth / 2.0D / this.trackScale * (pt - this.prevPartialTicks);
             turnAmountL -= v;
             turnAmountR += v;
           } else {
             double v = ((rotationYaw - entitylivingbase.rotationYaw) / 180.0F) * Math.PI * entityIn.vehicleWidth / 2.0D / this.trackScale * (pt - this.prevPartialTicks);
             turnAmountL -= v;
             turnAmountR += v;
           } 
         } 
         for (int i = 0; i < this.wheelNum; i++) {
           (this.wheelsL[i]).rotateAngleX = (float)((this.wheelsL[i]).rotateAngleX + turnAmountL);
           (this.wheelsR[i]).rotateAngleX = (float)((this.wheelsR[i]).rotateAngleX + turnAmountR);
         } 
         this.crawlerAnimeL += turnAmountL / 8.0D;
         this.crawlerAnimeR += turnAmountR / 8.0D;
       } 
     } 
     this.prevPartialTicks = partialTicks;
   }
 }


/* Location:              C:\Users\adamh\Downloads\CustomizableArtilleryMODv1_3_0forMC1.10.2-deobf.jar!\cannonmod\entity\ModelCannon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */