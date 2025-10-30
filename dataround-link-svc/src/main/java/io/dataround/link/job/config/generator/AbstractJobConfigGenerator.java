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

package io.dataround.link.job.config.generator;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.JSONObject;

/**
 * AbstractJobConfigGenerator
 *
 * @author yuehan124@gmail.com
 * @since 2025/10/29
 */
public abstract class AbstractJobConfigGenerator implements JobConfigGenerator {
    
    public String sourceResultTableName(String tableName, Long jobId, GeneratorContext context) {
        String result = "TableSource_" + tableName + "_" + jobId;
        context.setPrevStepResultTableName(result);
        return result;
    }

    public String transformResultTableName(String tableName, Long jobId, GeneratorContext context) {        
        String result = "TableTransform_" + tableName + "_" + jobId;
        context.setPrevStepResultTableName(result);
        return result;
    }

    public String prevStepResultTableName(GeneratorContext context) {
        // If has transform step, return transform result table name
        return context.getPrevStepResultTableName();
    }

    @Override
    public List<JSONObject> generateTransformConfig(GeneratorContext context) {
        // Return an empty list by default
        return new ArrayList<>();
    }
}
