package css.out.file.entiset;

import lombok.extern.slf4j.Slf4j;

import static css.out.file.entity.disk.initialDisk;
import static css.out.file.handleS.HandleFS.*;

/**
 * 单例模式工厂
 * Single Factory
 */
@Slf4j
public abstract class SFA {

    /**
     * DiskSyS磁盘系统: 唯一
     */
    private static final DiskSyS DISK_SYS = DiskSyS.getInstance();

    /**
     * FileSyS文件系统: 唯一
     */
    private static final FileSyS FILE_SYS = FileSyS.getInstance();


    /**
     * private构造
     * 保证外部无法实例化
     */
    private SFA() {
    }


    /**
     * 获取磁盘系统
     */
    public static DiskSyS initialDiskSyS() {
        DISK_SYS.disk = initialDisk();
        return DISK_SYS;
    }


    /**
     * 获取文件系统
     */
    public static FileSyS initialFileSys() {
        FILE_SYS.tree = initialTR();
        FILE_SYS.pathManager = initialPM();
        FILE_SYS.extendManager = initialEM();
        return FILE_SYS;
    }


}
