package css.out.file.handleB;

import css.out.file.entity.block;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.FileApp.diskSyS;

/**
 * II级 磁盘块CRUD
 */
@Slf4j
public abstract class HandleBlock {

    //! 1.磁盘块操作

    /**
     * bytes设置BLOCKS中1个block内容
     *
     * @param bytes    字节数组内容
     * @param blockNum 块号
     */
    public static void setBytes21Block(byte[] bytes, int blockNum) {
        diskSyS.disk.BLOCKS.set(blockNum, new block(bytes));
    }


    /**
     * 从磁盘读取一个块的bytes内容
     *
     * @param sit 块号
     * @return 返回一个块的bytes内容
     */
    public static byte[] read1Block2Bytes(int sit) {
        block tempBlock = diskSyS.disk.BLOCKS.get(sit);
        return tempBlock.bytes;
    }
}
