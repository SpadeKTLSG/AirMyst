package css.out.file.enums;

import lombok.Getter;

/**
 * 用户命令接口命令列表
 * create   创建文件
 * copy     复制文件
 * delete   删除文件
 * move     移动文件
 * cat      查看文件内容
 * chtype   改变文件类型
 * mkdir    创建目录
 * chdir    改变目录
 * deldir   删除目录
 */
@Getter
public enum CMD_ORDER {

    create("create", "创建文件"),
    copy("copy", "复制文件"),
    delete("delete", "删除文件"),
    move("move", "移动文件"),
    cat("cat", "查看文件内容"),
    chtype("chtype", "改变文件类型"),
    mkdir("mkdir", "创建目录"),
    chdir("chdir", "改变目录"),
    deldir("deldir", "删除目录");


    public final String name;
    public final String desc;

    CMD_ORDER(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
