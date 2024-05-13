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

    /**
     * 进程调度: 从就绪队列中取出一个进程运行
     */
    @Transactional
    public void getReadyToRun() throws InterruptedException {

        ProcessA first = readyQueues.take(); //从就绪队列中取出一个进程
        runing = first; //标记为正在运行的进程
        first.pcb.state = 1; //设置进程状态为运行态
        synchronized (first) {
            first.notifyAll(); //唤醒进程
        }
    }

    /**
     * 进程调度: 从运行态转为就绪态
     */
    @Transactional
    public void getRunToReady() throws InterruptedException {
        runing.pcb.state = 0; //设置进程状态为就绪态
        readyQueues.add(runing); //加入就绪队列
        runing.wait(); //等待
    }

    //废弃
    @Transactional
    public void changeProcess(String fileName) throws IOException {
        ProcessA process = new ProcessA(fileName);
        readyQueues.add(process);
        process.start();
    }

    //废弃
    @Transactional
    public void changeBlocking(ProcessA process) throws InterruptedException {
        runing = null;
        process.pcb.state = 2;
        process.wait();
    }

    //废弃
    @Transactional
    public void changeReady(ProcessA process) {
        process.pcb.state = 0;
    }

    //测试操作
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
