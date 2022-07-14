package be.looorent.notion.command;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

@TopCommand
@Command(mixinStandardHelpOptions = true,
        name = "notion",
        subcommands = { DatabaseExportCommand.class, PageExportCommand.class }
)
class DefaultCommand {
}
