package css.out.file.entity;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.entiset.GF.*;
import static css.out.file.enums.FileDirTYPE.DIR;

/**
 * 虚拟节点, 用于存储文件系统结构
 * 核心仍然是FCB
 */
@Data
@Slf4j
public class node {

    /**
     * 节点内包含对应文件/目录的FCB
     */
    public FCB fcb;

    /**
     * 左孩子节点
     */
    public node left;

    /**
     * 右孩子节点
     */
    public node right;

    /**
     * 节点需要绑定对应的FCB
     *
     * @param fcb 对应文件/文件夹的FCB
     */
    public node(FCB fcb) {
        this.fcb = fcb;
        this.left = null;
        this.right = null;
    }

    /**
     * 根节点特殊处理, 传入根目录的权限
     * <p>不挂载到磁盘, 手动指定大小为0</p>
     *
     * @param auth 根目录权限
     */
    public node(String auth) {
        if (auth.equals(ROOT_AUTH)) {
            this.fcb = new FCB("/" + ":", ROOT_DIR_BLOCK_WHERE, DIR_EXTEND.get(0), DIR, DIR_LENGTH_DEFAULT);
        } else {
            log.error("非法的根目录创建!");
        }
    }


}
