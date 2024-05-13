package css.out.file.SyS;

import css.out.file.FileApp;
import css.out.file.entity.dir;
import css.out.file.entity.file;
import css.out.file.enums.ROOT_PATH;
import org.junit.Test;

import static css.out.file.entiset.GF.*;
import static css.out.file.handleB.HandleDISK.fullFillFAT;
import static css.out.file.handleB.HandleFile.str2Path;
import static css.out.file.handleS.HandleFS.*;

public class FSTest {

    @Test
    public void FStest1_CRUD() {
        FileApp app = new FileApp();

//        app.kickDiskRoboot();
        //上上强度
        fullFillFAT(1); //FAT1满

        //temp文件 手动构造写入磁盘
        file temp_file = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(1), "correct input");
        file temp_fileAltered = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + "狂人日记", FILE_EXTEND.get(1), "I want to surpass humanity, with your blood!");
        dir temp_dir = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + DIR_NAME_DEFAULT, DIR_EXTEND.get(0));
        dir temp_dirAltered = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + "Notebooks of SpadeK", DIR_EXTEND.get(1));

        //! CRUD FS

        //? add
        file temp_file_head = new file(str2Path(String.valueOf(ROOT_PATH.tmp)) + str2Path(DIR_NAME_DEFAULT) + ':' + FILE_NAME_DEFAULT, FILE_EXTEND.get(2), "correct input");
        dir temp_dir_head = new dir(str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + DIR_NAME_DEFAULT, DIR_EXTEND.get(0));

//        addTR(temp_file_head.fcb);
        addContentFS(temp_dir_head);
        addContentFS(temp_file_head);
        addContentFS(temp_file);
        addContentFS(temp_fileAltered);
        addContentFS(temp_dir);
        addContentFS(temp_dirAltered);
        //手动bindPM(X)
        bindPM(temp_dir_head.fcb.pathName);
        bindPM(temp_file_head.fcb.pathName);
        bindPM(temp_file.fcb.pathName);
        bindPM(temp_fileAltered.fcb.pathName);
        bindPM(temp_dir.fcb.pathName);
        bindPM(temp_dirAltered.fcb.pathName);

        app.stateFile();
        //? delete
        System.out.println("\n\n\n天启之火!\n\n\n");
//        deleteContentFS(temp_fileAltered); //烧毁狂人日记.txt
//
//        app.stateFile();
//
//        //recover
//        System.out.println("\n\n\n神之一手!\n\n\n");
//        addContentFS(temp_fileAltered);
//        bindPM(temp_fileAltered.fcb.pathName);
//        app.stateFile();

        //deep test
        deleteContentFS(temp_dir_head);
        //recover
        addContentFS(temp_dir_head);
        bindPM(temp_dir_head.fcb.pathName);

        app.stateFile();

    }


}
