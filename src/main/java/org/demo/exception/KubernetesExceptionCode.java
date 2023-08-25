package org.demo.exception;

public enum KubernetesExceptionCode
{

    SHELL_COMMAND_EXCEPTION(100, "Failed to execute shell/batch commands due to an exception %s"),

    PARSE_TEMPLATE_EXCEPTION(101, "Could not parse template '%s'"),
    WRITE_FILE_EXCEPTION(102, "Unable to write file to: %s."),
    CREATE_FOLDER_EXCEPTION(103, "Unable to create folder: %s."),
    READ_FILE_CONTENT_EXCEPTION(104, "Unable to read file: %s."),

    RECEIVE_KUBE_CONFIG_FILE_EXCEPTION(501, "Unable to receive kube-config from: %s."),
    EMPTY_KUBE_CONFIG_FILE_EXCEPTION(502, "Kube-config is empty: %s."),

    KUBE_CLIENT_APPLY_FILE_EXCEPTION(601, "Unable to apply file: %s."),

    HELM_NOT_INSTALLED_EXCEPTION(650, "You need to install helm (at least version 3) on os."),
    HELM_SHOW_CHART_PARSE_EXCEPTION(660, "'helm show chart' result not parsable (file: %s)."),
    HELM_STATUS_PARSE_EXCEPTION(670, "Helm result not parsable (file: %s)."),
    KUBECTL_IO_EXCEPTION(680, "Kubectl result not parsable (file: %s, content: %s)."),

    UNEXPECTED_EXCEPTION(700, "Unexception exception occured."),

    REST_EXCEPTION(401, "Unable to process rest-call."),
    MESSAGING_CLIENT_EXCEPTION(800, "Unable to process azure message to %s"),;

    private final int exceptionNumber;
    private final String messageTemplate;

    KubernetesExceptionCode(int exceptionNumber, String messageTemplate)
    {
        this.exceptionNumber = exceptionNumber;
        this.messageTemplate = messageTemplate;
    }

    public int getExceptionNumber()
    {
        return exceptionNumber;
    }

    public String getMessage(Object... params)
    {
        return toString() + " " + String.format(messageTemplate, params);
    }

    @Override
    public String toString()
    {
        return name() + " " + exceptionNumber;
    }

}
