package css.out.file.entity;

import lombok.extern.slf4j.Slf4j;

import static css.out.file.utils.TreeUtil.printTree;

/**
 * 二叉树树形文件结构, 用于存储文件系统结构
 * <p>TREE特指文件系统树形结构</p>
 */
@Slf4j
public class TREE {

    //使用树的二叉树表示法, 根节点为/根目录, 其只有一个孩子节点, 为根目录下的第一个文件夹

    /**
     * 根节点不绑定FCB, 其下的8个孩子节点绑定FCB
     */
    public node root;

    /**
     * 树形文件结构名称
     */
    public String name;

    /**
     * 初始化文件系统树形结构(单例)
     */
    public TREE() {

    }


    @Override
    public String toString() {

        return name + "树形结构如下\n" + printTree(root, "");
//        return showGreatTree(root);//直接调用方法把树形结构打印出来
    }
}
