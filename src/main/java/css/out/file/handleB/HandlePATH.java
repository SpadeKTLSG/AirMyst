package css.out.file.handleB;

import css.out.file.entity.FCB;
import css.out.file.entity.node;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static css.out.file.FileApp.fileSyS;
import static css.out.file.api.InteractApiList.alertUser;
import static css.out.file.handleB.HandleFile.getName;
import static css.out.file.handleB.HandleFile.getPathArray;


/**
 * I级 路径序列工具类 - CRUD
 */
@Slf4j
public abstract class HandlePATH {


    //! 1. 文件系统树形结构TR


    /**
     * 层次遍历TR
     *
     * @param root 文件系统树形结构根节点
     * @deprecated 不是很好看
     */
    public static String printTR(node root) {
        LinkedList<node> queue = new LinkedList<>();
        //使用Stringbuffer存储输出结果
        StringBuilder sb = new StringBuilder();

        queue.offer(root);
        while (!queue.isEmpty()) {
            node node = queue.poll();
            sb.append(node.fcb.pathName).append(" ");

            if (node.right != null) queue.offer(node.right); // 如果结点有兄弟结点，将其入队
            if (node.left != null) queue.offer(node.left); // 如果结点有孩子结点，将其入队
        }
        return sb.toString();
    }


    /**
     * 针对某一节点生成TR序列
     *
     * @param target 目标节点
     * @return 返回TR序列
     */
    public static String pathTR(node target) {
        //? 使用StringBuilder拼接从根目录root节点到target节点的路径
        StringBuilder sb = new StringBuilder();
        //在TR中查找node, 并将路径拼接到SB中
        boolean found = searchTRbyNode(fileSyS.tree.root.left, target, sb);


        return found ? sb.toString() : "未找到该节点" + target.fcb.pathName.split(":")[1] + "!";
    }


    /**
     * 通过树形结构搜索TR节点
     *
     * @param root   根节点
     * @param target 目标节点
     * @param sb     目录序列
     * @return 返回是否找到
     */
    public static boolean searchTRbyNode(node root, node target, StringBuilder sb) {
        if (root == null) {
            return false;
        }

        if (root == target) {
            sb.append(root.fcb.pathName.split(":")[1]);//将当前root指向的fcb的文件名拼接到sb中
            return true;
        }


        sb.append(root.fcb.pathName.split(":")[1]).append("->");


        if (searchTRbyNode(root.left, target, sb)) return true;

        sb.delete(sb.length() - root.fcb.pathName.split(":")[1].length() - 2, sb.length()); //回溯

        if (searchTRbyNode(root.right, target, sb)) return true;

        sb.delete(sb.length() - root.fcb.pathName.split(":")[1].length() - 2, sb.length()); //回溯

        sb.append(target);

        return false;
    }


    /**
     * 通过Order序列搜索TR节点
     *
     * @param root  根节点
     * @param dir   目录数组
     * @param index 目录数组的索引
     * @param sb    目录序列
     * @return 返回定位到的节点
     */
    static node searchNodebyOrder(node root, String[] dir, int index, StringBuilder sb) {
        //从根节点的左子树开始遍历, 每次遍历到一个节点, 就判断其是否是目录数组中的目录, 如果是, 就将tempnode指向其左子树, 如果不是, 就将tempnode指向其右子树
        //如果遍历到最后一个目录, 就将tempnode指向其左子树, 然后将其右子树指向新节点
        if (root == null) {
            return null;
        }

        if (getName(root.fcb).equals(dir[index])) { //比对当前节点的fcb的pathName切分后提取出的文件名和目录数组中的目录是否相同
            // 如果这是目录数组的最后一个元素, 定位到了, 则返回当前节点
            if (index == dir.length - 1) {
                sb.append(root.fcb.pathName.split(":")[1]).append("/"); //将当前root指向的fcb的文件名拼接到sb中
                return root;
            }

            // 否则，还得继续在左子树中搜索下一个目录
            else {
                sb.append(root.fcb.pathName.split(":")[1]).append("/"); //将当前root指向的fcb的文件名拼接到sb中
                return searchNodebyOrder(root.left, dir, index + 1, sb);
            }
        }

        // 在右子树中搜索
        return searchNodebyOrder(root.right, dir, index, sb);
    }


    /**
     * 查找给定fcb的对应节点的父节点(上一个文件层级
     *
     * @param fcb 给定的fcb
     * @return 返回父节点
     */
    public static node searchUpperNode(FCB fcb) {

        //1. 拿到FCB按照"目录 : 名称"切分为两块: 目录和名称
        String[] dir = getPathArray(fcb);
//        String name = getName(fcb);
        node temp_node = fileSyS.tree.root.left;      //创建游标指针tempnode去TR中跟踪序列, 指向根节点的左子树, 也就是第一个挂载到/的节点
        StringBuilder sb = new StringBuilder();     //创建SB存储目录序列


        //2. 遍历目录数组, 每次遍历到一个目录, 就判断其是否是tempnode的左子树, 如果是, 就将tempnode指向其左子树, 如果不是, 就将tempnode指向其右子树
        node dir_temp = searchNodebyOrder(temp_node, dir, 0, sb); //得到要新增的节点的父节点

        if (dir_temp == null) {
            log.warn("文件树层级关系错误, 请检查文件系统树形结构");
            alertUser("Ooops! 系统中找不到对应的目录");
            return null;
        }

        //System.out.println(sb); //展示搜索到的目录序列
        //System.out.println(dir_temp);//展示当前找到的插入目标文件夹
        return dir_temp;
    }


    /**
     * 查询TR节点的对应node
     *
     * @param fcb 给定的fcb
     * @return 返回对应的node
     */
    public static node selectTR2Node(FCB fcb) {
        //? 拿到FCB后, 通过String切分判断其位置(树上意义无意义),因此暂时直接调用pathTR返回类似order的路径

        //1. 拿到FCB按照"目录 : 名称"切分为两块: 目录和名称
        String[] dir = getPathArray(fcb);
        String name = getName(fcb);

        //将dir和name合并
        String[] dir_name = new String[dir.length + 1];
        System.arraycopy(dir, 0, dir_name, 0, dir.length);
        dir_name[dir.length] = name;


        node temp_node = fileSyS.tree.root.left;      //创建游标指针tempnode去TR中跟踪序列, 指向根节点的左子树, 也就是第一个挂载到/的节点
        StringBuilder sb = new StringBuilder();     //创建SB存储目录序列


        //2. 遍历目录数组, 每次遍历到一个目录, 就判断其是否是tempnode的左子树, 如果是, 就将tempnode指向其左子树, 如果不是, 就将tempnode指向其右子树
        node dir_temp = searchNodebyOrder(temp_node, dir_name, 0, sb); //得到要新增的节点

        if (dir_temp == null) {
            log.warn("文件树层级关系错误, 请检查文件系统树形结构");
            alertUser("Ooops! 系统中找不到对应的目录");
            return null;
        }

        return dir_temp;
    }


    //! 2. 路径管理器PM


    /**
     * PM中找pathName的键
     *
     * @param pathName 扩展名
     * @return 路径管理器中的键
     */
    public static Integer findKeyiPM(String pathName) {
        try {
            List<Integer> keys = fileSyS.pathManager.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(pathName))
                    .map(Map.Entry::getKey)
                    .toList();

            return keys.get(0);

        } catch (Exception e) {
            log.error("路径管理器中找不到对应的路径{}?!", pathName);
            throw new RuntimeException(e);
        }
    }


    //! 3. 扩展名管理器EM

    /**
     * EM定位ExtendName
     *
     * @param key 键
     * @return 扩展名
     */
    public static String selectEM(Integer key) {
        return fileSyS.extendManager.get(key);
    }

    /**
     * EM新增ExtendName
     *
     * @param extendName 扩展名
     * @return 返回扩展名管理器中的键
     */
    public static Integer bindEM(String extendName) {

        List<Integer> keys = fileSyS.extendManager.entrySet().stream()
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();

        // log.debug("当前extend中的空白位置: {}", keys.size());
        fileSyS.extendManager.put(keys.get(0), extendName);
        return keys.get(0);
    }


    /**
     * EM删除ExtendName
     *
     * @param extendName 扩展名
     * @deprecated 默认不提供删除扩展名方法
     */
    public static void deleteEM(String extendName) {
        Integer key = findKeyiEM(extendName);
        fileSyS.extendManager.put(key, "");
    }


    /**
     * EM修改ExtendName
     *
     * @param origin_exName 原扩展名
     * @param new_exName    新的扩展名
     * @return 返回新的扩展名
     * @deprecated 默认不提供修改扩展名方法
     */
    public static String alterEM(String origin_exName, String new_exName) {
        Integer key = findKeyiEM(origin_exName);
        fileSyS.extendManager.put(key, new_exName);
        return new_exName;
    }


    /**
     * EM中找ExtendName的键
     *
     * @param extendName 扩展名
     * @return 扩展名管理器中的键
     */
    public static Integer findKeyiEM(String extendName) {
        try {
            List<Integer> keys = fileSyS.extendManager.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(extendName))
                    .map(Map.Entry::getKey)
                    .toList();

            return keys.get(0);

        } catch (Exception e) {
            log.error("扩展名管理器中找不到对应的扩展名{}?!", extendName);
            throw new RuntimeException(e);
        }
    }


}
