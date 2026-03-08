/**
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */
package io.dataround.link.common.utils;

import com.alibaba.fastjson2.JSONObject;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.exception.JwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtUtil
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
public class JwtUtil {

    private static final String SECRET_KEY = "^_^__mydp__^_^";

    public static String genToken(UserResponse user) {
        Map<String, Object> claims = new HashMap<>();
        UserResponse userInfo = new UserResponse();
        userInfo.setUserId(user.getUserId());
        userInfo.setUserName(user.getUserName());
        userInfo.setProjectId(user.getProjectId());
        userInfo.setProjectName(user.getProjectName());
        userInfo.setUserIp(user.getUserIp());
        userInfo.setExpiration(user.getExpiration());
        claims.put("user", JSONObject.toJSONString(userInfo));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(user.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static UserResponse verifyToken(String token, String remoteIp) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        Date date = claims.getExpiration();
        if (date.before(new Date())) {
            throw new JwtTokenException(String.format("Token invalid, expired time: %s", date));
        }
        UserResponse userInfo = JSONObject.parseObject(claims.get("user", String.class), UserResponse.class);
        if (!remoteIp.equals(userInfo.getUserIp())) {
            String str = String.format("Token invalid, remote ip not match, token ip: %s, remote ip: %s",
                    userInfo.getUserIp(), remoteIp);
            throw new JwtTokenException(str);
        }
        return userInfo;
    }

}
