package css.out.file.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.enums.FileDirTYPE.FILE;

/**
 * 文件实体

 */
@Slf4j
@Data
@Builder
public class file {

    /**
     * 文件控制块
     */
    public FCB fcb;


    /**
     * 文件内容
     */
    public String content;


    @Override
    public String toString() {
        return "file{" +
                fcb +
                ", content='" + content + '\'' +
                '}';
    }


    /**
     * 文件FCB式直接构建
     *
     * @param fcb     对应绑定的文件控制块
     * @param content 文件内容
     */
    public file(FCB fcb, String content) {
        this.fcb = fcb;
        this.content = content;
    }


    /**
     * 文件DTO构造
     *
     * @param pathName   目录名+文件名
     * @param extendName 扩展名
     * @param content    文件内容
     */
    public file(String pathName, String extendName, String content) {
        this.fcb = new FCB(pathName, extendName, FILE);
        this.content = content;
    }


    /**
     * 文件基本指定构造
     *
     * @param pathName 目录名+文件名
     * @param content  文件内容
     */
    public file(String pathName, String content) {
        this.fcb = new FCB(pathName, FILE);
        this.content = content;
    }


    /**
     * 有内容文件临时生成(不挂载)
     *
     * @param content 文件内容
     */
    public file(String content) {
        this.fcb = new FCB(FILE);
        this.content = content;
    }


    /**
     * 无内容文件临时生成(不挂载)
     * <p>默认走/tmp目录</p>
     */
    public file() {
        this.fcb = new FCB(FILE);
        this.content = "";
    }

}

