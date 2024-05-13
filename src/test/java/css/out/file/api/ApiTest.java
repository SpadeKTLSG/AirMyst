package css.out.file.api;

import css.core.process.ProcessA;
import css.out.file.FileApp;
import css.out.file.entity.dir;
import css.out.file.entity.file;
import css.out.file.enums.ROOT_PATH;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static css.out.file.FileApp.addContent;
import static css.out.file.api.MonitorApiList.diskUsageAmount_All;
import static css.out.file.api.MonitorApiList.diskUsageAmount_SyS;
import static css.out.file.api.PowerApiList.startFile;
import static css.out.file.api.toFrontApiList.*;
import static css.out.file.entiset.GF.DIR_EXTEND;
import static css.out.file.entiset.GF.FILE_EXTEND;
import static css.out.file.handleB.HandleFile.str2Path;

public class ApiTest {


    //!使用API接口Test
    @Test
    public void testFileSystemAPI() {
        startFile();
        //展示系统占用情况
        System.out.println(diskUsageAmount_All());
        System.out.println(diskUsageAmount_SyS());


    }

    @Test
    public void handleTest() {
        FileApp app = new FileApp();
//        app.kickDiskRoboot();
//        app.coverDiskRoboot();
        file test_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Crazy", FILE_EXTEND.get(1), "I want to  surpass humanity -- with your blood!");
        file test_exe = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "#EXE", FILE_EXTEND.get(5), "b=3\nb++\nb++\nb++\nend\n");
        dir test_dir = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Guys", DIR_EXTEND.get(0));
        addContent(test_file);
        addContent(test_exe);
        System.out.println(Arrays.toString(givePath2Front()));
        System.out.println(giveBlockStatus2Front());
    }

    @Test
    public void CommonApiTest1() {
        System.out.println("test交互式文件-进程传递");
        FileApp app = new FileApp();
        //使用自己的Java文件对象完成一次Common交互流程
        file test_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Crazy", FILE_EXTEND.get(1), "I want to  surpass humanity -- with your blood!");
        file test_exe = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "#EXE", FILE_EXTEND.get(5), "b=3\nb++\nb++\nb++\nend\n");
        dir test_dir = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Guys", DIR_EXTEND.get(0));
        addContent(test_file);
        addContent(test_exe);
//        addContent(test_dir);
//        app.state();


        //模拟前端请求
//        getFrontRequest("create Crazy.txt /tmp");
//        getFrontRequest("delete Crazy.txt /tmp");
//        getFrontRequest("move Crazy.txt /tmp /home");
//        getFrontRequest("type Crazy.txt /tmp");
//        getFrontRequest("change Crazy.txt /tmp Cookie");
//        getFrontRequest("makdir Man /tmp");
//        getFrontRequest("chadir Guys /tmp Cookie");
//        getFrontRequest("deldir Guys /tmp");
        getFrontRequest("run #EXE.dll /tmp");
//        getFrontRequest("run Crazy.txt /tmp");
//        getFrontRequest("edit Crazy.txt /tmp nut");
//        app.state();
    }

    @Test
    public void frontApiTest1() throws IOException {
        System.out.println("test交互式文件-前端传递");
        FileApp app = new FileApp();
        file test_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Crazy", FILE_EXTEND.get(1), "I want to  surpass humanity -- with your blood!");
        file test_exe = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "#EXE", FILE_EXTEND.get(5), "b=3\nb++\nb++\nb++\nend\n");
        dir test_dir = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Guys", DIR_EXTEND.get(0));
        addContent(test_file);
        addContent(test_exe);
        addContent(test_dir);
//        getFrontRequest("create Crazy.txt /tmp");
        new ProcessA("src/main/java/css/core/memory/api/info.txt").start();
        new ProcessA("src/main/java/css/core/process/api/info.txt").start();

    }
}
