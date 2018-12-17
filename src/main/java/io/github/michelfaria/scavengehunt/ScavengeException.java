package io.github.michelfaria.scavengehunt;

public class ScavengeException extends RuntimeException {
    public ScavengeException() {
    }

    public ScavengeException(String message) {
        super(message);
    }

    public ScavengeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScavengeException(Throwable cause) {
        super(cause);
    }

    public ScavengeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
