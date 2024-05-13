package css.core.process;


import css.core.memory.MemoryManager;
import css.out.device.DeviceManagement;
import css.out.device.ProcessDeviceUse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.Iterator;
import java.util.Map;


public class ProcessA extends Thread {
    ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-config.xml");
    ProcessScheduling processScheduling = (ProcessScheduling) context.getBean("processScheduling");
    DeviceManagement deviceManagement = (DeviceManagement) context.getBean("deviceManagement");


    volatile public boolean stop = false;
    public Pcb pcb;
    public FileReader file;
    public BufferedReader bufferedReader;


    public ProcessA(String fileName) throws IOException {
        pcb = new Pcb();
        file = new FileReader(fileName);
        bufferedReader = new BufferedReader(file);
    }

    @Override
    public void run() {
        try {
            System.out.println(processScheduling);
            synchronized (this) {
                ProcessScheduling.readyQueues.add(this);
                ProcessScheduling.linkedList.put(this.pcb.pcbId, this);
                this.wait();
            }
            int i = 1;
            while (!stop) {
                CPU();
            }
            MemoryManager.releaseMemory(pcb.pcbId);
            MemoryManager.displayMemory();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void wirth() throws IOException {
        Iterator<Map.Entry<String, Integer>> iterator = pcb.register.entrySet().iterator();
        FileWriter fileWriter = new FileWriter("src/main/resources/common/file/out.txt"); //File
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> next = iterator.next();
            bufferedWriter.write(next.getKey() + "=" + next.getValue() + "\t\n");
        }
        bufferedWriter.close();
        fileWriter.close();
    }

    @Transactional
    public void CPU() throws IOException, InterruptedException {
        synchronized (this) {
            if (pcb.state == 1) {
                String s = bufferedReader.readLine();
                System.out.println(s);
                MemoryManager.allocateMemory(pcb.pcbId, s);
                MemoryManager.displayMemory();
                pcb.lines = s;
                if (s == null) {
                    pcb.state = 3;
                    ProcessScheduling.runing = null;
                    processScheduling.getReadyToRun();
                    this.stop = true;
                } else if (s.contains("=")) {
                    String[] split = s.split("=");
                    pcb.register.put(split[0], Integer.valueOf(split[1]));
                } else if (s.contains("++")) {
                    String[] split = s.split("\\+\\+");
                    Integer integer = pcb.register.get(split[0]);
                    pcb.register.put(split[0], integer + 1);
                } else if (s.contains("--")) {
                    String[] split = s.split("--");
                    Integer integer = pcb.register.get(split[0]);
                    pcb.register.put(split[0], integer - 1);
                } else if (s.startsWith("!")) {
                    String c = String.valueOf(s.charAt(1));
                    System.out.println(deviceManagement.devices.size());
                    //放入设备的等待队列中
                    deviceManagement.devices.get(c).arrayBlockingQueue.put(new ProcessDeviceUse(this, s.charAt(2) - '0'));

                    //将其设为阻塞状态
                    pcb.state = 2;
                    //从就绪队列中选一个进程运行
                    ProcessScheduling.blocking.add(this);
                    ProcessScheduling.runing = null;
                    this.wait();
                } else if (s.equals("end")) {
                    bufferedReader.close();
                    file.close();
                    wirth();
                    stop = true;
                }

                Thread.sleep(2000); //

                pcb.state = 0;
                ProcessScheduling.readyQueues.add(ProcessScheduling.runing);
                ProcessScheduling.runing = null;
            }

        }
    }

}
