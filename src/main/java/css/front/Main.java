package css.front;

import css.core.process.ProcessScheduling;
import css.out.file.FileApp;
import css.out.file.entity.dir;
import css.out.file.entity.file;
import css.out.file.enums.ROOT_PATH;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

import static css.out.file.FileApp.addContent;
import static css.out.file.entiset.GF.DIR_EXTEND;
import static css.out.file.entiset.GF.FILE_EXTEND;
import static css.out.file.handleB.HandleFile.str2Path;

/**
 * 前端作为项目主启动类
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException {

        // 读取Spring配置文件, 但是有可能遇到不存在问题
        ApplicationContext context =
                new ClassPathXmlApplicationContext("spring-config.xml");
        ProcessScheduling processScheduling = (ProcessScheduling) context.getBean("processScheduling");
        processScheduling.use();

        // 运行图形化主界面线程
        FileApp app = new FileApp();
//        app.kickDiskRoboot();        //清空磁盘启动


        file test_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Crazy", FILE_EXTEND.get(1), "I want to  surpass humanity -- with your blood!");
        file test_exe = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "#EXE", FILE_EXTEND.get(5), "b=3\nb++\nb++\nend\n");
        dir test_dir = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Guys", DIR_EXTEND.get(0));
        addContent(test_file);
        addContent(test_exe);
        addContent(test_dir);
/*        new Device("A").start();
        new Device("B").start();*/
//        new ProcessA("src/main/java/css/core/process/api/info.txt").start();
//        new ProcessA("src/main/java/css/core/process/api/info.txt").start();
//        new ProcessA("src/main/java/css/core/process/api/info.txt").start();
//        new ProcessA("src/main/java/css/core/process/api/info.txt").start();


        javax.swing.SwingUtilities.invokeLater(() -> {
            // 创建图形化界面
            MainGui gui = new MainGui();
            log.info("启动界面");
            // 设置面板可见
            gui.showGUI();
        });
    }
}
