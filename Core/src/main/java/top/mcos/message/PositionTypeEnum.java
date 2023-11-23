package top.mcos.message;

/**
 * 消息类型
 */
public enum PositionTypeEnum {
    ACTIONBAR("actionbar"),
    TITLE("title"),
    ;
    private String name;

    PositionTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
