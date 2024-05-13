package css.out.file.handleB;

import css.out.file.entity.FCB;
import css.out.file.entity.block;
import css.out.file.enums.FileDirTYPE;
import css.out.file.enums.ROOT_PATH;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static css.out.file.FileApp.diskSyS;
import static css.out.file.entiset.GF.*;
import static css.out.file.handleB.HandleBlock.setBytes21Block;
import static css.out.file.handleB.HandleFile.fcb2Bytes;
import static css.out.file.handleB.HandleTXT.write1Str2TXT;
import static css.out.file.utils.ByteUtil.byte2Str;
import static css.out.file.utils.ByteUtil.str2Byte;

/**
 * I级 磁盘(TXT)管理工具类
 */
@Slf4j
public abstract class HandleDISK {


    //! 1. 磁盘 - CRUD


    /**
     * 字节数组设置1个block内容+TXT位置写入
     *
     * @param bytes    字节数组内容
     * @param blockNum 块号
     */
    public static void setBytes21Block_TXT(byte[] bytes, int blockNum) {
        setBytes21Block(bytes, blockNum);
        write1Str2TXT(byte2Str(bytes), WORKSHOP_PATH + DISK_FILE, blockNum);
    }


    /**
     * 字符串设置1个block内容+TXT位置写入
     *
     * @param str      字符串内容
     * @param blockNum 块号
     */
    public static void setStr21Block_TXT(String str, int blockNum) {
        setBytes21Block(str2Byte(str), blockNum);
        write1Str2TXT(str, WORKSHOP_PATH + DISK_FILE, blockNum);
    }


    //! 2. FAT - CRUD


    /**
     * 挂载FAT到 BLOCKS
     *
     * @param BLOCKS   磁盘块阵列
     * @param FAT_Byte FAT字节对象
     * @param type     FAT类型: 1 or 2
     */
    public static void mountFAT2BLOCKS(List<block> BLOCKS, byte[] FAT_Byte, Integer type) {
        switch (type) {
            case 1 -> { //FAT 1
                BLOCKS.set(FAT1_DIR, new block(FAT_Byte));
            }
            case 2 -> { //FAT 2
                BLOCKS.set(FAT2_DIR, new block(FAT_Byte));
            }
            default -> {
                log.warn("FAT类型错误!");
            }
        }
    }


    /**
     * 获取空FAT1
     *
     * @return 默认FAT1
     */
    public static List<Integer> getVoidFAT1() {

        List<Integer> FAT = getVoidFAT2();

        FAT.set(0, 1); //0号块指向1号块FAT1
        FAT.set(1, 2); //1号块指向2号块FAT2
        FAT.set(2, 3); //2号块指向3号块(系统目录挂载块)
        //3号块指向空, 4号块为第一个空闲块

        return FAT;
    }


    /**
     * 获取空FAT2(全空)
     *
     * @return 默认FAT2
     */
    public static List<Integer> getVoidFAT2() {

        List<Integer> FAT = new ArrayList<>(FAT_SIZE);

        for (int i = 0; i < FAT_SIZE; i++)
            FAT.add(Null_Pointer);

        return FAT;
    }


    /**
     * FAT(List) -> Bytes
     *
     * @param fat FAT对象
     * @return FAT字节对象
     */
    public static byte[] FAT2Bytes(List<Integer> fat) {

        byte[] FATByte = new byte[FAT_SIZE];

        for (int i = 0; i < FAT_SIZE; i++) {

            if (Objects.equals(fat.get(i), Null_Pointer)) //如果这一项是空的, 就在磁盘上写0(空)
                FATByte[i] = 0;

            else
                FATByte[i] = fat.get(i).byteValue(); //将FAT列表中每一项的值转换为字节
        }

        return FATByte;
    }


    /**
     * Bytes -> FAT(List)
     *
     * @param bytes FAT字节对象
     * @return FAT对象
     */
    public static List<Integer> Bytes2FAT(byte[] bytes) {
        List<Integer> FAT = new ArrayList<>(FAT_SIZE);

        for (int i = 0; i < FAT_SIZE; i++)
            FAT.add(Null_Pointer);

        for (int i = 0; i < FAT_SIZE; i++)
            if ((int) bytes[i] != 0)
                FAT.set(i, (int) bytes[i]);

        return FAT;
    }


    /**
     * 将FAT1和FAT2合并为一整个FAT
     *
     * @return FAT1 + FAT2 = allFAT
     */
    public static List<Integer> mergeFATs() {
        List<Integer> allFAT = new ArrayList<>();
        for (int i = 0; i < FAT_SIZE; i++)
            allFAT.add(diskSyS.disk.FAT1.get(i));
        for (int i = 0; i < FAT_SIZE; i++)
            allFAT.add(diskSyS.disk.FAT2.get(i));
        return allFAT;
    }


    /**
     * 将逻辑allFAT分割为FAT1和FAT2保存
     *
     * @param allFAT 逻辑allFAT
     */
    public static void breakFAT(List<Integer> allFAT) {
        diskSyS.disk.FAT1 = allFAT.subList(0, FAT_SIZE);
        diskSyS.disk.FAT2 = allFAT.subList(FAT_SIZE, FAT_SIZE * 2);
    }


    /**
     * 指定FAT1为全满状态, 下一个空闲位置位于FAT2
     * <p>或: 指定FAT2为全满状态,全部被占满</p>
     *
     * @param type 1: FAT1 2: FAT2
     */
    public static void fullFillFAT(Integer type) {

        List<Integer> FAT1 = new ArrayList<>(FAT_SIZE);
        int i = 0;
        for (; i < FAT_SIZE; i++)
            FAT1.add(i + 1);

        FAT1.set(FAT_SIZE - 1, FAT_SIZE);

        diskSyS.disk.FAT1 = FAT1;

        if (type == 2) { //还要将FAT2设置为全满
            List<Integer> FAT2 = new ArrayList<>(FAT_SIZE);

            for (; i < FAT_SIZE * 2; i++)
                FAT2.add(i + 1);

            FAT2.set(FAT_SIZE - 1, Null_Pointer);

            diskSyS.disk.FAT2 = FAT2;
        }
        mountFAT2BLOCKS(diskSyS.disk.BLOCKS, FAT2Bytes(diskSyS.disk.FAT1), 1); //挂载FAT1字节对象
        mountFAT2BLOCKS(diskSyS.disk.BLOCKS, FAT2Bytes(diskSyS.disk.FAT2), 2); //挂载FAT2字节对象
    }


    /**
     * 使用键值对手动指定逻辑大FAT中的某个位置
     *
     * @param pos   位置
     * @param value 值
     */
    public static void specifyFAT(Integer pos, Integer value) {
        List<Integer> allFAT = mergeFATs();
        allFAT.set(pos, value);
        breakFAT(allFAT);
    }


    /**
     * 使用Map来手动指定逻辑大FAT中的一些位置的值
     *
     * @param map 键值对
     */
    public static void specifyFAT(Map<Integer, Integer> map) {
        List<Integer> allFAT = mergeFATs();

        for (Map.Entry<Integer, Integer> entry : map.entrySet())//解析传入的键值对, 将其设置到allFAT中
            allFAT.set(entry.getKey(), entry.getValue());

        breakFAT(allFAT);
    }


    /**
     * 利用FAT构建当前磁盘块的顺序访问序列order
     * <p>order的最后一项就是最后一个占用了的盘块, 其指向空</p>
     *
     * @return 顺序访问的盘块号List序列
     */
    public static List<Integer> getFATOrder() {

        int pos = 0;
        List<Integer> order = new ArrayList<>(); //顺序访问的盘块号

        List<Integer> allFAT = mergeFATs();


        //以下版本1只能适用于线性堆砌FAT内容的情况, 不适合交叉FAT, 不能线性遍历..!
        //因为被引用的pos可能是带有指向当前队列中其他节点的脏pointer, 会导致死循环
        //需要判断pos指向的地方是否已经在队列中, 如果在队列中, 就不再按照其内容添加了, 而是直接退出
        for (int i = 0; i < FAT_SIZE * 2; i++) {

            if (!Objects.equals(allFAT.get(pos), Null_Pointer)) {
                pos = allFAT.get(pos);
                order.add(pos);

            } else //查找链断裂, 代表找到了全部的Order
                return order;
        }

        //Fixed Faild by SK 12.30
/*
        int pos = 0;
        List<Integer> order = new ArrayList<>(); //顺序访问的盘块号

        List<Integer> allFAT = mergeFATs();

        for (int i = 0; i < FAT_SIZE * 2; i++) {

            if (!Objects.equals(pos, Null_Pointer)) {

                order.add(pos); //第一个肯定是在队列中的
                int pre = pos; //保存一份
                pos = allFAT.get(pos); //更新pos

                if (order.contains(pos)) {//如果新的pos仍然在队列中, 默认后来的覆盖之前的, 之前的被删除释放指针为空; 给予最后的对象最高控制
                    log.warn("系统遇到FAT问题, 正在自动重建FAT脏指针");
                    //干掉pos:取消被引用(删除逻辑已经实现), 取消order, 并且在FAT中指向空
                    order.remove(pos);
                    allFAT.set(pos, Null_Pointer);

                    breakFAT(allFAT);
                }



            } else { //查找链断裂, 代表找到了全部的Order
                return order;
            }
        }*/

        //还是找不到退出?
        log.debug("FATorder序列不能正常使用了");
        return null;
    }


    /**
     * FAT查找第一个空闲块: 找NP
     * <p>直接在FAT1 -> FAT2 中找值为NULL_POINTER的键</p>
     * 要删除掉虽然是NULL_Pointer但是事实上已经被指向的占用块: 从路径序列中去重取差集
     *
     * @return FAT序列的综合下标; if -1: 没有找到空闲块
     */
    public static Integer get1FreeFAT() {
        List<Integer> allFAT = mergeFATs();
        Map<Integer, Integer> allFATMap = new HashMap<>();

        for (int i = 0; i < FAT_SIZE * 2; i++)
            allFATMap.put(i, allFAT.get(i));

        List<Integer> order = getFATOrder();
        Integer pre;

        if (order != null) {
            pre = order.get(order.size() - 1);
        } else {
            log.warn("系统order序列不能正常使用了");
            return -1;
        }


        //从FATmap中去除已经被占用的块, 并去除0号块, 因为0和1号块是分派给FAT的, 2号块是分派给目录的
        for (Integer i : order)
            allFATMap.remove(i);
        allFATMap.remove(0);
        allFATMap.remove(1);
        allFATMap.remove(2);


        //不需要再去找为空值的块了, 因为没有被引用的空置节点也算是空闲块
        List<Integer> keys = allFATMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(Null_Pointer)) //需要找到空置节点序列, 在序列中取第一项
                .map(Map.Entry::getKey)
                .toList();

        Integer pos = keys.get(0);

        //直接找到第一个allFATMap的值


        if (pos != null && !pos.equals(FAT_SIZE * 2 - 1))
            return pos;

        //如果正常的空置队列里找不到, 就去探索没有被占用的悬空队列作为保底

        //从allFATMAP中获取一个任意值
        pos = allFATMap.values().stream()
                .toList().get(0);

        if (pos != null && !pos.equals(FAT_SIZE * 2 - 1))
            return pos;

        log.warn("咩有空闲块咯");
        //throw new RuntimeException("没有找到空闲块!");
        return -1;
    }


    /**
     * FAT序列中占用第一个空闲块, 返回逻辑FAT位置
     *
     * @return 逻辑FAT位置
     */
    public static Integer set1FATUse() {

        Integer pos = get1FreeFAT(); //FAT序列中有没有空闲块

        if (pos == -1) {
            log.warn("FAT序列中没有空闲块");
            return -1;
        }

        List<Integer> order = getFATOrder(); //构造FAT序列,获得其最后一项的位置

        Integer pre;

        if (order != null) {
            pre = order.get(order.size() - 1); //order最后一项的位置
        } else {
            log.warn("系统order序列不能正常使用了");
            return -1;
        }

        List<Integer> allFAT = mergeFATs();

        allFAT.set(pre, pos); //将最后一项指向空闲块位置

        breakFAT(allFAT);

        return pos;
    }


    /**
     * 释放block空间: 将FAT最晚的一个块指向这个位置, 这个位置同样指向空(514)
     *
     * @param blockNum 要释放的块号
     * @return FAT中释放的块号(可能已经不能使用) / -1失败
     */
    public static Integer set1FATFree(int blockNum) {

        List<Integer> order = getFATOrder();

        int pos;
        if (order != null) {//定位blockNum
            pos = order.indexOf(blockNum);
        } else {
            log.warn("系统order序列不能正常使用了");
            return -1;
        }

        if (pos == -1) {
            log.warn("要释放的块号不在FAT序列中");
            return -1;
        }

        //LinkedList思维 : 在allFAT中定位值为pre和pos的sit, 将当前posSitFAT指向空

        int pre = pos - 1;

        if (pos == order.size() - 1) { //pos是序列中的最后者

            List<Integer> allFAT = mergeFATs();

            int posSit = allFAT.indexOf(order.get(pos));
            int preSit = allFAT.indexOf(order.get(pre));

            //pre指向空, pos指向空
            allFAT.set(preSit, Null_Pointer);
            allFAT.set(posSit, Null_Pointer);

            breakFAT(allFAT);
            return posSit;

        } else { //pos不是序列中的最后者

            Integer post = order.get(pos + 1);

            List<Integer> allFAT = mergeFATs();

            int preSit = allFAT.indexOf(pre);
            int posSit = allFAT.indexOf(pos);
            int postSit = allFAT.indexOf(post);

            //pre指向post
            allFAT.set(preSit, postSit);
            allFAT.set(posSit, Null_Pointer);
//            allFAT.set(postSit, Null_Pointer);

            breakFAT(allFAT);
            return posSit;
        }


    }

    //! 3. 初始目录

    /**
     * 将根目录下的根目录集合一并挂载到磁盘系统中(Blocks + TXT), 默认是位于FAT后面
     * <p>需要创建temp_fcb封装每一个目录, 最后还得只写一次TXT</p>
     */
    public static void mountDefaultDir2BLOCKS() {
        //setBytes21Block_TXT(str2Byte(root_path.name), pos); //可不能这么写哦! 是把这8个B直接合并FCB
        //简化: 由于没有对这些根目录操作的可能, 因此采用直接注入磁盘Blocks与TXT的方法


        int pos = FAT2_DIR + 1; //pos = 3, 从4号块就是自己的内容了
        FCB temp_fcb = new FCB();
        byte[] bytes = new byte[64]; //byteBuilder - 8 * 8
        int count = 0; //当前插入字符byteBuilder末尾指针


        for (ROOT_PATH root_path : ROOT_PATH.values()) {

            temp_fcb = new FCB("/:" + root_path.getName(), pos, FileDirTYPE.DIR);
            System.arraycopy(fcb2Bytes(temp_fcb), 0, bytes, count, 8);
            count += FCB_BYTE_LENGTH;
        }

        setBytes21Block_TXT(bytes, pos);
    }

}
