package org.demo.controller;

import java.util.concurrent.CompletableFuture;

import org.demo.service.HelmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(KubernetesServiceController.ISTE_KUBERNETES_RESOURCE)
public class KubernetesServiceController
{
    public static final String ISTE_KUBERNETES_RESOURCE = "/kubernetes";

    public static final String HELM_VERSION = "/helmVersion";

    @Autowired
    private HelmService helmService;

    @GetMapping(HELM_VERSION)
    public CompletableFuture<String> helmVersion(@RequestParam(required = false) String taskName,
                    @RequestParam(required = false, defaultValue = "false") Boolean failed)
    {
        return CompletableFuture.supplyAsync(() -> helmService.executeHelmVersionScript());

    }
}