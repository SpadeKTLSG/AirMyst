package css.out.device;


import css.core.process.Pcb;
import css.core.process.ProcessScheduling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 设备类
 */
@Slf4j
public class Device {

    ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-config.xml");
    ProcessScheduling processScheduling = (ProcessScheduling) context.getBean("processScheduling");
    DeviceManagement deviceManagement = (DeviceManagement) context.getBean("deviceManagement");

    /**
     * 设备名
     */
    public String name;

    /**
     * 当前使用设备的进程
     */
    public Pcb nowProcessPcb = null;

    /**
     * 设备阻塞队列
     */
    public ArrayBlockingQueue<ProcessDeviceUse> arrayBlockingQueue = new ArrayBlockingQueue<ProcessDeviceUse>(10);

    public Device(String name) {
        this.name = name;
        deviceManagement.devices.put(name, this);
    }

    /**
     * 开始使用设备
     */
    public void start() {

        //内部创建新线程, 用于处理设备的使用
        new Thread(() -> {
            while (true) {

                try {
                    ProcessDeviceUse remove = arrayBlockingQueue.take(); //获取一个进程
                    nowProcessPcb = remove.process.pcb;

                    log.debug("进程" + nowProcessPcb.pcbId + "在使用" + this.name);

                    synchronized (this) {
                        this.wait(remove.longTime); //模拟设备使用时间
                    }

                    //使用完毕, 将进程从阻塞队列中移除, 并加入就绪队列
                    ProcessScheduling.blocking.remove(remove.process);
                    ProcessScheduling.readyQueues.add(remove.process);
                    remove.process.pcb.state = 0;

                } catch (InterruptedException e) {
                    log.error("设备使用异常", e);
                }
                nowProcessPcb = null;
            }
        }).start();
    }
}
