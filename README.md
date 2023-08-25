# CompletableFuture classpath problem

Reproducer project

```
2023-08-24T12:18:37.661Z  INFO 7 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 0 ms
2023-08-24T12:18:37.696Z ERROR 7 --- [onPool-worker-1] o.d.e.KubernetesExceptionService         : SHELL_COMMAND_EXCEPTION 100 Failed to execute shell/batch commands due to an exception classpath:scripts/helm_version_script

java.io.FileNotFoundException: class path resource [scripts/helm_version_script.sh] cannot be opened because it does not exist
        at org.springframework.core.io.ClassPathResource.getInputStream(ClassPathResource.java:211) ~[spring-core-6.0.11.jar!/:6.0.11]
        at org.demo.command.CommandExecutor.executeFile(CommandExecutor.java:138) ~[classes!/:na]
        at org.demo.service.HelmService.executeHelmVersionScript(HelmService.java:26) ~[classes!/:na]
        at org.demo.controller.KubernetesServiceController.lambda$helmVersion$0(KubernetesServiceController.java:27) ~[classes!/:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinTask.doExec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.scan(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(Unknown Source) ~[na:na]

2023-08-24T12:18:37.714Z ERROR 7 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] threw exception

java.io.FileNotFoundException: class path resource [scripts/helm_version_script.sh] cannot be opened because it does not exist
        at org.springframework.core.io.ClassPathResource.getInputStream(ClassPathResource.java:211) ~[spring-core-6.0.11.jar!/:6.0.11]
        at org.demo.command.CommandExecutor.executeFile(CommandExecutor.java:138) ~[classes!/:na]
        at org.demo.service.HelmService.executeHelmVersionScript(HelmService.java:26) ~[classes!/:na]
        at org.demo.controller.KubernetesServiceController.lambda$helmVersion$0(KubernetesServiceController.java:27) ~[classes!/:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinTask.doExec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.scan(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(Unknown Source) ~[na:na]

2023-08-24T12:18:37.715Z ERROR 7 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: SHELL_COMMAND_EXCEPTION 100 Failed to execute shell/batch commands due to an exception classpath:scripts/helm_version_script] with root cause

java.io.FileNotFoundException: class path resource [scripts/helm_version_script.sh] cannot be opened because it does not exist
        at org.springframework.core.io.ClassPathResource.getInputStream(ClassPathResource.java:211) ~[spring-core-6.0.11.jar!/:6.0.11]
        at org.demo.command.CommandExecutor.executeFile(CommandExecutor.java:138) ~[classes!/:na]
        at org.demo.service.HelmService.executeHelmVersionScript(HelmService.java:26) ~[classes!/:na]
        at org.demo.controller.KubernetesServiceController.lambda$helmVersion$0(KubernetesServiceController.java:27) ~[classes!/:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.run(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.CompletableFuture$AsyncSupply.exec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinTask.doExec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.scan(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinPool.runWorker(Unknown Source) ~[na:na]
        at java.base/java.util.concurrent.ForkJoinWorkerThread.run(Unknown Source) ~[na:na]
```

### Execute

```bash
$ ./gradlew clean build
$ docker build -t java-classpath-problem .
$ docker run -p 8080:8080 java-classpath-problem
$ curl http://localhost:8080/kubernetes/helmVersion
```

### Fails with

```java
    @GetMapping(HELM_VERSION)
    public CompletableFuture<String> helmVersion(@RequestParam(required = false) String taskName,
                    @RequestParam(required = false, defaultValue = "false") Boolean failed)
    {
        return CompletableFuture.supplyAsync(() -> helmService.executeHelmVersionScript());

    }
```

### Succeeds with

```java
    @GetMapping(HELM_VERSION)
    public String helmVersion(@RequestParam(required = false) String taskName,
                    @RequestParam(required = false, defaultValue = "false") Boolean failed)
    {
        return helmService.executeHelmVersionScript();
    }
```

or

by using `in = this.getClass().getClassLoader().getResourceAsStream(scriptName + ".bat");` instead of `resource = resourceLoader.getResource(scriptName + ".bat");`.

So something between Spring Boot 3 ResourceLoader, Classloader and CompletableFutures is broken.

