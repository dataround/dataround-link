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

package io.dataround.link.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataround.link.entity.JobInstance;
import io.dataround.link.entity.req.JobInstanceReq;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper interface for JobInstance entity.
 * Provides database operations for job execution instances.
 *
 * @author yuehan124@gmail.com
 * @date 2025-05-06
 */
public interface JobInstanceMapper extends BaseMapper<JobInstance> {

    @Select("""
            <script>
            select a.*, b.name as jobName from job_instance a 
            left join job b on a.job_id=b.id 
            where job_type = #{inst.jobType} 
            <if test='inst.id != null'>
            and a.id = #{inst.id} 
            </if>
            <if test='inst.projectId != null'>
            and a.project_id = #{inst.projectId} 
            </if>
            <if test='inst.status != null'>
            and a.status = #{inst.status} 
            </if>
            <if test='inst.jobName != null'>
            and b.name = #{inst.jobName} 
            </if>
            <if test='inst.startTime != null'>
            and a.start_time <![CDATA[>=]]> #{inst.startTime} 
            </if>
            <if test='inst.endTime != null'>
            and a.end_time <![CDATA[<=]]> #{inst.endTime} 
            </if>
            order by a.id desc 
            limit #{inst.size} offset #{inst.offset}
            </script>""")
    List<JobInstance> selectPage(@Param("inst")JobInstanceReq inst);


    @Select("""
            <script>
            select count(1) from job_instance a 
            left join job b on a.job_id=b.id 
            where job_type = #{inst.jobType}
            <if test='inst.id != null'>
            and a.id = #{inst.id} 
            </if>
            <if test='inst.projectId != null'>
            and a.project_id = #{inst.projectId} 
            </if>
            <if test='inst.status != null'>
            and a.status = #{inst.status} 
            </if>
            <if test='inst.jobName != null'>
            and b.name = #{inst.jobName} 
            </if>
            <if test='inst.startTime != null'>
            and a.start_time <![CDATA[>=]]> #{inst.startTime} 
            </if>
            <if test='inst.endTime != null'>
            and a.end_time <![CDATA[<=]]> #{inst.endTime} 
            </if>
            </script>""")
    long selectCount(@Param("inst")JobInstanceReq inst);

}
