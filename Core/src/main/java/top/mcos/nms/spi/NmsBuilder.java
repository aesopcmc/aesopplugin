package top.mcos.nms.spi;

public interface NmsBuilder {
	
	boolean checked(String version);
	NmsProvider build();
	
}
