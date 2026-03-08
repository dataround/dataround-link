/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.dataround.link.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.AES;
import com.google.code.kaptcha.Producer;
import io.dataround.link.common.utils.CookieUtils;
import io.dataround.link.common.utils.JwtUtil;
import io.dataround.link.common.Result;
import io.dataround.link.common.controller.BaseController;
import io.dataround.link.common.entity.res.UserResponse;
import io.dataround.link.common.utils.RequestUtils;
import io.dataround.link.entity.User;
import io.dataround.link.service.UserService;
import io.dataround.link.config.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Login controller
 *
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Controller
@RequestMapping("/api/")
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProduer;

    private static final String CAPTCHA_SALT = "dataroundcaptcha";

    @PostMapping("/login")
    @ResponseBody
    @Operation
    @Parameters({
            @Parameter(name = "name", description = "user name", required = true),
            @Parameter(name = "passwd", description = "user passwd", required = true)
    })
    public Result<Object> login(@RequestBody Map<String, String> requestBody) {
        HttpServletRequest request = getRequest();
        HttpServletResponse response = getResponse();
        String name = requestBody.get("name");
        String passwd = requestBody.get("passwd");
        String captcha = requestBody.get("captcha");
        // check captcha
        boolean isValidCaptcha = false;
        if (StringUtils.isNotBlank(captcha)) {
            String encodedCaptcha = CookieUtils.getCookie(CookieUtils.COOKIE_KEY_CAPTCHA, request);
            if (StringUtils.isNotBlank(encodedCaptcha)) {
                String decodedCaptcha = AES.decrypt(encodedCaptcha, CAPTCHA_SALT);
                if (StringUtils.equalsIgnoreCase(captcha, decodedCaptcha)) {
                    CookieUtils.cleanCookie(CookieUtils.COOKIE_KEY_CAPTCHA, request, response);
                    isValidCaptcha = true;
                }
            }
        }
        if (!isValidCaptcha) {
            return Result.error(MessageUtils.getMessage("login.captcha.error"));
        }
        // check user and password
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(passwd)) {
            UserResponse loginUser = userService.login(name, passwd);
            if (loginUser != null) {
                String remoteIp = RequestUtils.getRemoteIp(getRequest());
                log.info("user {} login success, ip {}", name, remoteIp);
                long expire = System.currentTimeMillis() + CookieUtils.EXPIRATION_TIME * 2 * 240 * 1000;
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("expire", expire);
                // create new cookie
                loginUser.setUserIp(remoteIp);
                loginUser.setExpiration(expire);
                CookieUtils.addUidCookie(JwtUtil.genToken(loginUser), request, response);
                // setting current project, used for scheduler iframe request only
                CookieUtils.addProjectCookie(loginUser.getProjectId() + "%2C" + loginUser.getProjectName(), request, response);
                // update user login info
                LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(User::getId, loginUser.getUserId());
                updateWrapper.set(User::getLastLoginTime, new Date());
                updateWrapper.set(User::getLastLoginIp, loginUser.getUserIp());
                userService.update(updateWrapper);
                return Result.success(map);
            }
        }
        log.info("user {} login failed, remote ip: {}", name, RequestUtils.getRemoteIp(request));
        return Result.error(MessageUtils.getMessage("login.error"));
    }

    @GetMapping("logout")
    @ResponseBody
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // clean cookies
        CookieUtils.cleanAllCookies(request, response);
        return Result.success("logout successful");
    }

    @GetMapping("/captcha")
    public ResponseEntity<byte[]> getCaptcha() throws IOException {
        HttpServletResponse response = getResponse();
        String text = kaptchaProduer.createText();
        // generate captcha image
        BufferedImage image = kaptchaProduer.createImage(text);
        String captcha = AES.encrypt(text, CAPTCHA_SALT);
        CookieUtils.addCaptchaCookie(captcha, getRequest(), response);
        // Convert BufferedImage to byte[]
        byte[] bytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            bytes = baos.toByteArray();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

}

