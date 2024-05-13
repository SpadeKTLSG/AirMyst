package css.out.file.utils;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类 - 废案
 *
 * @author SpadeK
 */
public abstract class ReflectUtil {


    /**
     * 用于获取指定类的可访问构造函数
     *
     * @param clazz          目标类
     * @param parameterTypes 构造函数参数类型
     * @param <T>            泛型
     * @return 构造函数
     * @throws NoSuchMethodException 无法获取构造函数时抛出
     */
    public static <T> Constructor<T> getAccessibleConstructor(Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        return ReflectionUtils.accessibleConstructor(clazz, parameterTypes);
    }

    /**
     * 用于获取指定类的指定方法
     *
     * @param clazz      目标类
     * @param name       方法名
     * @param paramTypes 方法参数类型
     * @return 方法对象
     */
    public static Method getMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        return ReflectionUtils.findMethod(clazz, name, paramTypes);
    }


    /**
     * 用于调用指定方法，并传入目标对象和参数
     *
     * @param method 方法对象
     * @param target 目标对象
     * @param args   参数
     * @return 方法返回值
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        return ReflectionUtils.invokeMethod(method, target, args);
    }


    /**
     * 用于获取指定类的指定字段
     *
     * @param clazz 目标类
     * @param name  字段名
     * @return 字段对象
     */
    public static Field getField(Class<?> clazz, String name) {
        return ReflectionUtils.findField(clazz, name);
    }


    /**
     * 用于获取指定字段的值
     *
     * @param field  字段对象
     * @param target 目标对象
     * @return 字段值
     */
    public static Object getFieldValue(Field field, Object target) {
        return ReflectionUtils.getField(field, target);
    }


    /**
     * 用于设置指定字段的值
     *
     * @param field  字段对象
     * @param target 目标对象
     * @param value  字段值
     */
    public static void setFieldValue(Field field, Object target, Object value) {
        ReflectionUtils.setField(field, target, value);
    }
}
