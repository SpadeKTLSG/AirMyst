package css.out.device;


import css.core.process.Pcb;
import css.core.process.ProcessScheduling;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ArrayBlockingQueue;


public class Device {

    //? 问题 FIXME
    ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-config.xml");
    ProcessScheduling processScheduling = (ProcessScheduling) context.getBean("processScheduling");
    DeviceManagement deviceManagement = (DeviceManagement) context.getBean("deviceManagement");

    public String name;
    public Pcb nowProcessPcb = null;
    public ArrayBlockingQueue<ProcessDeviceUse> arrayBlockingQueue = new ArrayBlockingQueue<ProcessDeviceUse>(10);

    public Device(String name) {
        this.name = name;
        deviceManagement.devices.put(name, this);
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    ProcessDeviceUse remove = arrayBlockingQueue.take();
                    nowProcessPcb = remove.process.pcb;
                    System.out.println("进程" + nowProcessPcb.pcbId + "再使用" + this.name);
                    synchronized (this) {
                        this.wait(remove.longTime);
                    }
                    ProcessScheduling.blocking.remove(remove.process);
                    ProcessScheduling.readyQueues.add(remove.process);
                    remove.process.pcb.state = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                nowProcessPcb = null;
            }
        }).start();
    }
}
