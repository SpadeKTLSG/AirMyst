package css.out.file.handleS;

import java.util.List;

import static css.out.file.entiset.GF.DISK_SIZE;
import static css.out.file.handleB.HandleDISK.getFATOrder;

public class monitor {

    //磁盘的使用情况（未占用的多少，占用的多少，系统占用的多少） : 直接查FATorder的list长度

    public static Double getDiskUsageAmount() {
        List<Integer> usedFATList = getFATOrder();
        if (usedFATList != null) {
            return ((usedFATList.size() + 1.0) / (double) DISK_SIZE);
        }
        return 0.0;
    }

}
