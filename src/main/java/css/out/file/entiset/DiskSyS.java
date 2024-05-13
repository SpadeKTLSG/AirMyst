package css.out.file.entiset;

import css.out.file.entity.disk;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 单例实现磁盘系统
 */
@Slf4j
@Data
public class DiskSyS {

    private volatile static DiskSyS instance;


    /**
     * 磁盘
     */
    public disk disk;

    /**
     * 单例实现
     */
    private DiskSyS() {
    }

    public static DiskSyS getInstance(){
        if(instance == null){
            instance = new DiskSyS();
        }
        return instance;
    }

    @Override
    public String toString() {
        return "DiskSyS\n{" +
                "disk=" + disk +
                '}';
    }
}
