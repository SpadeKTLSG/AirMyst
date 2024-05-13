package css.out.file.api;

import lombok.extern.slf4j.Slf4j;

import static css.out.file.entiset.GF.DISK_SIZE;
import static css.out.file.handleS.monitor.getDiskUsageAmount;

/**
 * 系统监控API
 */
@Slf4j
public class MonitorApiList {

    /**
     * 获取全局磁盘使用量
     *
     * @return 全局磁盘使用量(百分数
     */
    public static Double diskUsageAmount_All() {
        double or = getDiskUsageAmount() * 100;
        //保留两位
        or = (double) Math.round(or * 100) / 100;
        log.debug("全局磁盘使用量 = {} %", or);
        return or;
    }

    /**
     * 获取系统磁盘使用量
     *
     * @return 系统磁盘使用量(百分数
     */
    public static Double diskUsageAmount_SyS() {
        double sor = (3.0 / (double) DISK_SIZE) * 100; //3.0 == FAT + DefaultDir
        //保留两位
        sor = (double) Math.round(sor * 100) / 100;
        log.debug("系统磁盘使用量 = {} %", sor);
        return sor;
    }
}
