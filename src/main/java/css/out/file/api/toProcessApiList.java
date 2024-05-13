package css.out.file.api;

import css.core.process.ProcessA;
import css.out.file.entity.dir;
import css.out.file.entity.file;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 与进程的功能api
 */
@Slf4j
public class toProcessApiList {

    /**
     * 读取文件的标志位 0: No 1: Yes
     */
    public static int isExeFile = 0; //接下来读取的是否是可执行文件

    /**
     * 一般的文件 -> 进程处理
     */
    public static void handleCommon(Object object) { //Controller
        if (object == null) return; //判断是否为空, 空就是用户输入错误

        if (object instanceof file temp) {
            String allName = temp.fcb.pathName.split(":")[1] + temp.fcb.getExtendName();
            notifyProcessSyS(mkObject(allName, temp.getContent())); //通知进程创建一个文件处理进程, 传递虚拟DTO文件/文件夹(目录)对象到工作文件夹

        } else if (object instanceof dir temp) {
            String allName = temp.fcb.pathName.split(":")[1]; //这个文件夹不能用空的扩展名, 不如会报错, 因此设置为空
            notifyProcessSyS(mkObject(allName, temp.getContent()));

        } else {
            log.warn("传递对象类型错误");
        }

    }

    /**
     * 创建文件到缓冲区
     *
     * @param allName 文件名(包含后缀)
     * @param content 文件内容
     * @return 文件路径
     */
    public static Path mkObject(String allName, String content) {
        //comment: 由于目录和文件如果设计两套生成策略会很不友好, 因此我决定全部用文件来代替

        File resourcesDirectory = new File("src/main/resources/static/tmp");
        //首先清空与进程模块交互的缓冲区 : 清空tmp文件夹内容
        File[] files = resourcesDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    log.warn("删除缓冲区文件{} 失败", file.getName());
                }
            }
        }

        //log.debug("清理缓冲区{} 成功", resourcesDirectory.getName());

        File newFile = new File(resourcesDirectory.getAbsolutePath() + "/" + allName);

        try {
            Files.createFile(newFile.toPath());
            if (!newFile.exists()) {
                log.warn("创建文件{} 失败", newFile.getName());
            }
        } catch (IOException e) {
            log.warn("创建文件{} 失败, errorStack{}", newFile.getName(), e.getStackTrace());
        }

        //写入内容content到文件(自动换行)
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(String.valueOf(newFile.toPath())), StandardCharsets.UTF_8))) {

            bw.write(content);

        } catch (Exception e) {
            log.warn("写入文件{} 失败, errorStack{}", newFile.getName(), e.getStackTrace());
        }

        log.debug("创建文件到缓冲区{} 成功", newFile.getName());

        return newFile.toPath();
    }

    /**
     * 通知进程系统创建进程读取path文件
     *
     * @param path 文件路径
     */
    public static void notifyProcessSyS(Path path) {
        log.info("正在通知进程系统创建进程读取path文件...{}", path.getFileName());

        if (isExeFile == 0) { //如果不是可执行文件, 就什么都不做
            log.debug("传递文件对象{}", path.getFileName());
        } else {    //如果是可执行文件, 就创建进程
            try {
                log.debug("传递文件对象{}", path.getFileName());
/*                log.debug("清理残留缓冲进程对象中...");

                //删除 static/tmp 下的所有文件
                File resourcesDirectory = new File("src/main/resources/static/tmp");
                File[] files = resourcesDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.delete()) {
                            log.warn("删除缓冲区文件{} 失败", file.getName());
                        }
                    }
                }

                log.debug("清理缓冲区{} 成功", resourcesDirectory.getName());*/
                new ProcessA(path.toString()).start();

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

}
