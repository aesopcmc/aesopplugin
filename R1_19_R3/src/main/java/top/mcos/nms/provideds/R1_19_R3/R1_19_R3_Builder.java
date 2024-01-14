package top.mcos.nms.provideds.R1_19_R3;

import top.mcos.nms.spi.NmsBuilder;
import top.mcos.nms.spi.NmsProvider;

public class R1_19_R3_Builder implements NmsBuilder {
	@Override
	public boolean checked(String version) {
		return version.equals("v1_19_R3");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_19_R3();
	}
}
