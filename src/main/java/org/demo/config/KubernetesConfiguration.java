package org.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class KubernetesConfiguration
{
    @Value("${kubernetes.working-dir}")
    private String workingDir;
}
