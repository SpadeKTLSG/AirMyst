package css.out.file.entiset;

import java.util.List;

/**
 * InteractField 交互变量&信息定义
 * <p>用于从文件系统外部载入信息</p>
 */
public abstract class IF {
    /**
     * 用户新增扩展名序列
     * <p>预定义了两个ppt项, 后续增添时需要判断重复</p>
     */
    public static List<String> AddedEXTEND = List.of(".ppt", "pptx");
}
