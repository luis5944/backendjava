package com.luisfn.backendjava.security;

import com.luisfn.backendjava.SpringApplicationContext;

public class SecurityConstants {
    //El tiempo que el JWT es valido
    public static final long EXPIRATION_DATE = 864000000;//10 dias
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";

    public static String getTokenSecret() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
