/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.entity.req;

import io.dataround.link.entity.ResourceApi;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Resource request DTO
 * 
 * @author yuehan124@gmail.com
 * @since 2026/05/03
 */
@Data
public class ResourceRequest {

    private Long id;
    private Long pid;
    private String i18nName;
    private String resKey;
    private String description;
    private Date createTime;
    
    private List<ResourceApi> resourceApis;
}
