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

package io.dataround.link;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the dataround-link Spring Boot application.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"io.dataround.link"})
@MapperScan(basePackages = {"io.dataround.link.mapper"})
@EnableScheduling
public class LinkApplication {

    /**
     * Main method to run the dataround-link application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(LinkApplication.class, args);
        log.info("SpringDoc URI: /swagger-ui/index.html");
    }
}