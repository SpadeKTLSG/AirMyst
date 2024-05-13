package css.out.device;

import css.core.process.ProcessA;

/**
 * 进程 - 设备使用
 */
public class ProcessDeviceUse {


    public ProcessA process;
    public long longTime;

    public ProcessDeviceUse(ProcessA process, long l) {
        this.process = process;
        longTime = l;
    }
}
