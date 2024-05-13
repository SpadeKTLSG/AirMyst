package css.out.file.handleB;

import css.out.file.entity.block;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static css.out.file.entiset.GF.*;

/**
 * II级 磁盘块映射文件(TXT文件)工具类
 *
 * @comment 取名TXT会不会太随便了, 嗯?
 */
@Slf4j
public abstract class HandleTXT {

    //! 1.单行TXT操作

    /**
     * Str msg直接狠狠注入TXT对应行
     * <p>需要上级调用控制写入不越界</p>
     *
     * @param msg  要写入的字符串
     * @param path 目标TXT文件路径
     * @param pos  位置
     * @comment 这样我们就真的回不到过去了, 前辈...
     */
    public static void write1Str2TXT(String msg, String path, Integer pos) {
        StringBuilder sb = new StringBuilder();            //全部读取保存到StringBuffer
        Integer pos_temp = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (pos_temp.equals(pos)) {
                    if (msg.length() < BLOCK_SIZE) {
                        msg = msg + " ".repeat(BLOCK_SIZE - msg.length());
                    }
                    sb.append(msg).append("\n");
                } else {
                    sb.append(line).append("\n");
                }
                pos_temp++;
            }

        } catch (Exception e) {
            log.error("注入msg到{}位置失败! {}", path, e.getMessage());
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {

            for (String line : sb.toString().split("\n")) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();

        } catch (Exception e) {
            log.error("写入磁盘映射文件{}失败, 错误日志: {}", path, e.getMessage());
        }
    }


    /**
     * 在TXT中读取一个block
     *
     * @param path 磁盘TXT映射文件路径
     * @param pos  行号(位置)
     * @return String化的内容对象(还是byte)
     * @deprecated
     */
    public static String read1BlockiTXT(String path, Integer pos) {

        String res = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(WORKSHOP_PATH + DISK_FILE), StandardCharsets.UTF_8))) {

            for (int i = 0; i < pos; i++)  //跳过前面的pos行, 读取pos行的内容到s
                br.readLine();

            res = br.readLine();

        } catch (Exception e) {
            log.error("读取磁盘映射文件{}失败, 错误日志: {}", path, e.getMessage());
        }

        return res;
    }

    /**
     * 在TXT中读取一个block
     *
     * @param pos 行号(位置)
     * @return String化的内容对象(还是byte)
     */
    public static String read1BlockiTXT(Integer pos) {
        String path = WORKSHOP_PATH + DISK_FILE;
        String res = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(WORKSHOP_PATH + DISK_FILE), StandardCharsets.UTF_8))) {

            for (int i = 0; i < pos; i++)  //跳过前面的pos行, 读取pos行的内容到s
                br.readLine();

            res = br.readLine();

        } catch (Exception e) {
            log.error("读取磁盘映射文件{}失败, 错误日志: {}", path, e.getMessage());
        }

        return res;
    }

    //! 2.整体TXT操作

    /**
     * 将BLOCKS全部内容写入目标TXT文件
     *
     * @param BLOCKS 磁盘块阵列
     * @param path   目标TXT文件路径
     */
    public static void writeAllDISK2TXT(List<block> BLOCKS, String path) {

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {

            for (block block : BLOCKS) {

                byte[] byte_temp = block.bytes;
                StringBuilder sb = new StringBuilder();

                for (byte b : byte_temp)
                    sb.append(b).append(" ");

                bw.write(sb.toString());
                bw.newLine();
            }

        } catch (Exception e) {
            log.error("写入磁盘映射文件错误日志: {}", e.getMessage());
        }

        //log.debug("写入磁盘映射文件 {} 成功", path);
        log.debug("写入磁盘映射文件成功");
    }


    /**
     * 从目标TXT文件中读取磁盘对象DISK.BLOCKS的流式全部内容
     * <p>存入一整个String大对象中</p>
     *
     * @param path 目标TXT文件路径
     * @return 一整个String大对象
     */
    public static String readAllTXT2Str(String path) {

        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;

            while ((line = br.readLine()) != null)
                sb.append(line).append("\n");


        } catch (Exception e) {
            log.error("读取磁盘映射文件{}失败, 错误日志: {}", path, e.getMessage());
        }

        log.debug("读取磁盘映射文件成功");
        return sb.toString();
    }


}
