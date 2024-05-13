package css.out.file.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static css.out.file.handleB.HandleTXT.write1Str2TXT;


@Slf4j
public class UtilTest {

    @Test
    public void IOtest() {
        log.debug("测试工具类");

        //测试精确替换
        String path = "D:\\Workshop\\Codes\\JA\\ComputerSystemsSimulator\\src\\test\\resources\\common\\file\\temp.txt";
        write1Str2TXT("1234567890", path, 11);

        //测试能否覆盖写入 OK
        //向path中写入5次, 位置从0-1
        for (int i = 0; i < 5; i++) {
            write1Str2TXT("1234567890", path, i);
        }
        //由于没有结束指针, 因此不能采用覆盖方法进行块释放, 必须手动清空这一行?
        //但是一个block只占用一行, 因此一行内的所有元素都会被覆盖.
        //可行! 就是不要忘记了要全量复制磁盘然后修改完再放回去
    }
}
