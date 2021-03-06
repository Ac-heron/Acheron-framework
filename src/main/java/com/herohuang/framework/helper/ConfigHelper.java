package com.herohuang.framework.helper;

import com.herohuang.framework.ConfigConstant;
import com.herohuang.framework.util.PropsUtil;

import java.util.Properties;

/**
 * 属性文件助手类
 *
 * @author Acheron
 * @since 1.0.0
 */
public final class ConfigHelper {

    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    public static String getString(String config) {
        return PropsUtil.getString(CONFIG_PROPS, config);
    }

    public static boolean getBoolean(String str) {
        return PropsUtil.getBoolean(CONFIG_PROPS, str);
    }

    /**
     * Get jdbc driver
     */
    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_DRIVER);
    }

    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_URL);
    }

    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_USERNAME);
    }

    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.JDBC_PASSWORD);
    }

    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * jsp path default is /WEB-INF/view/
     *
     * @return
     */
    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_JSP_PATH, "/WEB-INF/view/");
    }

    public static String getAppStaticPath() {
        return PropsUtil.getString(CONFIG_PROPS, ConfigConstant.APP_STATIC_PATH, "/static/");
    }

    /**
     * 获取文件上传限制
     */
    public static int getAppUploadLimit() {
        return PropsUtil.getInt(CONFIG_PROPS, ConfigConstant.APP_UPLOAD_LIMIT);
    }


}
