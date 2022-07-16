package cannonmod.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
 /**a130*/
public class MessageCannonParticle implements IMessage {
 
    public double x,y,z;
    public double mx,my,mz;
    public int num;
    public double noise,movNoise;
    
    public byte noiseType,movNoiseType;
    public int type;
    public int[] params;
 
    public MessageCannonParticle(){}
    public MessageCannonParticle(double x,double y,double z,int type){
    	this(x,y,z,0,0,0,1,0,0,(byte)0,(byte)0,type);
    }
    
    /**@param noiseType 0:No noise;1:Gaussian;2:Double
     * @param movnoiseType 0:No noise;1:Gaussian;2:Double*/
    public MessageCannonParticle(double x,double y,double z,double mx,double my,double mz,int num,double noise,double movNoise,
    		byte noiseType,byte movNoiseType,int type) {
        this(x, y, z, mx, my, mz, num, noise, movNoise, noiseType, movNoiseType, type,new int[0]);
    }
    /**@param noiseType 0:No noise;1:Gaussian;2:Double
     * @param movnoiseType 0:No noise;1:Gaussian;2:Double*/
    public MessageCannonParticle(double x,double y,double z,double mx,double my,double mz,int num,double noise,double movNoise,
    		byte noiseType,byte movNoiseType,int type,int[] params) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.mx=mx;
        this.my=my;
        this.mz=mz;
        this.num=num;
        this.noise=noise;
        this.movNoise=movNoise;
        this.noiseType=noiseType;
        this.movNoiseType=movNoiseType;
        this.type=type;
        this.params=params;
    }
 
    @Override//IMessage
    public void fromBytes(ByteBuf buf) {
    	this.x=buf.readDouble();
    	this.y=buf.readDouble();
    	this.z=buf.readDouble();
    	this.mx=buf.readDouble();
    	this.my=buf.readDouble();
    	this.mz=buf.readDouble();
    	this.num=buf.readInt();
    	this.noise=buf.readDouble();
    	this.movNoise=buf.readDouble();
    	this.noiseType=buf.readByte();
    	this.movNoiseType=buf.readByte();
        this.type=buf.readInt();
        List<Integer> list=new ArrayList<Integer>();
        while(buf.isReadable()){
        	list.add(new Integer(buf.readInt()));
        }
        this.params=new int[list.size()];
        for(int i=0;i<list.size();i++){
        	this.params[i]=list.get(i);
        }
    }
 
    @Override//IMessage
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeDouble(this.mx);
        buf.writeDouble(this.my);
        buf.writeDouble(this.mz);
        buf.writeInt(this.num);
        buf.writeDouble(this.noise);
        buf.writeDouble(this.movNoise);
        buf.writeByte(this.noiseType);
        buf.writeByte(this.movNoiseType);
        buf.writeInt(this.type);
        for(int i:this.params){
        	buf.writeInt(i);
        }
    }
}