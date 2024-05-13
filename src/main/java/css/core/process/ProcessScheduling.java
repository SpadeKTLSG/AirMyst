package css.core.process;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessScheduling {
    /*ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-config.xml");
    DeviceManagement deviceManagement = (DeviceManagement) context.getBean("deviceManagement");*/

    /**
     * 进程链表
     */
    public static ConcurrentHashMap<Integer, ProcessA> linkedList = new ConcurrentHashMap<>();

    /**
     * 就绪队列
     */
    volatile public static ArrayBlockingQueue<ProcessA> readyQueues = new ArrayBlockingQueue<ProcessA>(10);

    /**
     * 阻塞队列
     */
    volatile public static ArrayBlockingQueue<ProcessA> blocking = new ArrayBlockingQueue<ProcessA>(10);

    /**
     * 正在运行的进程
     */
    volatile public static ProcessA runing = null;

    @Transactional
    public void getReadyToRun() throws InterruptedException {
        ProcessA first = readyQueues.take();
        runing = first;
        first.pcb.state = 1;
        synchronized (first) {
            first.notifyAll();
        }
    }

    @Transactional
    public void getRunToReady() throws InterruptedException {
        runing.pcb.state = 0;
        readyQueues.add(runing);
        runing.wait();
    }

    @Transactional
    public void changeProcess(String fileName) throws IOException {
        ProcessA process = new ProcessA(fileName);
        readyQueues.add(process);
        process.start();
    }

    @Transactional
    public void changeBlocking(ProcessA process) throws InterruptedException {
        runing = null;
        process.pcb.state = 2;
        process.wait();
    }

    @Transactional
    public void changeReady(ProcessA process) {
        process.pcb.state = 0;
    }

    public void use() {
        Thread thread = new Thread(() -> {
            while (true) {
                if (runing == null) {
                    try {
                        getReadyToRun();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

/*    public void commandExecution(String order) {
        String[] s = order.split(" ");
        switch (s[0]) {
            case "$add" -> {
                deviceManagement.devices.put(s[1], new Device(s[1]));
            }
            case "$remove" -> {
                deviceManagement.devices.remove(s[1]);
            }
            case "stop" -> {
                linkedList.get(s[1]).stop = true;
            }
            default -> {
                toFrontApiList.getFrontRequest(order);
            }
        }
    }*/

}
