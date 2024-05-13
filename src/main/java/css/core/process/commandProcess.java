package css.core.process;

import css.out.device.Device;
import css.out.device.DeviceManagement;
import css.out.file.api.toFrontApiList;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static css.core.process.ProcessScheduling.linkedList;


public class commandProcess {
    //bugfix : 提取公共注入bean方法规避反复注入问题
    static ApplicationContext context =
            new ClassPathXmlApplicationContext("spring-config.xml");
    static DeviceManagement deviceManagement = (DeviceManagement) context.getBean("deviceManagement");

    public static void commandExecution(String order) {
        String[] s = order.split(" ");
        switch (s[0]) {
            case "$add" -> {
                deviceManagement.devices.put(s[1], new Device(s[1]));
            }
            case "$remove" -> {
                deviceManagement.devices.remove(s[1]);
            }
            case "stop" -> {
                linkedList.get(s[1]).stop = true;
            }
            default -> {
                toFrontApiList.getFrontRequest(order);
            }
        }
    }
}
