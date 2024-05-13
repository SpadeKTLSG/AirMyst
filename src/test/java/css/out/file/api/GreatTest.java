package css.out.file.api;

import css.out.file.FileApp;
import css.out.file.entity.file;
import css.out.file.enums.ROOT_PATH;
import org.junit.Test;

import static css.out.file.FileApp.addContent;
import static css.out.file.entiset.GF.FILE_EXTEND;
import static css.out.file.handleB.HandleFile.str2Path;

public class GreatTest {

    @Test
    public void workWithFront() {

        //!正确打开方式: 先运行Main.java, 再运行GreatTest.java

        FileApp app = new FileApp();
//        app.kickDiskRoboot();        //清空磁盘启动

        file test_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Crazy", FILE_EXTEND.get(1), "I want to  surpass humanity -- with your blood!");
        file test_exe = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "#EXE", FILE_EXTEND.get(5), "b=3\nb++\nb++\nb++\nend\n");
        addContent(test_file);
        addContent(test_exe);


        //? 1. 测试命令接口
        //ok

        //? 2. 测试硬盘传递
        //ok

        //? 3. 测试文件树传递
        //ok
    }

    @Test
    public void globalCoWork() {

        FileApp app = new FileApp();    //!正确打开方式: 先运行Main.java, 再运行GreatTest.java
//        app.kickDiskRoboot();        //清空磁盘启动

        file test_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Crazy", FILE_EXTEND.get(1), "I want to  surpass humanity -- with your blood!");
        file test_exe = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "#EXE", FILE_EXTEND.get(5), "b=3\nb++\nb++\nb++\nend\n");
        addContent(test_file);
        addContent(test_exe);

    }

}
