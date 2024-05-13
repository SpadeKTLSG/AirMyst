package css.out.file.entiset;

import css.out.file.entity.TREE;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 单例实现文件系统
 */
@Slf4j
@Data
public class FileSyS {

    private volatile static FileSyS instance;

    /**
     * 文件系统树形结构
     * <p>没有存在磁盘里</p>
     */
    public TREE tree;

    /**
     * 路径管理器
     * <p>没有存在磁盘里</p>
     */
    public Map<Integer, String> pathManager;

    /**
     * 扩展名管理器
     * <p>没有存在磁盘里</p>
     */
    public Map<Integer, String> extendManager;

    /**
     * 单例实现
     */
    private FileSyS() {
    }

    public static FileSyS getInstance(){
        if(instance == null){
            instance = new FileSyS();
        }
        return instance;
    }


    @Override
    public String toString() {
        return "FileSyS{" +
                "tree=" + tree +
                "\npathManager=" + pathManager +
                "\nextendManager=" + extendManager +
                "}\n";
    }
}
