package css.out.file.handleS;

import css.out.file.FileApp;
import css.out.file.entity.*;
import css.out.file.enums.FileDirTYPE;
import css.out.file.enums.ROOT_PATH;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static css.out.file.FileApp.fileSyS;
import static css.out.file.api.InteractApiList.alertUser;
import static css.out.file.entiset.GF.*;
import static css.out.file.entiset.IF.AddedEXTEND;
import static css.out.file.entiset.SFA.initialFileSys;
import static css.out.file.handleB.HandleBlock.read1Block2Bytes;
import static css.out.file.handleB.HandleDISK.getFATOrder;
import static css.out.file.handleB.HandleFile.bytes2Fcb_AppendPM;
import static css.out.file.handleB.HandleFile.str2Path;
import static css.out.file.handleB.HandlePATH.*;
import static css.out.file.handleB.HandleTXT.read1BlockiTXT;
import static css.out.file.utils.ByteUtil.byte2Str;

/**
 * 0级 文件系统管理工具类
 */
@Slf4j
public abstract class HandleFS {

    //! 0. 文件系统/


    /**
     * ?正常模式
     * <p>正常从磁盘完全刷新文件系统</p>
     */
    public static void normalRebootFile() {
        //需要从磁盘读取当前文件树信息, 在基础索引树的基础上重建
        //因为设置了一个块里只能有一个文件(整个/部分), 因此需要按照FAT的顺序遍历磁盘, 读取每个文件对象(file/dir)的字节流, 转换为对象, 挂载到树上


//        setDefaultPM();
        setDefaultEM();

        //?使用order序列遍历查找对应文件/文件夹的内容, 逐个添加到树上, 同时加到PM上
        List<Integer> orderList = getFATOrder();

        if (orderList != null && orderList.isEmpty()) {
            return;
        }

        for (Integer single : orderList) {

            //去除FAT和DEFAULT位置盘块
            if (Objects.equals(single, FAT1_DIR) || Objects.equals(single, FAT2_DIR) || Objects.equals(single, ROOT_DIR_BLOCK))
                continue;

            //读取出块切分为FCB_LENGTH和余下部分
            byte[] tempBytes = read1Block2Bytes(single);
            byte[] fcbBytes = new byte[FCB_BYTE_LENGTH];
            byte[] contentBytes = new byte[BLOCK_SIZE - FCB_BYTE_LENGTH];
            System.arraycopy(tempBytes, 0, fcbBytes, 0, FCB_BYTE_LENGTH);
            System.arraycopy(tempBytes, FCB_BYTE_LENGTH, contentBytes, 0, BLOCK_SIZE - FCB_BYTE_LENGTH);

            //提取出fcb和Contents
            FCB tempFCB = bytes2Fcb_AppendPM(fcbBytes); //这里有当前无法实现的重大BUG: 文件系统始终是JAVA对象,重启后无法复原. 只能进行转移
            String tempContent = byte2Str(contentBytes);

            //添加封装的对象到TR(FS)
            addTR(tempFCB);

        }

        log.debug("文件模块重读完成");
    }


    /**
     * ?覆盖模式
     * <p>直接用当前JAVA文件对象结构重建系统</p>
     * <p>根据当前Java对象的挂载关系操作</p>
     */
    public static void coverRebootFile() {
        //由于超纲, 未实现

        log.debug("文件模块覆盖完成");
    }


    /**
     * ?格式化模式
     * <p>重新格式化文件系统, 清空所有内容</p>
     *
     * @comment 你最好不要玩火...
     */
    public static void cleanRebootFile() {

        FileApp.fileSyS = initialFileSys();
        log.debug("文件模块格式化完成");
    }


    //! 1. 文件系统树形结构TR/


    /**
     * 获取初始TR
     */
    public static TREE initialTR() {
        TREE tree = new TREE();
        tree.name = FILE_TREE_NAME;
        tree.root = new node(ROOT_AUTH); //挂载根节点
        setDefaultTR(tree.root);
        return tree;
    }

    /**
     * 初始化TR
     * <p>挂载根目录到TR</p>
     *
     * @param root 根节点
     */
    public static void setDefaultTR(node root) {

        boolean isFirst = true;

        for (ROOT_PATH root_path : ROOT_PATH.values()) {

            if (isFirst) {
                isFirst = false;
                dir tempdir = new dir("/:" + root_path.getName(), ROOT_DIR_BLOCK);
                root.left = new node(tempdir.fcb); //挂载到根节点的左子树上

            } else {
                dir tempdir = new dir("/:" + root_path.getName(), ROOT_DIR_BLOCK);
                node tempnode = root.left;
                while (tempnode.right != null) {
                    tempnode = tempnode.right; //递归查找根节点的左子树的最后一个右孩子节点
                }
                tempnode.right = new node(tempdir.fcb);
            }
        }
    }


    /**
     * 新增TR节点
     *
     * @param fcb 新增对象的FCB
     */
    public static void addTR(FCB fcb) { //fcb或者是node皆可
        //? 拿到FCB后, 通过String切分判断其上级已存在node的位置, 封装到searchUpperNode方法中
        node dir_temp = searchUpperNode(fcb);

        if (dir_temp == null) {
            return;
        }

        //?新增逻辑: fcb/node组装为node, 挂载到孩子兄弟树TR上父节点的左孩子或者是左孩子的右兄弟上

        node input = new node(fcb); //直接将fcb封装为node, 左右子节点均为null

        if (dir_temp.left == null) {
            dir_temp.left = input; //如果父节点的左孩子为空, 直接挂载到左孩子上
        } else {
            node tempnode = dir_temp.left;
            while (tempnode.right != null) {
                tempnode = tempnode.right; //递归查找根节点的左子树的最后一个右孩子节点
            }
            tempnode.right = input;//将新节点挂载到最后一个右孩子节点的右兄弟上
        }

    }


    /**
     * 删除TR节点
     *
     * @param fcb 删除对象的FCB
     */
    public static void deleteTR(FCB fcb) {
        node parentNode = searchUpperNode(fcb); //得到要删除的节点的父节点(一定是目录)
        node targetNode = selectTR2Node(fcb);//得到要删除的节点本身

        if (parentNode == null) {
            return;
        }

        if (targetNode == null) {
            return;
        }

        if (Objects.equals(parentNode.fcb.getPathName(), "/:")) { //安全判断
            log.warn("根目录下的8个节点不能删除!");
            alertUser("小子, 你在玩火!");
            return;
        }

        // 如果要删除的节点是其父节点的左孩子
        if (parentNode.left == targetNode) {
            parentNode.left = targetNode.right; // 将父节点的左孩子指针指向要删除节点的右兄弟
        } else {
            // 如果要删除的节点是其兄弟节点的右兄弟
            node siblingNode = parentNode.left;
            while (siblingNode != null && siblingNode.right != targetNode) {
                siblingNode = siblingNode.right; // 找到要删除节点的左兄弟
            }
            if (siblingNode != null) {
                siblingNode.right = targetNode.right; // 将其兄弟节点的右兄弟指针指向要删除节点的右兄弟
            }
        }

        targetNode.left = null; // 清除要删除节点的左孩子指针
        targetNode.right = null; // 清除要删除节点的右兄弟指针

    }


    //修改TR节点
    public static void alterTR(FCB fcb1, FCB fcb2) {
        //? 拿到FCB1后, 通过String切分判断其位置, 定位到地点后执行孩子兄弟树的修改节点操作

        if (Objects.equals(fcb1.getPathName(), "/:")) { //安全判断
            log.warn("根目录下的8个节点不能删除!");
            alertUser("小子, 你在玩火!");
            return;
        }

        Objects.requireNonNull(selectTR2Node(fcb1)).fcb = fcb2;//直接替换内容

    }


    /**
     * 查询TR节点 -> 得到对应路径
     *
     * @param fcb 给定的fcb
     * @return 返回对应的路径
     * @deprecated
     */
    public static String selectTR2Path(FCB fcb) {
        return pathTR(selectTR2Node(fcb));
    }


    /**
     * 查询TR节点 -> 得到对应对象
     *
     * @param fcb 给定的fcb
     * @return 返回对应的文件/文件夹对象
     */
    public static Object selectTR2Content(FCB fcb) {
        FCB originalFcb = Objects.requireNonNull(selectTR2Node(fcb)).fcb;
        String allBlock = read1BlockiTXT(originalFcb.getStartBlock());
        String[] temp = allBlock.split(" ");
        StringBuilder sb = new StringBuilder();
        int lastMeaningfulIndex = 0;

        for (int i = 0; i < temp.length; i++)
            if (!temp[i].equals("0"))
                lastMeaningfulIndex = i;

        for (int i = FCB_BYTE_LENGTH; i < lastMeaningfulIndex + 1; i++)
            sb.append((char) Integer.parseInt(temp[i]));

        if (originalFcb.getTypeFlag() == FileDirTYPE.FILE)
            return new file(new FCB(originalFcb.getPathName(), originalFcb.getStartBlock(), originalFcb.getExtendName(), originalFcb.getTypeFlag(), originalFcb.getFileLength()), sb.toString());
        else
            return new dir(new FCB(originalFcb.getPathName(), originalFcb.getStartBlock(), originalFcb.getExtendName(), originalFcb.getTypeFlag(), originalFcb.getFileLength()));
    }

    //! 2. 路径管理器PM/


    /**
     * 获取初始PM
     */
    public static Map<Integer, String> initialPM() {
        Map<Integer, String> pm = new HashMap<>();
        for (int i = 0; i < 1000; i++) pm.put(i, "");
        pm.put(0, "/"); //根节点
        return pm;
    }


    /**
     * 初始化PM
     * <p>挂载根目录</p>
     *
     * @deprecated 已经通过磁盘模块初始化完成
     */
    public static void setDefaultPM() {
        fileSyS.pathManager = initialPM();
        for (ROOT_PATH root_path : ROOT_PATH.values())
            bindPM(str2Path(String.valueOf(root_path)));
    }


    /**
     * PM新增PathName
     *
     * @param pathName FCB的PathName
     * @comment 这样硬盘只需要存储对应的键即可
     */
    public static Integer bindPM(String pathName) {

        List<Integer> keys = fileSyS.pathManager.entrySet().stream() //将Map转换为Stream，过滤出值等于目标值的键值对，映射为键，收集为集合
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();

        //log.debug("当前路径映射器中的空白位置: {}", keys.size());
        fileSyS.pathManager.put(keys.get(0), pathName);
        return keys.get(0);
    }


    /**
     * PM删除PathName
     *
     * @param pathName FCB的PathName
     * @return 路径管理器中的键
     */
    public static Integer deletePM(String pathName) {
        //!合法性校验: 禁止删除根目录和根目录下的节点

        if (pathName.equals("/")) {
            log.warn("根目录不能删除!");
            return null;
        }

        for (ROOT_PATH root_path : ROOT_PATH.values()) {
            if (pathName.equals("/" + root_path.getName())) {
                log.warn("根目录下的8个节点不能删除!");
                return null;
            }
        }

        List<Integer> keys = fileSyS.pathManager.entrySet().stream() //将Map转换为Stream，过滤出值等于目标值的键值对，映射为键，收集为集合
                .filter(entry -> entry.getValue().equals(pathName))
                .map(Map.Entry::getKey)
                .toList();

        if (keys.isEmpty()) {
            log.warn("路径管理器中找不到对应的路径{}?!", pathName);
            return null;
        }

        fileSyS.pathManager.put(keys.get(0), "");
        return keys.get(0);
    }


    /**
     * PM修改PathName
     * <p>先删除再新增</p>
     *
     * @param pathName_or  原PathName
     * @param pathName_new 新PathName
     * @return 路径管理器中的键
     */
    public static Integer alterPM(String pathName_or, String pathName_new) {
        deletePM(pathName_or);
        return bindPM(pathName_new);
    }


    /**
     * PM定位PathName
     *
     * @param key 路径管理器中的键
     * @return FCB的PathName
     */
    public static String selectPM(Integer key) {
        return fileSyS.pathManager.get(key);
    }


    //! 3. 扩展名管理器EM/


    /**
     * 获取扩展名管理器
     * <p>初始化为0-100的Integer与空String键值对</p>
     */
    public static Map<Integer, String> initialEM() {
        Map<Integer, String> em = new HashMap<>();
        for (int i = 0; i < 100; i++) em.put(i, "");
        return em;
    }


    /**
     * 初始化EM
     * <p>按照文档和文件的扩展名进行设置</p>
     */
    public static void setDefaultEM() {
        fileSyS.extendManager = initialEM();
        try {//系统自带的直接指定提高效率
            int i = 0;
            for (; i < DIR_EXTEND.size(); i++) {
                fileSyS.extendManager.put(i, DIR_EXTEND.get(i));
            }
            for (int k = 0; k < FILE_EXTEND.size(); k++) {
                fileSyS.extendManager.put(i + k, FILE_EXTEND.get(k));
            }
            for (String s : AddedEXTEND) {
                bindEM(s); //同步用户添加的扩展名
            }
        } catch (Exception e) {
            log.warn("装不下了, extendManager被撑爆咯! {}", e.getMessage());
        }

    }

    //! 4.CRUD FS

    /**
     * 将对象加入文件模块
     *
     * @param A 文件/文件夹对象
     */
    public static void addContentFS(Object A) {
        //?自动绑定PM和EM, Path名字和扩展名都会在创建磁盘系统创建文件FCB字节对象时候自动绑定 (这叫解耦? SpadeK?)
        //?那么文件系统剩下的工作就是加入TR了
        //? TR新增节点
        if (A instanceof file file_temp) {
            log.debug("正在往文件树记录文件索引{}", file_temp.fcb.getPathName());
            addTR(file_temp.fcb);

        } else if (A instanceof dir dir_temp) {
            log.debug("正在往文件树记录文件夹索引{}", dir_temp.fcb.getPathName());
            addTR(dir_temp.fcb);

        } else {
            log.warn("不是文件也不是文件夹, 你是什么东西?{}", A);
            throw new RuntimeException("被投喂了奇怪的东西, 我当场趋势: " + A);
        }


    }


    /**
     * 将对象赶出文件模块
     *
     * @param A 文件/文件夹对象
     */
    public static void deleteContentFS(Object A) {

        //?需要解除PM的绑定(通过磁盘系统绑定)
//        deletePM(A);
        //?TR删除节点
//        deleteTR(A);

        if (A instanceof file file_temp) {
            log.debug("正在往文件树删除文件索引{}", file_temp.fcb.getPathName());
            deletePM(file_temp.fcb.getPathName());
            deleteTR(file_temp.fcb);

        } else if (A instanceof dir dir_temp) {
            log.debug("正在往文件树删除文件夹索引{}", dir_temp.fcb.getPathName());
            deletePM(dir_temp.fcb.getPathName());
            deleteTR(dir_temp.fcb);

        } else {
            log.warn("不是文件也不是文件夹, 你是什么东西?{}", A);
            throw new RuntimeException("被投喂了奇怪的东西, 我当场趋势: " + A);
        }

    }


    /**
     * 修改文件模块的一个对象
     *
     * @param A 被修改的对象
     * @param B 修改后的对象
     */
    public static void alterContentFS(Object A, Object B) {

        //?鉴权 * 2
        if (!(A instanceof file) & !(A instanceof dir)) {
            log.warn("不是文件也不是文件夹, 你是什么东西?{}", A);
            throw new RuntimeException("被投喂了奇怪的东西, 我当场趋势: " + A);
        }
        if (!(B instanceof file) & !(B instanceof dir)) {
            log.warn("不是文件也不是文件夹, 你是什么东西?{}", B);
            throw new RuntimeException("被投喂了奇怪的东西, 我当场趋势: " + A);
        }

        //?需要修改PM的绑定内容


        //?TR修改节点信息

        if (A instanceof file file_temp_A) {

            if (B instanceof file file_temp_B) {
                log.debug("正在往文件树修改文件索引{}", file_temp_A.fcb.getPathName());
                alterPM(file_temp_A.fcb.getPathName(), file_temp_B.fcb.getPathName());
                alterTR(file_temp_A.fcb, file_temp_B.fcb);

            } else {
                log.warn("文件/文件夹类型不匹配, 你是来干什么的?");
            }

        } else {
            if (B instanceof dir dir_temp_B) {
                dir dir_temp_A = (dir) A;
                log.debug("正在往文件树修改文件夹索引{}", dir_temp_A.fcb.getPathName());
                alterPM(dir_temp_A.fcb.getPathName(), dir_temp_B.fcb.getPathName());
                alterTR(dir_temp_A.fcb, dir_temp_B.fcb);

            } else {
                log.warn("文件/文件夹类型不匹配, 你是来干什么的?");
            }
        }
    }


    /**
     * 查找文件模块的一个对象信息, 返回具体new出来的对象
     *
     * @param A 文件/文件夹对象
     * @return 文件模块的TR生成的拷贝镜像对象
     */
    public static Object selectContentFS(Object A) {
        if (A instanceof file file_temp) {
            log.debug("正在往文件树查询文件索引{}", file_temp.fcb.getPathName());
            return selectTR2Content(file_temp.fcb);

        } else if (A instanceof dir dir_temp) {
            log.debug("正在往文件树查询文件夹索引{}", dir_temp.fcb.getPathName());
            return selectTR2Content(dir_temp.fcb);

        } else {
            log.warn("不是文件也不是文件夹, 你是什么东西?{}", A);
            throw new RuntimeException("被投喂了奇怪的东西, 我当场趋势: " + A);
        }
    }

}
