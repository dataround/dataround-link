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

package io.dataround.link.init;

import io.dataround.link.job.JobStatusChecker;
import io.dataround.link.quartz.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Application Initializer
 * Executes initialization tasks after Spring Boot startup is complete
 *
 * @author yuehan124@gmail.com
 * @since 2025/09/26
 */
@Component
@Slf4j
public class ApplicationInitializer implements CommandLineRunner {
  @Autowired
  private SchedulerService schedulerService;
  @Autowired
  private JobStatusChecker jobStatusChecker;
  
  @Override
  public void run(String... args) {
    log.info("Application initialization started...");

    // Add initialization tasks here
    // For example: loading configurations, initializing caches, warming up data, etc.
    schedulerService.start();
    // @PostConstruct runs before automatic database initialization, 
    // so we need to call start in CommandLineRunner run method to ensure job status is checked after database is initialized
    jobStatusChecker.start();

    log.info("Application initialization completed");
  }

}