package be.looorent.notion.command;

import be.looorent.notion.command.option.AuthenticationOption;
import be.looorent.notion.command.option.DatabaseOption;
import be.looorent.notion.command.option.OutputOption;
import be.looorent.notion.port.NotionDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "export-database",
        description = "In Notion, a database is basically a collection of pages that have properties. With this command, you can create a document based on these pages, grouped and ordered.",
        mixinStandardHelpOptions = true,
        showDefaultValues = true)
class DatabaseExportCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseExportCommand.class);

    @Mixin
    private AuthenticationOption authentication = new AuthenticationOption();

    @Mixin
    private DatabaseOption options = new DatabaseOption();

    @Mixin
    private OutputOption output = new OutputOption();


    @Option(names = { "-v", "--verbose"}, description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private boolean[] verbose;

    @Option(names = {"-ti", "--title"},
            description = "The title to use in the exported document. " +
                    "When this property is not defined, the database's title is used.")
    private String title;

    private final CommandService service;

    public DatabaseExportCommand(CommandService service) {
        this.service = service;
    }

    @Override
    public void run() {
        var database = new NotionDatabase(options.getDatabaseId(), title, options.getGroupPropertyName(), options.getOrderPropertyName(), output.toOutputFormats());
        LOG.info("Exporting database with configuration: {}", database);
        service.export(database, authentication.getNotionToken());
    }

    public AuthenticationOption getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AuthenticationOption authentication) {
        this.authentication = authentication;
    }

    public DatabaseOption getOptions() {
        return options;
    }

    public void setOptions(DatabaseOption options) {
        this.options = options;
    }

    public OutputOption getOutput() {
        return output;
    }

    public void setOutput(OutputOption output) {
        this.output = output;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
