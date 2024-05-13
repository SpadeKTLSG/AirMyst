package css.out.file.enums;

import lombok.Getter;

/**
 * 文件控制块字段枚举
 */
@Getter
public enum FCB_FIELD {
    PATH_NAME("pathName", "路径"),
    START_BLOCK("startBlock", "起始块"),
    EXTEND_NAME("extendName", "扩展名"),
    TYPE_FLAG("typeFlag", "类型"),
    FILE_LENGTH("fileLength", "长度");

    public final String name;
    public final String desc;

    FCB_FIELD(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

}
