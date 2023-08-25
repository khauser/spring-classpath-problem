package org.demo.command;

import java.util.function.Consumer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CommandExecutorResult
{
    private final Level level;
    private final int exitCode;

    private final String command;

    private final String standardOutput;
    private final String errorOutput;
    private final boolean isFailed;

    public CommandExecutorResult(String command, String standardOutput, String errorOutput, int exitCode)
    {
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;

        this.command = command;
        this.exitCode = exitCode;

        String output = standardOutput + errorOutput;
        this.isFailed = output.contains("ERROR") || exitCode != 0;

        level = findLogLevel();
    }

    private Level findLogLevel()
    {
        String output = standardOutput + errorOutput;
        if (isFailed())
        {
            return Level.ERROR;
        }
        else if (output.contains("Warning"))
        {
            return Level.WARN;
        }
        return Level.INFO;
    }

    public void logResult()
    {
        level.log(command, standardOutput, errorOutput);
    }

    public enum Level
    {
        INFO(log::info), WARN(log::warn), ERROR(log::error);

        private final Consumer<String> logger;

        private Level(Consumer<String> logger)
        {
            this.logger = logger;
        }

        public void log(String command, String standardOutput, String errorOutput)
        {
            logger.accept("Command: " + command + "\n Message: " + standardOutput + "\n Error-Message: " + errorOutput);
        }
    }

}
