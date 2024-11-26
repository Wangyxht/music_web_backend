package com.music.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {

    private static final String KEY = "key";

    /**
     * 接受业务数据，生成token令牌
     * @param claims 业务数据
     * @return 加密后的token
     */
    public static String generateToken(Map<String, Object> claims){
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 4))
                .sign(Algorithm.HMAC256(KEY));
    }

    /**
     * 接收一个token，解密业务数据
     * @param token token令牌
     * @return 业务数据
     */
    public static Map<String, Object> parseToken(String token){
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }
}
