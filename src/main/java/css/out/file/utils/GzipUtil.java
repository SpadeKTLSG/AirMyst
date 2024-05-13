package css.out.file.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip压缩工具类 - 废案
 *
 * @author SK
 */
public abstract class GzipUtil {

    /**
     * 将一个String对象压缩并转换为字节数组
     *
     * @param s 要压缩的String对象
     * @return 压缩后的字节数组
     * @throws IOException 如果发生IO异常
     */
    public static byte[] zip(String s) throws IOException {
        // 使用UTF-8编码，将s转换为ByteBuffer对象
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(s);
        // 获取ByteBuffer对象的字节数组
        byte[] bytes = buffer.array();
        // 创建一个字节数组输出流，用于压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 创建一个gzip输出流，用于压缩
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        // 将字节数组写入gzip输出流
        gos.write(bytes);
        // 关闭gzip输出流
        gos.close();
        // 获取压缩后的字节数组
        byte[] compressed = baos.toByteArray();
        // 关闭字节数组输出流
        baos.close();
        // 使用BASE64编码，将压缩后的字节数组转换为字符串
        String encoded = Base64.encodeBase64String(compressed);
        // 返回编码后的字节数组
        return encoded.getBytes();
    }

    /**
     * 将一个压缩后的字节数组解压并还原为原始的String对象
     *
     * @param bytes 要解压的字节数组
     * @return 还原后的String对象
     * @throws IOException 如果发生IO异常
     */
    public static String unzip(byte[] bytes) throws IOException {
        // 使用BASE64解码，将字节数组转换为字符串
        String decoded = new String(bytes, StandardCharsets.UTF_8);

        // 使用UTF-8编码，将decoded转换为ByteBuffer对象
        ByteBuffer buffer_temp = StandardCharsets.UTF_8.encode(decoded);
        // 获取ByteBuffer对象的字节数组
        bytes = buffer_temp.array();
        // 创建一个字节数组输入流，用于解压
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        // 创建一个gzip输入流，用于解压
        GZIPInputStream gis = new GZIPInputStream(bais);
        // 创建一个字节数组输出流，用于解压
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 定义一个缓冲区大小
        int bufferSize = 1024;
        // 定义一个缓冲区字节数组
        byte[] buffer = new byte[bufferSize];
        // 定义一个读取的字节数
        int len;

        while ((len = gis.read(buffer)) != -1) { // 循环读取gzip输入流，直到结束
            baos.write(buffer, 0, len); // 将缓冲区字节数组写入字节数组输出流
        }

        gis.close();
        bais.close();
        baos.close();

        // 使用UTF-8编码，将解压后的字节数组转换为字符串
        return baos.toString(StandardCharsets.UTF_8);
    }
}
