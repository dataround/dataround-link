/*
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */

package io.dataround.link.common.exception;

/**
 * Dataround link exception
 * 
 * @author yuehan124@gmail.com
 * @since 2026/02/23
 */
public class LinkException extends RuntimeException {

    public LinkException() {
        super();
    }

    public LinkException(String message) {
        super(message);
    }

    public LinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public LinkException(Throwable cause) {
        super(cause);
    }

}
