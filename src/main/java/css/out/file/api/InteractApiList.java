package css.out.file.api;

import lombok.extern.slf4j.Slf4j;

import static css.front.api.request.msg2Front;

/**
 * 通用api
 */
@Slf4j
public class InteractApiList {

    /**
     * 发送用户警告
     *
     * @param msg 警告信息
     * @return 警告信息
     */
    public static String alertUser(String msg) {
        log.warn("发送用户警告: " + msg);
        msg2Front(msg);
        return msg;
    }

    /**
     * 发送前端msg
     *
     * @param msg 前端msg
     */
    public static void msg(String msg) {
        String toFront = switch (msg) {
            case "0" -> "失败的请求调用";
            case "1" -> "失败的请求调用2";
            default -> msg; //不是0/1就是直接打印一段信息
        };
        log.debug("发送前端msg: " + toFront);
        msg2Front(toFront);
    }
}
