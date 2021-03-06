package com.herohuang.framework.helper;

import com.herohuang.framework.annotation.Action;
import com.herohuang.framework.bean.Handler;
import com.herohuang.framework.bean.Request;
import com.herohuang.framework.util.ArrayUtil;
import com.herohuang.framework.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controller helper
 *
 * @author Acheron
 * @date 25/07/2017
 * @since 1.0.0
 */
public final class ControllerHelper {
    public static final Logger logger = LoggerFactory.getLogger(ControllerHelper.class);
    /**
     * 存放请求与处理器的映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();

    static {
        // 获取所有controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            for (Class<?> controllerClass : controllerClassSet) {
                // 获取controller中的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    for (Method method : methods) {
                        // 判断当前method是否带有Action注解
                        if (method.isAnnotationPresent(Action.class)) {
                            // 从Action注解中获取url映射
                            Action action = method.getAnnotation(Action.class);
                            String requestPath = action.value();
                            String requestMethod = action.method().name();
                            //验证url映射规则
                            if (requestPath.matches("/\\w+") && requestMethod.matches("\\w+")) {
                                Request request = new Request(requestMethod, requestPath);
                                Handler handler = new Handler(controllerClass, method);
                                // 初始化Action map
                                ACTION_MAP.put(request, handler);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Get handler
     */
    public static Handler getHandler(String requestMethod, String repuestPath) {
        Request request = new Request(requestMethod, repuestPath);
        return ACTION_MAP.get(request);
    }

}
