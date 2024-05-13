package css.core.process;


import java.util.HashMap;


public class Pcb {

    Pcb() {
        this.pcbId = Constants.PID.getAndIncrement();
        this.state = 0;
        register = new HashMap<String, Integer>();
        this.lines = null;
    }

    /**
     * 进程标识符
     */
    public int pcbId;

    /**
     * 进程状态：
     * 0：就绪态
     * 1：运行态
     * 2：阻塞态
     * 3：终止
     */
    public volatile int state;

    /**
     * 寄存器
     */
    public HashMap<String, Integer> register;

    /**
     * 运行哪条语句
     */
    public String lines;

    /**
     * 阻塞原因
     */
    public String blockageCause;


}
