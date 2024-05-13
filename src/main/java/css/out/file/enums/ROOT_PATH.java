package css.out.file.enums;

import lombok.Getter;

/**
 * 根目录/下的子目录名
 * <p>|  home 用户子目录</p>
 * <p>|  app 应用程序目录</p>
 * <p>|  tmp 临时可变目录</p>
 * <p>|  conf 配置文件目录</p>
 * <p>|  mnt 设备挂载目录</p>
 * <p>|  bin 可执行命令目录</p>
 * <p>|  lib 系统库文件以及系统资料目录</p>
 * <p>|  boot 系统内核程序目录</p>
 */
@Getter
public enum ROOT_PATH {

    home("home", "用户子目录"),
    app("app", "应用程序目录"),
    tmp("tmp", "临时可变目录"),
    conf("conf", "配置文件目录"),
    mnt("mnt", "设备挂载目录"),
    bin("bin", "可执行命令目录"),
    lib("lib", "系统库文件以及系统资料目录"),
    boot("boot", "系统内核程序目录");

    public final String name;
    public final String desc;

    ROOT_PATH(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }


}
