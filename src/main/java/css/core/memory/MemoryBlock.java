package css.core.memory;

import lombok.Data;

/**
 * 内存块
 */
@Data
public class MemoryBlock {

    /**
     * 内存块的内容, 简单设计为字符串
     */
    private String content;

    /**
     * 空块的默认内容 ---
     */
    public MemoryBlock() {
        this.content = "---";
    }

}
