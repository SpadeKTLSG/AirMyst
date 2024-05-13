package css.front.api;

import css.front.PopupDialog;
import lombok.extern.slf4j.Slf4j;

/**
 * 前端接收请求处理类
 *
 * @author SpadeK
 */
@Slf4j
public class request {


    //!接收请求, 并调用弹窗组件
    public static void msg2Front(String content) {
        log.debug("前端接收到信息: " + content);
        //调用弹窗组件
        PopupDialog dialog = new PopupDialog(null);
        dialog.setText(content);
        dialog.setVisible(true);
    }

}
