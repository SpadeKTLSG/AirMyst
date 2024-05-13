package css.core.memory;

import css.core.process.ProcessScheduling;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static css.core.process.ProcessScheduling.linkedList;
import static css.out.file.api.toFrontApiList.giveBlockStatus2Front;


/**
 * 内存管理器
 */
@Slf4j
public class MemoryManager {

    /**
     * 内存块
     */
    private static final MemoryBlock[][] memory;

    /**
     * 进程状态
     */
    private static final int[][] cleanblock;


    static {
        //用64个块初始化内存，每个块可存储3个字符
        memory = new MemoryBlock[8][8];
        cleanblock = new int[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                memory[i][j] = new MemoryBlock(); //初始化内存块为---
                cleanblock[i][j] = -1; //初始化为-1
            }
        }
    }


    /**
     * 分配内存
     *
     * @param processId 进程ID
     * @param data      数据
     * @author A
     */
    public static void allocateMemory(int processId, String data) {

        // 查找要分配的连续块
        int consecutiveBlocks = data.length() / 3 + (data.length() % 3 == 0 ? 0 : 1);
        int[] startingBlock = findConsecutiveBlocks(consecutiveBlocks);

        // 如果找到连续块，则分配内存
        if (startingBlock != null) {
            MemoryBlock[] allocatedBlocks = new MemoryBlock[consecutiveBlocks];
            int blockIndex = 0;
            for (int i = startingBlock[0]; i < startingBlock[0] + consecutiveBlocks; i++) {
                for (int j = startingBlock[1]; j < 8; j++) {

                    if (blockIndex < data.length()) {
                        int blockSize = Math.min(3, data.length() - blockIndex);
                        memory[i][j].setContent(data.substring(blockIndex, blockIndex + blockSize));
                        allocatedBlocks[blockIndex] = memory[i][j];
                        blockIndex += blockSize;
                    }
                    //跟踪内存被哪些进程所占用
                    cleanblock[i][j] = processId;
                }
            }

            System.out.println("为进程分配的内存 " + processId);
        } else {
            System.out.println("进程的内存分配失败 " + processId);
        }
    }

    /**
     * 查找连续块
     *
     * @param consecutiveBlocks 连续块
     * @return 连续块数组
     * @author A
     */
    private static int[] findConsecutiveBlocks(int consecutiveBlocks) {

        //查找并返回连续分配的起始块索引
        for (int i = 0; i < 8; i++)
            for (int j = 0; j <= 8 - consecutiveBlocks; j++)
                if (isConsecutiveBlocksAvailable(i, j, consecutiveBlocks))
                    return new int[]{i, j};

        log.warn("内存不足，无法分配连续块");
        return null;
    }

    /**
     * 检查连续块是否可用
     *
     * @param row               row
     * @param col               col
     * @param consecutiveBlocks 连续块
     * @return 是否可用
     */
    private static boolean isConsecutiveBlocksAvailable(int row, int col, int consecutiveBlocks) {
        //检查从给定索引开始的连续块是否可用
        for (int j = col; j < col + consecutiveBlocks; j++)
            if (!memory[row][j].getContent().equals("---"))
                return false;

        return true;
    }


    /**
     * 释放结束进程的内存
     *
     * @param processId 进程ID
     */
    public static void releaseMemory(int processId) {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (cleanblock[i][j] == processId) {
                    memory[i][j].setContent("---");
                    cleanblock[i][j] = -1;
                }
            }
        }
    }


    /**
     * 显示内存状态
     */
    public static void displayMemory() {
        int status = 0;

        //显示内存的当前状态
//        System.out.println("Memory Status:");
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
//                System.out.print(memory[i][j].getContent() + " ");
                if (memory[i][j].getContent().equals("---")) {
                    status++;
                }
            }
//            System.out.println();

        }
        // ? SK 暂时停止调用展示, 调试结束
//        System.out.println("空闲空间:"+status);
//        return status; //返回空闲空间
    }

    /**
     * 获取系统内存使用情况
     *
     * @return 系统内存使用情况
     */
    public static int getSystemMemoryUsage() {

        //系统内存 = 磁盘大小 + 系统分配
        List<Integer> usage = giveBlockStatus2Front(); //从文件模块获取占用表

        List<Integer> temp = new ArrayList<>(); //查表, 找到系统占用大小

        for (Integer e : usage)
            if (e == 3)
                temp.add(e);

        return temp.size();
    }


    /**
     * 传递进程内存状态
     *
     * @return greatMemoryStack
     * @author SK(C) & A
     */
    public static List<Integer> givememorystatus() {
        List<Integer> greatmemory = new ArrayList<>();
        // 0: 空闲 1:占用 2:正在使用  3: 系统-> DTO FAT


        //? BrainStorm by SK
        //1. 确定方法获取途径... ok

        //2. 2个区段
        //2.1 一区段, 获取数据, 确定对应状态的块数是多少
        //      对应方法: 获取系统占用盘块数( int ) / 获取 .... / .....
        // 一区段负责向二区段提供对应状态占用的盘块数

        //2.2 二区段, 根据已有的对应盘块数, 通过一个for循, 逐个添加对应状态的盘块数
        //      for(i 0 -> 64){
        //          for(System){i -> V; continue}
        //          for(Ready){i -> V; continue}
        //          for(Block){i -> V; continue}
        //          溢出? -> break;报警}
        //      }

        //3. 返回值, 二区段的List<Integer> : K is Item, V is Status {0,1,2}


        //一区段
        //创建展示进程需要的两个状态队列
        List<String> blockList = new ArrayList<>(10); // 阻塞队列
        List<String> readyList = new ArrayList<>(10); // 就绪队列

        //进程链表遍历
        linkedList.forEach((k, v) -> {
            if (v.pcb.state == 2) { //2代表阻塞
                blockList.add(String.valueOf(v.pcb.pcbId));
            } else if (v.pcb.state == 0) { //0代表就绪
                readyList.add(String.valueOf(v.pcb.pcbId));
            }
        });

        boolean running_flag = ProcessScheduling.runing != null; //运行判断

        //正在运行 = 当前指令 = 如果有就+1

        //二区段
        for (int i = 0; i < 64; i++) {

            if (i < getSystemMemoryUsage() * 2) { // 系统模块占用内存 *2
                greatmemory.add(3);
                continue;
            }

            if (i < getSystemMemoryUsage() * 2 + blockList.size() + readyList.size()) { //普通占用内存 占用 = 就绪 + 阻塞
                greatmemory.add(1);
                continue;
            }

            if (running_flag && i == getSystemMemoryUsage() * 2 + blockList.size() + readyList.size()) { //正在运行
                greatmemory.add(2);
                continue;
            }

            greatmemory.add(0);

        }

     /*   int systemBlock = 2 * getSystemMemoryUsage(); // 设置系统模块盘块占用内存为2 * 盘块数( 2 * 64B = 128B)
        for (int i = 0; i < systemBlock; i++) {
            greatmemory.add(3);
        }

        for (int i = 0; i < 64 - displayMemory() - systemBlock; i++) {
            greatmemory.add(1);
        }

        for (int i = 0; i < displayMemory(); i++) {
            greatmemory.add(0);
//            System.out.println("空闲空间:" + displayMemory());
        }


        if (ProcessScheduling.runing != null) {
            greatmemory.add(2);
        }

*/
        return greatmemory;

    }
}

