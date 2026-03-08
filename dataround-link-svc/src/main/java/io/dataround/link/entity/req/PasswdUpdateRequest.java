/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */
package io.dataround.link.entity.req;

import lombok.Data;

/**
 * Password update request entity
 * @author yuehan124@gmail.com
 * @since 2026-02-19
 */
@Data
public class PasswdUpdateRequest {

    private String oldPasswd;
    private String newPasswd;
}
