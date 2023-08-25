package org.demo.command;

import static org.demo.exception.KubernetesExceptionCode.SHELL_COMMAND_EXCEPTION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;

import org.demo.exception.KubernetesExceptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommandExecutor
{
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

    @Autowired
    private KubernetesExceptionService exceptionService;

    @Autowired
    private ResourceLoader resourceLoader;

    public synchronized CommandExecutorResult execute(String dirPath, String command)
    {
        try
        {
            log.debug("Execute command: '{}' here '{}'", command, dirPath);

            ProcessBuilder builder = new ProcessBuilder();

            File dir = new File(dirPath);
            builder.directory(dir);

            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            if (isWindows)
            {
                builder.command("cmd", "/c", command);
                builder.redirectErrorStream(true);
            }
            else
            {
                builder.command("bash", "-xc", command);
            }

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while((line = reader.readLine()) != null)
            {
                log.debug("tasklist: {}", line);
            }
            int exitCode = process.waitFor();

            // warning are seen as errors!!!
            String errorOutput = StreamUtils.copyToString(process.getErrorStream(), StandardCharsets.UTF_8);
            if (StringUtils.hasText(errorOutput) && errorOutput.contains("Error"))
            {
                log.warn("errorOutput: {}" + errorOutput);
            }
            return new CommandExecutorResult(command, null, null, exitCode);
        }
        catch(InterruptedException ie)
        {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt();
            return null;
        }
        catch(IOException ioe)
        {
            throw exceptionService.createAndSendException(SHELL_COMMAND_EXCEPTION, ioe, command);
        }
    }

    public CommandExecutorResult executeFile(Map<String, String> model, String workingDir, String scriptName,
                    String targetScriptName)
    {
        Resource resource;
        File variables;
        File executable;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        try
        {
            InputStream in;
            if (isWindows)
            {
                //in = this.getClass().getClassLoader().getResourceAsStream(scriptName + ".bat");
                resource = resourceLoader.getResource(scriptName + ".bat");
                executable = new File(workingDir + targetScriptName + ".bat");
                variables = new File(workingDir + targetScriptName + "_vars.bat");
                if (variables.exists())
                {
                    Files.delete(Paths.get(variables.getAbsolutePath()));
                }
                variables.getParentFile().mkdirs();
                FileWriter fw = new FileWriter(variables, false);
                for (Entry<String, String> entry : model.entrySet())
                {
                    fw.write("SET " + entry.getKey() + "=" + entry.getValue() + LINE_SEPARATOR_WINDOWS);
                }
                fw.close();
            }
            else
            {
                //in = this.getClass().getClassLoader().getResourceAsStream(scriptName + ".sh");
                resource = resourceLoader.getResource(scriptName + ".sh");
                executable = new File(workingDir + targetScriptName + ".sh");
                variables = new File(workingDir + targetScriptName + "_vars.sh");
                if (variables.exists())
                {
                    Files.delete(Paths.get(variables.getAbsolutePath()));
                }
                variables.getParentFile().mkdirs();
                FileWriter fw = new FileWriter(variables, false);
                fw.write("#!/usr/bin/env bash" + LINE_SEPARATOR_UNIX);
                for (Entry<String, String> entry : model.entrySet())
                {
                    fw.write(entry.getKey() + "=\"" + entry.getValue() + "\"" + LINE_SEPARATOR_UNIX);
                }
                fw.close();
            }

//            Files.writeString(executable.toPath(),
//                            StreamUtils.copyToString(in, Charset.defaultCharset()));
            Files.writeString(executable.toPath(),
                            StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset()));
        }
        catch(IOException e)
        {
            throw exceptionService.createAndSendException(SHELL_COMMAND_EXCEPTION, e, scriptName);
        }
        if (isWindows)
        {
            return execute(workingDir, executable.getName());
        }
        else
        {
            execute(workingDir, "chmod +x " + executable);
            execute(workingDir, "chmod +x " + variables);
            return execute(workingDir, "./" + executable.getName());
        }
    }
}
