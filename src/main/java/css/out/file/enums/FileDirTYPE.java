package css.out.file.enums;

import lombok.Getter;

/**
 * 文件类型枚举
 * <p>FILE: 文件</p>
 * <p>DIR: 目录</p>
 */
@Getter
public enum FileDirTYPE {

    FILE("FILE", "文件"), DIR("DIR", "文件夹");

    public final String name;
    public final String desc;

    FileDirTYPE(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }


}
