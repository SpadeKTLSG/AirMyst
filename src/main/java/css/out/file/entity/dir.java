package css.out.file.entity;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.enums.FileDirTYPE.DIR;

@Slf4j
@Data
@Builder
public class dir {

    /**
     * 文件控制块
     */
    public FCB fcb;


    /**
     * 文件内容
     * <p>锁定为"" - 维持通用性</p>
     */
    public final String content = "";


    /**
     * 默认取消打印空内容
     *
     * @return 文件夹信息
     */
    @Override
    public String toString() {
        return "dir{" +
                fcb +
                '}';
    }

    /**
     * 文件夹FCB式直接构建
     *
     * @param fcb 对应绑定的文件控制块
     */
    public dir(FCB fcb) {
        this.fcb = fcb;
    }

    /**
     * 文件夹DTO构造
     *
     * @param pathName   目录名+文件名
     * @param extendName 扩展名
     */
    public dir(String pathName, String extendName) {
        this.fcb = new FCB(pathName, extendName, DIR);

    }


    /**
     * 文件夹基本指定构造
     *
     * @param pathName 目录名+文件名
     */
    public dir(String pathName) {
        this.fcb = new FCB(pathName, DIR);

    }

    /**
     * 文件夹指定块号构造
     *
     * @param pathName   目录名+文件名
     * @param startBlock 起始盘块号
     */
    public dir(String pathName, int startBlock) {
        this.fcb = new FCB(pathName, startBlock, DIR);
    }


    /**
     * 文件夹临时生成(不挂载)
     * <p>默认走/tmp目录</p>
     */
    public dir() {
        this.fcb = new FCB(DIR);
    }


}
