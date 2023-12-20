package top.mcos.database.enums;

import lombok.Getter;

@Getter
public enum GiftTypeEnum {
    CHRISTMAS_GIFT(1, "圣诞礼物"),
    CHINESE_NEW_YEAR_GIFT(2, "春节礼物"),
    SNOWBALL_ITEM(11, "圣诞雪球"),
    ;
    private Integer index;
    private String name;

    GiftTypeEnum(Integer index, String name) {
        this.index = index;
        this.name = name;
    }
}
