package com.herohuang.framework;

import com.herohuang.framework.bean.Data;
import com.herohuang.framework.bean.Handler;
import com.herohuang.framework.bean.Param;
import com.herohuang.framework.bean.View;
import com.herohuang.framework.helper.BeanHelper;
import com.herohuang.framework.helper.ConfigHelper;
import com.herohuang.framework.helper.ControllerHelper;
import com.herohuang.framework.helper.RequestHelper;
import com.herohuang.framework.helper.ServletHelper;
import com.herohuang.framework.helper.UploadHelper;
import com.herohuang.framework.util.JsonUtil;
import com.herohuang.framework.util.ReflectionUtil;
import com.herohuang.framework.util.StringUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 请求转发器
 *
 * @author Acheron
 * @date 25/07/2017
 * @since 1.0.0
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {


    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        // 初始化helper类
        HelperLoader.init();
        // 获取ServletContext对象，用于注册servlet
        ServletContext servletContext = servletConfig.getServletContext();
        // 注册处理jsp的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        // 注册处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppStaticPath() + "*");

        //上传对象的初始化
        UploadHelper.init(servletContext);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletHelper.init(req, resp);
        try {
            // 获取请求方法和路径
            String requestMethod = req.getMethod().toUpperCase();
            String requestPath = req.getPathInfo();

            // 过滤favicon.ico的请求
            if (requestPath.equals("/favicon.ico")) {
                return;
            }
            // 获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            if (handler != null) {
                // 获取Controller类和Bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                // 创建请求参数对象
                Param param;
                if (UploadHelper.isMultipart(req)) {
                    param = UploadHelper.createParam(req);
                } else {
                    param = RequestHelper.createParam(req);
                }

                Object result;

                // 调用Action方法
                Method actionMethod = handler.getActionMethod();

                // 判断param是否为空
                if (param.isEmpty()) {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                } else {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                }

                // 处理返回值
                if (result instanceof View) {
                    handleViewRequest((View) result, req, resp);
                } else if (result instanceof Data) {
                    handleDataResult((Data) result, resp);
                }

            }
        } finally {
            ServletHelper.destroy();
        }

    }

    private void handleViewRequest(View view, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                response.sendRedirect(request.getContextPath() + path);
            } else {
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
            }
        }
    }

    private void handleDataResult(Data data, HttpServletResponse response) throws IOException {
        Object model = data.getModel();
        if (model != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}
