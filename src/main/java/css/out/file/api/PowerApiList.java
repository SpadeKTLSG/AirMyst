package css.out.file.api;

import css.out.file.FileApp;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.entiset.GF.ROOT_AUTH;

/**
 * 文件系统api
 */
@Slf4j
public class PowerApiList {

    /**
     * 开机
     */
    public static void startFile() {
        FileApp app = new FileApp(); //开机 - 将FileApp类实例化到内存中
    }


    /**
     * 特殊启动
     *
     * @param type 1:格式化 2:重读 3:摧毁系统
     */
    public static void handleFile(Integer type) {
        FileApp app = new FileApp(ROOT_AUTH, type); //特殊启动模式
    }

}
