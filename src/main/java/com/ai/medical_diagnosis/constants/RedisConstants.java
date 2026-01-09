package com.ai.medical_diagnosis.constants;

public class RedisConstants {
    private static final String PROJECT_NAME = "medical_diagnosis";

    public static final String LOGIN_USER_KEY = PROJECT_NAME + ":login:user:token:";
    public static final String LOGIN_ADMIN_KEY = PROJECT_NAME + ":login:admin:token:";

    public static final String REDIS_VECTOR_KEY = PROJECT_NAME + "vector:";
    // 分钟
    public static final Long LOGIN_USER_TTL = 60*24*3L;
    public static final Long LOGIN_ADMIN_TTL = 60L;
}
