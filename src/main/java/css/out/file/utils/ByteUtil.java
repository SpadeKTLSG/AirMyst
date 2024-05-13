package css.out.file.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public abstract class ByteUtil {

    //! 1. 基础封装

    /**
     * 自制byte数组合并
     *
     * @param byte_1 byte数组1
     * @param byte_2 byte数组2
     * @return 合并后的byte数组
     * @author SK
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    //! 2. 类型转换


    /**
     * 0-127Integer -> Byte[]
     *
     * @param num 0-127的数字
     * @return byte数组(只有一个元素)
     */
    public static byte[] Int2Byte(Integer num) {
        if (num < 0 || num > 127) {//这种数字只能适用于0-127的数字
            log.warn("数字{}溢出无法转换", num);
            return new byte[]{(byte) 127};//只能返回最大值
        }

        return new BigInteger(Integer.toBinaryString(num), 2).toByteArray();
    }


    /**
     * Byte[] -> Integer
     *
     * @param bytes byte数组(只有一个元素)
     * @return 0-127的数字
     */
    public static Integer byte2Int(byte[] bytes) {

        int sit = bytes.length;
        byte[] bytes1 = new byte[sit];

        for (int i = bytes.length - 1; i >= 0; i--)
            if (bytes[i] == 32) sit--;//如果找到一个空格, 就把sit往前移动(-1)

        System.arraycopy(bytes, 0, bytes1, 0, sit);//使用拷贝工具, 将0-sit的bytes拷贝到新的byte数组中

        return Integer.parseInt(new BigInteger(bytes1).toString(2), 2);
    }


    /**
     * 空格分隔Str -> byte[]
     *
     * @param str 空格分隔的Str对象
     * @return byte数组
     */
    public static byte[] str2Byte(String str) {
        String[] str_temp = str.split(" "); //将str按照空格分割为String[]
        byte[] bytes_temp = new byte[str_temp.length];//创建一个byte[]

        for (int i = 0; i < str_temp.length; i++)
            bytes_temp[i] = Byte.parseByte(str_temp[i]);


        return bytes_temp;
    }


    /**
     * byte[]-> 空格分隔Str
     *
     * @param bytes byte数组
     * @return 空格分隔的Str对象
     */
    public static String byte2Str(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte aByte : bytes)
            stringBuilder.append(aByte).append(" ");

        return stringBuilder.toString();
    }


    //! 3. 逻辑实现


    /**
     * 字节[] -> 定长字节[]
     *
     * @param bytes 指定的字节数组
     * @param len   指定的数组长度
     * @return 转换后的字节数组
     */
    public static byte[] toFixLenBytes(byte[] bytes, int len) {

        if (bytes.length < len) { // 如果长度小于len，就在右边填充空格

            byte[] padded = new byte[len];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            for (int i = bytes.length; i < len; i++)
                padded[i] = ' ';

            return padded;

        } else if (bytes.length > len) { // 如果compressed的长度大于len，就截取左边的len个字节

            log.warn("有对象被截断");
            byte[] truncated = new byte[len];
            System.arraycopy(bytes, 0, truncated, 0, len);

            return truncated;
        } else {// 如果compressed的长度等于len，就直接返回
            return bytes;
        }
    }


}
