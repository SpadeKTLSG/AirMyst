package css.out.file.entity;

import css.out.file.enums.FileDirTYPE;
import css.out.file.enums.ROOT_PATH;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.api.InteractApiList.alertUser;
import static css.out.file.entiset.GF.*;
import static css.out.file.enums.FileDirTYPE.DIR;
import static css.out.file.enums.FileDirTYPE.FILE;
import static css.out.file.handleB.HandleFile.str2Path;

/**
 * FCB 文件控制块
 * 8B绑定文件/目录HEAD, 用于描述文件的基本信息
 */
@Slf4j
@Data
@AllArgsConstructor
@Builder
public class FCB {

    /**
     * !目录名(/分割)+ (:) +文件名(不包含:)
     * <p>从/(根目录)唯一查找到该文件</p>
     * <p>需要被加载到PathManager中</p>
     * <p>e.g.</p>
     * <p>/home/114514:     小本本.txt</p>
     * <p>/home/114514:     上课文件夹</p>
     * <p>/home:            114514</p>
     */
    public String pathName;

    /**
     * 起始盘块号(对应块号)
     * <p>很抱歉, 为了项目进度锁定了一个文件只能占一个块</p>
     * <p>3 <= startBlock <= 127</p>
     */
    public Integer startBlock;

    /**
     * 扩展名(目录为空)
     *
     * <p>需要被加载到扩展名映射表中</p>
     */
    public String extendName;

    /**
     * 文件目录标识
     * <p> -> FILE / DIR</p>
     * <p>需要被加载到标识映射表中</p>
     */
    public FileDirTYPE typeFlag;

    /**
     * 文件长度(目录为空)
     * <p>单位:字节</p>
     * <p>用工具类计算占用的块数</p>
     */
    public Integer fileLength;


    /**
     * 指定目录和磁盘块手动新建文件/文件夹构造
     * <p>还需要处理的属性: 扩展名 + 长度</p>
     *
     * @param pathName   目录名:文件名
     * @param startBlock 起始盘块号
     * @param typeFlag   文件or目录标识
     */
    public FCB(String pathName, Integer startBlock, FileDirTYPE typeFlag) {
        if (typeFlag == DIR) { //目录

            this.pathName = pathName;
            this.startBlock = startBlock;
            //autofill
            this.typeFlag = DIR;
            this.extendName = DIR_EXTEND.get(0);
            this.fileLength = FCB_BYTE_LENGTH + DIR_LENGTH_DEFAULT;

        } else if (typeFlag == FILE) { //文件

            this.pathName = pathName;
            this.startBlock = startBlock;
            //autofill
            this.typeFlag = FILE;
            this.extendName = FILE_EXTEND.get(0);
            this.fileLength = FCB_BYTE_LENGTH + FILE_LENGTH_DEFAULT;

        } else { //出错
            alertUser("FCB构造失败, 传递flag: " + typeFlag + " 错误");
            log.error("FCB构造失败, 传递flag: {} 错误", typeFlag);
        }

    }

    /**
     * DTO类型FCB构造, 前端传递使用
     * <p>还需要处理的属性: 盘块位置 + 长度</p>
     *
     * @param pathName   目录名:文件名
     * @param extendName 扩展名
     * @param typeFlag   文件or目录标识
     */
    public FCB(String pathName, String extendName, FileDirTYPE typeFlag) {
        if (typeFlag == DIR) { //目录

            this.pathName = pathName;
            this.extendName = extendName;
            //autofill
            this.startBlock = Null_Pointer;
            this.typeFlag = DIR;
            this.fileLength = FCB_BYTE_LENGTH + DIR_LENGTH_DEFAULT;

        } else if (typeFlag == FILE) { //文件

            this.pathName = pathName;
            this.extendName = extendName;
            //autofill
            this.startBlock = Null_Pointer;
            this.typeFlag = FILE;
            this.fileLength = FCB_BYTE_LENGTH + FILE_LENGTH_DEFAULT;

        } else { //出错
            alertUser("FCB构造失败, 传递flag: " + typeFlag + " 错误");
            log.error("FCB构造失败, 传递flag: {} 错误", typeFlag);
        }

    }

    /**
     * 指定目录(用户友好型)新建文件/文件夹构造
     *
     * @param pathName 目录名:文件名
     * @param typeFlag 文件or目录标识
     */
    public FCB(String pathName, FileDirTYPE typeFlag) {

        if (typeFlag == DIR) { //目录

            this.pathName = pathName;
            //autofill
            this.startBlock = Null_Pointer;
            this.extendName = DIR_EXTEND.get(0);
            this.typeFlag = DIR;
            this.fileLength = FCB_BYTE_LENGTH + DIR_LENGTH_DEFAULT;

        } else if (typeFlag == FILE) { //文件

            this.pathName = pathName;
            //autofill
            this.startBlock = Null_Pointer;
            this.extendName = FILE_EXTEND.get(0);
            this.typeFlag = FILE;
            this.fileLength = FCB_BYTE_LENGTH + FILE_LENGTH_DEFAULT;

        } else { //出错
            alertUser("FCB构造失败, 传递flag: " + typeFlag + " 错误");
            log.error("FCB构造失败, 传递flag: {} 错误", typeFlag);
        }
    }

    /**
     * 指定FCB类型快速构建无挂载的对象(中间操作)
     *
     * @param typeFlag 文件or目录标识
     */
    public FCB(FileDirTYPE typeFlag) {
        log.debug("正在构建一个空白文件/文件夹类型FCB");
        if (typeFlag == DIR) { //目录

            //autofill
            this.pathName = str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + DIR_NAME_DEFAULT;
            this.startBlock = Null_Pointer;
            this.extendName = DIR_EXTEND.get(0);
            this.typeFlag = DIR;
            this.fileLength = FCB_BYTE_LENGTH + DIR_LENGTH_DEFAULT;

        } else if (typeFlag == FILE) { //文件

            //autofill
            this.pathName = str2Path(String.valueOf(ROOT_PATH.tmp)) + ':' + FILE_NAME_DEFAULT;
            this.startBlock = Null_Pointer;
            this.extendName = FILE_EXTEND.get(0);
            this.typeFlag = FILE;
            this.fileLength = FCB_BYTE_LENGTH + FILE_LENGTH_DEFAULT;

        } else { //出错
            alertUser("FCB构造失败, 传递flag: " + typeFlag + " 错误");
            log.error("FCB构造失败, 传递flag: {} 错误", typeFlag);
        }
    }


    /**
     * 空白FCB构造(中间操作)
     */
    public FCB() {
//        log.debug("正在构建一个空白FCB");
    }


    /**
     * 根据是DIR还是FILE来构造FCB的toString方法
     *
     * @return FCB信息
     */
    @Override
    public String toString() {
        if (typeFlag == DIR) {
            return "FCB{" +
                    "pathName='" + pathName + '\'' +
                    ", startBlock=" + startBlock +
                    ", typeFlag=" + typeFlag +
                    ", fileLength=" + fileLength +
                    '}';
        } else {
            return "FCB{" +
                    "pathName='" + pathName + '\'' +
                    ", startBlock=" + startBlock +
                    ", extendName='" + extendName + '\'' +
                    ", typeFlag=" + typeFlag +
                    ", fileLength=" + fileLength +
                    '}';
        }
    }


}

