package top.mcos.message;

/**
 * 消息类型
 */
public enum PositionTypeEnum {
    actionbar("滚动消息"),
    title("标题消息"),
    ;
    private String desc;

    PositionTypeEnum(String name) {
        this.desc = name;
    }

    public String getDesc() {
        return desc;
    }
}
