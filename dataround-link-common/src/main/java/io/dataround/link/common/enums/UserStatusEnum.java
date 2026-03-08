/**
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */
package io.dataround.admin.common.enums;

/**
 * User status enum
 * @author yuehan124@gmail.com
 * @since 2026-02-19
 */
public enum UserStatusEnum {

    //Status: 1-Normal, 2-Disabled, 3-Locked
    NORMAL(1, "Normal"), RUNNING(2, "Disabled"), LOCKED(3, "Locked");

    private final int code;
    private final String description;

    UserStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
