package top.mcos.nms.provideds.R1_20_R2;

import top.mcos.nms.spi.NmsBuilder;
import top.mcos.nms.spi.NmsProvider;

public class R1_20_R2_Builder implements NmsBuilder {
	@Override
	public boolean checked(String version) {
		return version.equals("v1_20_R2");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_20_R2();
	}
}
