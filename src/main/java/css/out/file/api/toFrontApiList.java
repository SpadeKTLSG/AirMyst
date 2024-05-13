package css.out.file.api;

import css.out.file.entity.dir;
import css.out.file.entity.file;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static css.out.file.FileApp.*;
import static css.out.file.api.InteractApiList.alertUser;
import static css.out.file.api.InteractApiList.msg;
import static css.out.file.api.toProcessApiList.handleCommon;
import static css.out.file.api.toProcessApiList.isExeFile;
import static css.out.file.entiset.GF.*;
import static css.out.file.handleB.HandleDISK.mergeFATs;

/**
 * 用户命令接口Api
 */
@Slf4j
public class toFrontApiList {

    //! 1.信息传递


    /**
     * 传递路径给前端
     * <p>[/, /:home, /:app, /:tmp, /:conf,.....]</p>
     *
     * @return String[] pathList
     */
    public static String[] givePath2Front() {
        //?foreach 读取PM内的项目, 打包到String[]

        String[] pathList = new String[fileSyS.pathManager.size()];

        //从PM中选取所有非""的项目, 封装到String[]中, 传递给Front
        String[] finalPathList = pathList;
        fileSyS.pathManager.forEach((k, v) -> {
            if (!v.equals(" ")) finalPathList[Integer.parseInt(String.valueOf(k))] = v;
        });


        pathList = Arrays.stream(pathList)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        //?刷洗符合要求
        for (int i = 0; i < pathList.length; i++) {
            String e = pathList[i];
            if (e.equals("/")) {
                pathList[i] = "";
            } else if (e.startsWith("/")) {
                pathList[i] = e.substring(1);
            }
            if (e.contains(":")) {
                if (Objects.equals(e.split((":"))[0], "/"))
                    pathList[i] = e.replace(":", "");
                else {
                    pathList[i] = e.replace(":", "/");
                }
            }
        }

        //刷洗空值
        pathList = Arrays.stream(pathList)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);


        return pathList;
    }


    /**
     * 传递磁盘占用情况给前端
     *
     * @return DTO FAT
     */
    public static List<Integer> giveBlockStatus2Front() {
        List<Integer> greatFAT = mergeFATs();
        //刷洗greatFAT, 0: 空闲 1:占用 3: 系统-> DTO FAT
        //?刷洗符合要求
        for (int i = 0; i < greatFAT.size(); i++) {
            Integer e = greatFAT.get(i);
            if (e.equals(Null_Pointer)) {
                greatFAT.set(i, 0);
            } else {
                greatFAT.set(i, 1);
            }
        }
        //设置系统占用为3
        greatFAT.set(0, 3);
        greatFAT.set(1, 3);
        greatFAT.set(2, 3);
        return greatFAT;
    }

    //! 2. 请求处理

    /**
     * 从前端接收完整请求
     *
     * @param order 前端完整请求
     */
    public static void getFrontRequest(String order) {
        isExeFile = 0; //复位可执行标志位
        handleCommon(handleOrder(order));
    }


    /**
     * 处理前端请求
     *
     * @param allOrder 完整前端请求
     * @return 返回处理后的对象用于进程展示
     */
    public static Object handleOrder(String allOrder) {

        String[] orderList = allOrder.split(" ");
        int order_size = orderList.length;
        String order = null, allName = null, path1 = null, path2 = null;

        switch (order_size) {
            case 1, 2 -> {
                alertUser("找不到对应的命令");
                return null;
            }
            case 3 -> { //标准三个参数 -> order | 文件全名 | path1(ELSE)
                order = orderList[0];
                allName = orderList[1];
                path1 = orderList[2];
            }
            case 4 -> { //标准四个参数 -> order | 文件全名 | path1 | path2(ELSE)
                order = orderList[0];
                allName = orderList[1];
                path1 = orderList[2];
                path2 = orderList[3];
            }
            default -> {
                log.warn("命令错误");
                alertUser("找不到对应的命令");
            }
        }

        if (order == null | allName == null | path1 == null) {
            alertUser("找不到对应的命令");
            return null;
        }

        return doFunction(order, allName, path1, path2);          //?返回传递进程对象信息
    }


    /**
     * 执行对应命令, 动态赋值
     *
     * @param order   命令
     * @param allName 文件全名
     * @param path    文件路径
     * @param subPath 子路径
     * @return 返回工作对象给进程
     */
    public static Object doFunction(String order, String allName, String path, String subPath) {
        switch (order) { //根据order调用对应的方法, 根据命令的不同传递参数
            case "create" -> {
                return createOrder(packageObjectfrFront(order, allName, path));
            }
            case "copy" -> {
                return copyOrder(selectObjectfrFront(order, allName, path), subPath);
            }
            case "delete" -> {
                return deleteOrder(selectObjectfrFront(order, allName, path));
            }
            case "move" -> {
                return moveOrder(selectObjectfrFront(order, allName, path), subPath);
            }
            case "type" -> {
                return typeOrder(selectObjectfrFront(order, allName, path));
            }
            case "change" -> {
                return changeOrder(selectObjectfrFront(order, allName, path), subPath);
            }
            case "makdir" -> {
                return makdirOrder(packageObjectfrFront(order, allName, path));
            }
            case "chadir" -> {
                return chadirOrder(selectObjectfrFront(order, allName, path), subPath);
            }
            case "deldir" -> {
                return deldirOrder(selectObjectfrFront(order, allName, path));
            }
            case "run" -> {
                return runOrder(selectObjectfrFront(order, allName, path));
            }
            case "edit" -> {
                return editOrder(selectObjectfrFront(order, allName, path), subPath); //真正的用户修改内容指令
            }
            default -> {
                log.error("命令错误");
                alertUser("找不到对应的命令");
                return null;
            }
        }
    }


    //? 两种不同的对象处理方式, 一种是定位到现有对象, 一种是制作新对象

    /**
     * 定位到现有对象完成DTO传递
     *
     * @param order   操作指令
     * @param allName 文件全名
     * @param path    文件路径
     * @return 打包对象
     */
    public static Object selectObjectfrFront(String order, String allName, String path) {//定位到现有对象, 需要根据操作order判断文件还是文件夹
        try {
            if (order.equals("create") | order.equals("copy") | order.equals("delete") | order.equals("move") | order.equals("type") | order.equals("change") | order.equals("run") | order.equals("edit")) {
                String pathName = path + ':' + allName.split("\\.")[0];
                String extendName = '.' + allName.split("\\.")[1];

                file temp_file = new file(pathName, extendName, "");
                return selectContent(temp_file);

            } else if (order.equals("makdir") | order.equals("chadir") | order.equals("deldir")) {
                String pathName = path + ':' + allName.split("\\.")[0];
                String extendName = ".";

                dir temp_dir = new dir(pathName, extendName);
                return selectContent(temp_dir);

            } else {
                alertUser("命令语法错误!");
                return null;
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            alertUser("命令语法错误!");
            return null;
        } catch (Exception e) {
            alertUser("命令语法错误!");
            throw new RuntimeException(e);
        }

    }

    /**
     * 制作新对象完成DTO传递
     *
     * @param order   操作指令
     * @param allName 文件全名
     * @param path    文件路径
     * @return 新对象
     */
    public static Object packageObjectfrFront(String order, String allName, String path) {//制作新对象对象, 需要根据操作order判断文件还是文件夹
        if (order.equals("create") | order.equals("copy") | order.equals("delete") | order.equals("move") | order.equals("type") | order.equals("change") | order.equals("run") | order.equals("edit")) {
            file temp_file = new file(path + ':' + allName.split("\\.")[0], '.' + allName.split("\\.")[1], "");
            addContent(temp_file);
            return selectContent(temp_file);

        } else if (order.equals("makdir") | order.equals("chadir") | order.equals("deldir")) {
            dir temp_dir = new dir(path + ':' + allName.split("\\.")[0], "");
            addContent(temp_dir);
            return selectContent(temp_dir);

        } else {
            alertUser("命令语法错误!");
            return null;
        }
    }

    //! 以下是具体的命令实现, 每个方法注释后面标记了前端传递参数的数量 - 3 / 4, 以及对应的参数类型

    /**
     * 创建文件
     * <p>create XXX.XXX /tmp</p>
     *
     * @param object 源文件对象
     * @return 返回工作对象给进程
     */
    public static Object createOrder(Object object) {
        return object;
    }

    /**
     * 创建文件夹
     * <p>makdir XXX.XXX /tmp </p>
     *
     * @param object 文件夹对象
     */
    public static Object makdirOrder(Object object) {
        return object;
    }


    /**
     * 复制文件 - 4
     * <p>copy XXXA.XXX /tmp /home</p>
     *
     * @param A       源文件对象
     * @param subPath 目标位置
     */
    public static Object copyOrder(Object A, String subPath) {
        if (A == null) return null;
        if (subPath == null) return null;
        file or_file = (file) A;
        file B = new file(subPath + ':' + or_file.fcb.pathName.split(":")[1], or_file.fcb.getExtendName(), or_file.getContent());
        addContent(B);
        return B;
    }


    /**
     * 删除文件
     * <p>delete XXX.XXX /tmp</p>
     *
     * @param object 文件对象
     */
    public static Object deleteOrder(Object object) {
        if (object == null) return null;
        deleteContent(object);
        return object;
    }


    /**
     * 删除文件夹
     * <p>deldir XXX /tmp</p>
     *
     * @param object 文件夹对象
     */
    public static Object deldirOrder(Object object) {
        if (object == null) return null;
        deleteContent(object);
        return object;
    }


    /**
     * 移动文件
     * <p>move XXX.XXX /tmp /home</p>
     *
     * @param A 源文件对象
     */
    public static Object moveOrder(Object A, String subPath) {
        if (A == null) return null;
        if (subPath == null) return null;
        file or_file = (file) A;
        file B = new file(subPath + ':' + or_file.fcb.pathName.split(":")[1], or_file.fcb.getExtendName(), or_file.getContent());
        alterContent(A, B);
        return B;
    }


    /**
     * 展示文件, 使用msg传递到前端
     * <p>type XXX.XXX /tmp</p>
     *
     * @param object 文件对象
     */
    public static Object typeOrder(Object object) {
        if (object == null) return null;
        msg(((file) object).getContent());
        return object;
    }


    /**
     * 修改文件名称
     * <p>change XXX.XXX /tmp YYY</p>
     *
     * @param A       文件对象
     * @param newName 新名称
     */
    public static Object changeOrder(Object A, String newName) {
        if (A == null) return null;
        file or_file = (file) A;
        if (newName == null) newName = FILE_NAME_DEFAULT; //容错判定
        file B = new file(or_file.fcb.pathName.split(":")[0] + ':' + newName, or_file.fcb.getExtendName(), or_file.getContent());
        alterContent(A, B);
        return B;
    }


    /**
     * 修改文件夹名称
     * <p>chadir XXX.XXX /tmp YYY</p>
     */
    public static Object chadirOrder(Object A, String newName) {
        if (A == null) return null;
        dir or_file = (dir) A;
        if (newName == null) newName = DIR_NAME_DEFAULT; //容错判定
        dir B = new dir(or_file.fcb.pathName.split(":")[0] + ':' + newName, or_file.fcb.getExtendName());
        alterContent(A, B);
        return B;
    }


    /**
     * 运行可执行文件
     * <p>exefile XXX.XXX /tmp</p>
     *
     * @param object 文件对象
     * @return 返回工作对象给进程
     */
    public static Object runOrder(Object object) {
        if (object == null) return null;
        //判断是否符合命名规范; #开头
        file temp_file = (file) object;
        if (!temp_file.getFcb().pathName.split(":")[1].startsWith("#")) {
            alertUser("不是可执行文件");
            return null;
        }
        isExeFile = 1;//标记为可执行文件
        return object;
    }


    /**
     * 修改文件内容
     * <p>edit XXX.XXX asdasdasdasd</p>
     *
     * @return 返回工作对象给进程
     */
    public static Object editOrder(Object A, String newContent) {
        if (A == null) return null;
        if (newContent == null) newContent = ""; //容错判定
        file or_file = (file) A;
        file B = new file(or_file.fcb.pathName.split(":")[0] + ':' + or_file.fcb.pathName.split(":")[1], or_file.fcb.getExtendName(), newContent);
        alterContent(A, B);

        return B;
    }


    /**
     * 嵌套删除文件夹(以及下面的所有对象)
     * <p>rmall XXX /tmp</p>
     *
     * @param object 文件夹对象
     * @deprecated DLC内容 这里不建议使用嵌套回收, 因为我仍然使用的是普通的删除逻辑, 但是加上了一个类似GC的玩意在我的FAT Manager里面
     */
    public static void rmallOrder(Object object) {
        //工期不够, 不做了
    }

}
