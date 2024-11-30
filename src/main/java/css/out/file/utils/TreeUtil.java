package css.out.file.utils;


import css.out.file.entity.node;
import lombok.extern.slf4j.Slf4j;

import static css.out.file.handleB.HandleFile.getName;

/**
 * 处理树结构的工具类
 *
 * @author SpadeK
 */
@Slf4j
public abstract class TreeUtil {

    /**
     * 获得树的层数
     *
     * @param root 根节点
     * @return 树的层数
     */
    public static int getTreeDepth(node root) {
        return root == null ? 0 : (1 + Math.max(getTreeDepth(root.left), getTreeDepth(root.right)));
    }

    /**
     * 将树结构写入二维数组
     *
     * @param currNode    当前节点
     * @param rowIndex    行索引
     * @param columnIndex 列索引
     * @param res         二维数组
     * @param treeDepth   树的深度
     */
    private static void writeArray(node currNode, int rowIndex, int columnIndex, String[][] res, int treeDepth) {

        if (currNode == null) return;
        // 先将当前节点保存到二维数组中
        String temp = currNode.fcb.pathName;
        res[rowIndex][columnIndex] = temp;

        int currLevel = ((rowIndex + 1) / 2); // 计算当前位于树的第几层

        if (currLevel == treeDepth) return;

        // 计算当前行到下一行，每个元素之间的间隔（下一行的列索引与当前元素的列索引之间的间隔）
        int gap = treeDepth - currLevel - 1;

        if (currNode.left != null) {// 对左儿子进行判断，若有左儿子，则记录相应的"/"与左儿子的值
            res[rowIndex + 1][columnIndex - gap] = "|";
            writeArray(currNode.left, rowIndex + 2, columnIndex - gap, res, treeDepth);
        }

        if (currNode.right != null) { // 对右儿子进行判断，若有右儿子，则记录相应的"\"与右儿子的值
            res[rowIndex + 1][columnIndex + gap] = "|";
            writeArray(currNode.right, rowIndex + 2, columnIndex + gap, res, treeDepth);
        }
    }

    /**
     * 自定义打印基础树结构, 没有区分文件/文件夹, 一视同仁
     *
     * @param root   根节点
     * @param indent 初始符号
     * @return 树结构
     */
    public static String printTree(node root, String indent) {
        if (root == null) {
            return "";
        }

        // 添加当前节点的信息

        return indent + getName(root.fcb) + root.fcb.getExtendName() + "\n" +

                // 递归添加左子树（孩子）的信息
                printTree(root.left, indent + "\t") +

                // 递归添加右子树（兄弟）的信息
                printTree(root.right, indent);
    }

    /**
     * 美观(?)展示文件系统树形结构
     *
     * @param root 根节点
     */
    @Deprecated
    public static String showGreatTree(node root) {
        log.debug("美观展示文件系统树形结构: ");
        if (root == null) log.warn("纳尼? 情报是假的?");
        int treeDepth = getTreeDepth(root);

        // 最后一行的宽度为2的（n - 1）次方乘3，再加1作为整个二维数组的宽度
        int arrayHeight = treeDepth * 2 - 1;
        int arrayWidth = (2 << (treeDepth - 2)) * 3 + 1;

        String[][] res = new String[arrayHeight][arrayWidth];        // 用一个字符串数组来存储每个位置应显示的元素

        for (int i = 0; i < arrayHeight; i++) {// 初始化
            for (int j = 0; j < arrayWidth; j++) {
                res[i][j] = "";
            }
        }

        // 从根节点开始，递归处理整个树
        writeArray(root, 0, arrayWidth / 2, res, treeDepth);
        StringBuilder all = new StringBuilder();
        for (String[] line : res) {// 此时，已经将所有需要显示的元素储存到了二维数组中，将其拼接并打印即可
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i++) {
                sb.append(line[i]);
                if (line[i].length() > 1 && i <= line.length - 1) {
                    i += line[i].length() > 4 ? 2 : line[i].length() - 1;
                }
            }

            all.append(sb).append("\n");
        }
        return all.toString();
    }
}
