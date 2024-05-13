package css.out.file.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static css.out.file.entiset.GF.DISK_NAME;
import static css.out.file.handleB.HandleDISK.getVoidFAT1;
import static css.out.file.handleB.HandleDISK.getVoidFAT2;
import static css.out.file.handleS.HandleDS.initialBLOCKS;

/**
 * 磁盘
 */
@Data
@Slf4j
public class disk {

    /**
     * 磁盘名
     */
    public String name;

    /**
     * FAT1文件分配表1
     * <p>位示图(下标位置) + 显示链接的指针(值)</p>
     * <p>下标代表当前块号, 值就是下一个块号</p>
     * <p>空值->Null_Pointer</p>
     */
    public List<Integer> FAT1;

    /**
     * FAT2文件分配表2
     * <p>位示图(下标位置) + 显示链接的指针(值)</p>
     * <p>空值->Null_Pointer</p>
     */
    public List<Integer> FAT2;

    /**
     * 一维磁盘块阵列
     * <p>->DISK_SIZE</p>
     */
    public List<block> BLOCKS;


    /**
     * 磁盘限定构造
     * <p>只允许初始化构造, 不允许自定义</p>
     */
    public disk() {
    }

    @Override
    public String toString() {
        return "disk{" +
                "name='" + name + '\'' +
                "\nFAT1=" + FAT1 +
                "\nFAT2=" + FAT2 +
                "\nBLOCKS=" + BLOCKS +
                '}';
    }


    /**
     * diskJava对象初始化
     */
    public static disk initialDisk() {
        disk disk = new disk();
        disk.name = DISK_NAME;
        disk.BLOCKS = initialBLOCKS(); //获得初始磁盘空间(全0)
        disk.FAT1 = getVoidFAT1(); //获得FAT1对象
        disk.FAT2 = getVoidFAT2(); //获得FAT2对象
        return disk;
    }

}
