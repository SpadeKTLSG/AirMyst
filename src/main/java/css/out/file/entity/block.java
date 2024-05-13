package css.out.file.entity;

import java.util.Arrays;

import static css.out.file.entiset.GF.BLOCK_SIZE;

/**
 * 字节构成的单个磁盘块
 */
public class block {

    /**
     * 一个磁盘块的字节数组
     */
    public byte[] bytes;

    /**
     * 新磁盘块构造,全初始化为0
     */
    public block() {
        this.bytes = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++)
            this.bytes[i] = Byte.parseByte("0");
    }

    /**
     * 从字节数组直接构造磁盘块
     *
     * @param blockBytes 字节数组
     */
    public block(byte[] blockBytes) {

        if (blockBytes.length < BLOCK_SIZE) { //字节数组长度不足BLOCK_SIZE,则补空格
            this.bytes = new byte[BLOCK_SIZE];
            System.arraycopy(blockBytes, 0, this.bytes, 0, blockBytes.length);

        } else if (blockBytes.length == BLOCK_SIZE) {
            this.bytes = blockBytes;

        } else { //字节数组长度大于BLOCK_SIZE,则截取
            this.bytes = new byte[BLOCK_SIZE];
            System.arraycopy(blockBytes, 0, this.bytes, 0, BLOCK_SIZE);

        }
    }

    @Override
    public String toString() {
        return "\nblock{" +
                "bytes=" + Arrays.toString(bytes) +
                "}";
    }
}
