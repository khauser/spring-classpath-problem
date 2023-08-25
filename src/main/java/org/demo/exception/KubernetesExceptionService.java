package org.demo.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KubernetesExceptionService
{
    public KubernetesException createException(KubernetesExceptionCode exceptionCode, String... params)
    {
        return createAndSendException(exceptionCode, new RuntimeException(exceptionCode.name()), params);
    }

    public KubernetesException createAndSendException(KubernetesExceptionCode exceptionCode, Throwable originException,
                    String... params)
    {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        originException.printStackTrace(pw);
        String stacktrace = sw.getBuffer().toString();
        KubernetesException exception = new KubernetesException(exceptionCode, stacktrace, originException, params);
        log.error(exception.getMessage(), exception.getOriginException());
        return exception;
    }
}
