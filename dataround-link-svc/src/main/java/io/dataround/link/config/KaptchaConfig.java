
/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha config
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
        // create a default kaptcha instance
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        // create a kaptcha config instance
        Properties properties = new Properties();
        // set kaptcha border
        properties.setProperty("kaptcha.border", "yes");
        // set kaptcha border color
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // set kaptcha text producer font color
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // set kaptcha image width
        properties.setProperty("kaptcha.image.width", "125");
        // set kaptcha image height 
        properties.setProperty("kaptcha.image.height", "50");
        // set kaptcha session key
        properties.setProperty("kaptcha.session.key", "code");
        // set kaptcha text producer char length
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // set kaptcha text producer font names
        properties.setProperty("kaptcha.textproducer.font.names", "SimSun, KaiTi, Microsoft YaHei");
        // set kaptcha config
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        // return default kaptcha instance
        return defaultKaptcha;
    }
}
