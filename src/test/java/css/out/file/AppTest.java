package css.out.file;

import css.out.file.entity.dir;
import css.out.file.entity.file;
import css.out.file.enums.ROOT_PATH;
import org.junit.Test;

import static css.out.file.FileApp.*;
import static css.out.file.entiset.GF.*;
import static css.out.file.handleB.HandleFile.str2Path;

public class AppTest {

    @Test
    public void AppGreatTest() {
        FileApp app = new FileApp(); //开机
        app.state();
    }

    @Test
    public void AppCoworkTest() {
        FileApp app = new FileApp(); //开机
        app = new FileApp(ROOT_AUTH, 1); //格式化磁盘, 去除磁盘污染
        app.state();
        app = new FileApp(ROOT_AUTH, 2); //覆盖磁盘, 同步状态
        app.state();
    }


    @Test
    public void TempTest() {
        FileApp app = new FileApp(); //开机
        app = new FileApp(ROOT_AUTH, 3);//摧毁
        app.state();
    }


    @Test
    public void CRUDTest() {
        FileApp app = new FileApp(); //开机

//        app.kickDiskRoboot();
//        app.coverDiskRoboot();

//        fullFillFAT(1); //FAT1满

        //Objects - 按照顺序创建
        //?Dirs
        dir temp_dir_head1 = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + DIR_NAME_DEFAULT, DIR_EXTEND.get(0));
        dir temp_dir_head2 = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Headlines", DIR_EXTEND.get(0));
        dir temp_dir_head3 = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Notebooks of SpadeK", DIR_EXTEND.get(1));


        //?Files
        file temp_file_head2 = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(2), "correct input");
        file temp_fileAltered = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + "狂人日记", FILE_EXTEND.get(1), "I want to surpass humanity, with your blood!");
        file temp_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(1), "correct input");

        //add
/*        System.out.println("\n\n\n女娲造人!\n\n\n");
        addContent(temp_dir_head1);
        addContent(temp_dir_head2);
        addContent(temp_dir_head3);
        addContent(temp_file_head2);
        addContent(temp_fileAltered);
        addContent(temp_file);
        app.state();*/

        //delete
/*        System.out.println("\n\n\n天启之火!\n\n\n");
        deleteContent(temp_dir_head1);
        app.state();*/

        //alter
        addContent(temp_dir_head1);
        alterContent(temp_dir_head1, temp_dir_head3);
        app.state();

        //select
        String info = (String) selectContent(temp_dir_head3);
        System.out.println(info);
    }


    @Test
    public void AppendTest() {
        FileApp app = new FileApp(); //开机
//        app.kickDiskRoboot();
//        app.coverDiskRoboot();


        //Objects - 按照顺序创建
        //?Dirs
        dir temp_dir_head1 = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + DIR_NAME_DEFAULT, DIR_EXTEND.get(0));
        dir temp_dir_head2 = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Headlines", DIR_EXTEND.get(0));
        dir temp_dir_head3 = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Notebooks of SpadeK", DIR_EXTEND.get(1));


        //?Files
        file temp_file_head2 = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(2), "correct input");
        file temp_fileAltered = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + "狂人日记", FILE_EXTEND.get(1), "I want to surpass humanity, with your blood!");
        file temp_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(1), "correct input");


        app.state();

        cleanRecycleBin();

        app.state();
    }
}
