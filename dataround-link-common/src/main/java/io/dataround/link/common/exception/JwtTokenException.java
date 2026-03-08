/**
 * Copyright (C) 2025 yuehan124@gmail.com
 *
 * This repository is licensed under the Dataround Open Source License
 */
package io.dataround.admin.common;

/**
 * JwtTokenException
 * 
 * @author yuehan124@gmail.com
 * @since 2025/09/21
 */
public class JwtTokenException extends RuntimeException {
    public JwtTokenException() {
        super();
    }

    public JwtTokenException(String message) {
        super(message);
    }

    public JwtTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenException(Throwable cause) {
        super(cause);
    }

}
