package css.front;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Objects;

/**
 * 动态树状路径
 */
public class DynamicTreeExample {

    /**
     * 树状路径
     */
    JTree pathTree;

    /**
     * 树模型
     */
    private final DefaultTreeModel treeModel;

    /**
     * 构造方法
     */
    public DynamicTreeExample() {
        // 创建根节点
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        // 创建树
        treeModel = new DefaultTreeModel(root);
        pathTree = new JTree(treeModel);

        // 设置树的首选大小
        pathTree.setPreferredSize(new Dimension(560, 200));
    }


    /**
     * 更新树状路径
     *
     * @param pathArray 路径数组
     */
    public void updateTree(String[] pathArray) {
        // 获取根节点
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // 清空树
        root.removeAllChildren();
        treeModel.reload();

        // 动态添加节点
        DefaultMutableTreeNode currentNode = root;

        for (String pathPart : pathArray) {
            String[] subdirectories = pathPart.split("/");

            for (String subdirectory : subdirectories) {

                if (Objects.equals(subdirectory, "")) //? SK fix bug : 删除节点产生的空白
                    continue;

                //?智能判断是否需要更新层级
                boolean isExist = false;
                DefaultMutableTreeNode lastnode;
                DefaultMutableTreeNode p = root;

                for (int i = 0; i < p.getChildCount(); i++) {//树中查找当前节点
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getChildAt(i);

                    if (Objects.equals(node.getUserObject(), subdirectory)) { //找到了
                        currentNode = node;
                        isExist = true;
                        break;
                    }
                }

                if (isExist) { //如果是, 那么不要创建新的对应节点, 而是把公有部分去掉后接到对应节点下面
                    lastnode = currentNode;
                    //lastnode
                } else { //如果不是, 那么创建新的对应节点
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(subdirectory);
                    currentNode.add(newNode);
                }

            }
        }

        treeModel.reload();        // 刷新树
    }

    // 获取选定节点的路径
    public TreePath getSelectedPath() {
        return pathTree.getSelectionPath();
    }


}
