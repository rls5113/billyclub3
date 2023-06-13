package com.billyclub.points.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class ServletUtility {
    public static String getSiteURL(HttpServletRequest request) {
        StringBuffer siteURL = request.getRequestURL().replace(0,4,"https");
        String url = siteURL.toString();
        return  url.replace(request.getServletPath(),"");
//        String siteURL = request.getRequestURL().toString();
//        return  siteURL.replace(request.getServletPath(),"");
    }
}
