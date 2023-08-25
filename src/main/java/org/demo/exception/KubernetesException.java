package org.demo.exception;

public class KubernetesException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final KubernetesExceptionCode exceptionCode;
    private final String stacktrace;
    private final Throwable originException;
    private final transient Object[] params;

    public KubernetesException(KubernetesExceptionCode exceptionCode, String stacktrace, Throwable originException,
                    String... params)
    {
        super(originException);
        this.exceptionCode = exceptionCode;
        this.stacktrace = stacktrace;
        this.originException = originException;
        this.params = params;
    }

    public String extractStackTrace()
    {
        return stacktrace;
    }

    public KubernetesExceptionCode getExceptionCode()
    {
        return exceptionCode;
    }

    public Throwable getOriginException()
    {
        return originException;
    }

    @Override
    public String getMessage()
    {
        return exceptionCode.getMessage(params);
    }

    @Override
    public String toString()
    {
        return getMessage();
    }
}
