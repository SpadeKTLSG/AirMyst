package css.out.file.SyS;

import css.out.file.FileApp;
import css.out.file.entity.dir;
import css.out.file.entity.file;
import css.out.file.enums.ROOT_PATH;
import org.junit.Test;

import java.util.Map;

import static css.out.file.entiset.GF.*;
import static css.out.file.handleB.HandleDISK.fullFillFAT;
import static css.out.file.handleB.HandleDISK.specifyFAT;
import static css.out.file.handleB.HandleFile.str2Path;
import static css.out.file.handleS.HandleDS.*;

public class DSTest {

    @Test
    public void DStest1_CRUD() {

        FileApp app = new FileApp();

//        app.kickDiskRoboot();
        //上上强度
        fullFillFAT(1); //FAT1满
//        fullFillFAT(2); //全满

        Map<Integer, Integer> map = Map.of(
                3, 80,
                80, 50,
                50, 70,
                70, 40,
                40, 90,
                90, 60,
                60, Null_Pointer
        );
        specifyFAT(map);


        //temp文件 手动构造写入磁盘
        file temp_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(0), "correct input");
        file temp_fileAltered = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + "Notebook of SpadeK", FILE_EXTEND.get(1), "I want to surpass humanity, with your blood!");
        dir temp_dir = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + DIR_NAME_DEFAULT, DIR_EXTEND.get(0));
        dir temp_dirAltered = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Notebooks of SpadeK", DIR_EXTEND.get(0));
//        Object temp_obj = "我是一个病毒对象";
//
//        System.out.println(temp_obj + "~我跑进来咯~");
//        System.out.println(getFATOrder());//康康FAT占用顺序

        //! CRUD

//        System.out.println(getFATOrder());//康康FAT占用顺序

        addContentDS(temp_file);

//        System.out.println(getFATOrder());//康康FAT占用顺序

        deleteContentDS(temp_file);
//
//        System.out.println(getFATOrder());//康康FAT占用顺序
//
//        addContext(temp_dir);
//
//        System.out.println(getFATOrder());//康康FAT占用顺序
//
//        deleteContentDS(temp_dir);
//
//        System.out.println(getFATOrder());//康康FAT占用顺序


        //这时候能看到BLOCK中还残留着垃圾内容, 但是FAT已经清空了; TXT的内容是全空
//        addContext(temp_obj);
//        deleteContentDS(temp_obj);

        //改
        addContentDS(temp_file);

        alterContentDS(temp_file, temp_fileAltered);

        selectContentDS(temp_fileAltered);

        app.state();

    }
}
