package org.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.demo.command.CommandExecutor;
import org.demo.config.KubernetesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelmService
{
    public static final String HELM_CHART_VERSION_SCRIPT = "scripts/helm_version_script";

    @Autowired
    private CommandExecutor commandExecutor;

    @Autowired
    private KubernetesConfiguration kubernetesConfiguration;

    public String executeHelmVersionScript()
    {
        Map<String, String> model = new HashMap<>();

        commandExecutor.executeFile(model, kubernetesConfiguration.getWorkingDir(), HELM_CHART_VERSION_SCRIPT,
                        "helm_version");
        return "called";
    }
}
