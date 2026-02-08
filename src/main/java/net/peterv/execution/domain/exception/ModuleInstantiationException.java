package net.peterv.execution.domain.exception;

public class ModuleInstantiationException extends RuntimeException {

    public ModuleInstantiationException(String message) {
        super(message);
    }

    public ModuleInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
}
