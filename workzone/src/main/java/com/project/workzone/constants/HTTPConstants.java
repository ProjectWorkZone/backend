package com.project.workzone.constants;

public class HTTPConstants {
    public static final String PUBLIC_URL = "/public/**";
    public static final String SWAGGER_UI_URL = "/swagger-ui/**";
    public static final String SWAGGER_RESOURCES_URL = "/swagger-resources/";
    public static final String SWAGGER_RESOURCES_ALL_URL = "/swagger-resources/**";
    public static final String API_DOCS_URL = "/v3/api-docs";
    public static final String API_DOCS_ALL_URL = "/v3/api-docs/**";
    public static final String SIGN_IN_URL = "/api/v1/auth/sign-in";
    public static final String SIGN_UP_URL = "/api/v1/auth/sign-up";
    public static final String SIGN_OUT_URL = "/api/v1/auth/sign-out";
    public static final String REFRESH_TOKEN_URL = "/api/v1/auth/refresh";
    public static final String CONTENT_TYPE = "application/json";

    public static final String ACCESS_COOKIE = "access-token";
    public static final String REFRESH_COOKIE = "refresh-token";

    public static final String OK = "200";
    public static final String CREATED = "201";

    public static final String[] WHITE_LIST_URL = {
            PUBLIC_URL,
            SWAGGER_UI_URL,
            SWAGGER_RESOURCES_URL,
            SWAGGER_RESOURCES_ALL_URL,
            API_DOCS_URL,
            API_DOCS_ALL_URL,
            SIGN_UP_URL,
            SIGN_IN_URL
    };
}
