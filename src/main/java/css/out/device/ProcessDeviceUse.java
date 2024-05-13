package css.out.device;

import css.core.process.ProcessA;

/**
 * 进程 - 设备使用
 */
public class ProcessDeviceUse {


    /**
     * 对应处理进程
     */
    public ProcessA process;

    /**
     * 需要使用时间
     */
    public long longTime;


    public ProcessDeviceUse(ProcessA process, long l) {
        this.process = process;
        longTime = l;
    }
}
