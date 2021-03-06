package com.flink.streaming.web.common.util;

import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.model.dto.UserSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-12
 * @time 21:25
 */
public class UserSessionUtil {

    /**
     *根据cookie获取登陆信息
     * @author zhuhuipei
     * @date 2020-08-12
     * @time 19:06
     */
    public static UserSession userSession(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SystemConstants.COOKIE_NAME_SESSION_ID)) {
                return UserSession.toUserSession(Base64Coded.decode(cookie.getValue().getBytes()));
            }
        }

        return null;
    }
}
