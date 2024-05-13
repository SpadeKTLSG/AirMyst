package css.core.process;


import css.core.memory.MemoryManager;
import css.out.device.DeviceManagement;
import css.out.device.ProcessDeviceUse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * 进程实体
 */
@Slf4j
public class ProcessA extends Thread {

    //注入进程调度器
    ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-config.xml");
    ProcessScheduling processScheduling = (ProcessScheduling) context.getBean("processScheduling");
    //注入设备管理器
    DeviceManagement deviceManagement = (DeviceManagement) context.getBean("deviceManagement");

    /**
     * 是否停止
     */
    volatile public boolean stop = false;

    /**
     * 进程控制块
     */
    public Pcb pcb;

    /**
     * 文件读取
     */
    public FileReader file;
    public BufferedReader bufferedReader;


    /**
     * 进程构造函数
     * @param fileName  利用文件
     */
    public ProcessA(String fileName) throws IOException {
        pcb = new Pcb();
        file = new FileReader(fileName);
        bufferedReader = new BufferedReader(file);
    }

    /**
     * 进程运行
     */
    @Override
    public void run() {

        try {

            log.debug("{}开始运行",processScheduling);
            synchronized (this) {
                ProcessScheduling.readyQueues.add(this); //登记就绪队列
                ProcessScheduling.linkedList.put(this.pcb.pcbId, this); //登记进程链表
                this.wait();
            }

            while (!stop) { //持续占用CPU
                CPU(); //?
            }

            //释放CPU, 清理内存
            MemoryManager.releaseMemory(pcb.pcbId);
            MemoryManager.displayMemory();

        } catch (IOException | InterruptedException e) {
            log.error("进程运行异常",e);
        }
    }

    /**
     *
     */
    @Transactional
    public void wirth() {
        Iterator<Map.Entry<String, Integer>> iterator = pcb.register.entrySet().iterator(); //获取寄存器迭代器

/*        FileWriter fileWriter = new FileWriter("src/main/resources/common/file/out.txt"); //File读取
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> next = iterator.next();
            bufferedWriter.write(next.getKey() + "=" + next.getValue() + "\t\n"); //写入文件
        }
        bufferedWriter.close();
        fileWriter.close();
        */

        try (FileWriter fileWriter = new FileWriter("src/main/resources/common/file/out.txt");//使用try -catch 语句优化并自动关闭流
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            while (iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                bufferedWriter.write(next.getKey() + "=" + next.getValue() + "\t\n"); //写入文件
            }

        } catch (IOException e) {
            log.error("写入文件异常",e);
        }
    }


    /**
     * CPU 运行方法
     */
    @Transactional
    public void CPU() throws IOException, InterruptedException {


        synchronized (this) { //同步

            if (pcb.state == 1) { //运行态

                String s = bufferedReader.readLine();
                log.debug("进程{}运行{}", pcb.pcbId, s);

                //分配内存
                MemoryManager.allocateMemory(pcb.pcbId, s);
                MemoryManager.displayMemory();
                pcb.lines = s;

                //进行指令处理
                if (s == null) { //Supress warning
                    pcb.state = 3; //终止态
                    ProcessScheduling.runing = null;
                    processScheduling.getReadyToRun();
                    this.stop = true;

                } else if (s.contains("=")) { //赋值语句
                    String[] split = s.split("=");
                    pcb.register.put(split[0], Integer.valueOf(split[1]));

                } else if (s.contains("++")) { //自增语句
                    String[] split = s.split("\\+\\+");
                    Integer integer = pcb.register.get(split[0]); //取出寄存器的值
                    pcb.register.put(split[0], integer + 1);

                } else if (s.contains("--")) { //自减语句
                    String[] split = s.split("--");
                    Integer integer = pcb.register.get(split[0]);
                    pcb.register.put(split[0], integer - 1);

                } else if (s.startsWith("!")) { //设备请求语句
                    String c = String.valueOf(s.charAt(1));
//                    System.out.println(deviceManagement.devices.size());
                    log.debug("设备{}请求", c);
                    //放入设备的等待队列中
                    deviceManagement.devices.get(c).arrayBlockingQueue.put(new ProcessDeviceUse(this, s.charAt(2) - '0'));

                    //将其设为阻塞状态
                    pcb.state = 2;
                    //从就绪队列中选一个进程运行
                    ProcessScheduling.blocking.add(this);
                    ProcessScheduling.runing = null;

                    this.wait();

                } else if (s.equals("end")) { //终止语句
                    bufferedReader.close();
                    file.close();
                    wirth();
                    stop = true;
                }

                Thread.sleep(2000); //模拟处理过程

                pcb.state = 0; //就绪态
                ProcessScheduling.readyQueues.add(ProcessScheduling.runing);
                ProcessScheduling.runing = null;
            }

        }
    }

}
