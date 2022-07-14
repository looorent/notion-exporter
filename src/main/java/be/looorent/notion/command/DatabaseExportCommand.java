package be.looorent.notion.command;

import be.looorent.notion.command.option.Authentication;
import be.looorent.notion.command.option.DatabaseOption;
import be.looorent.notion.command.option.FormatStrategy;
import be.looorent.notion.command.option.Output;
import be.looorent.notion.port.DocumentFactory;
import be.looorent.notion.port.DocumentFactoryException;
import be.looorent.notion.port.DocumentWriteException;
import be.looorent.notion.port.NotionDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import static java.lang.System.exit;
import static picocli.CommandLine.ExitCode.*;

@Command(name = "export-database",
        description = "In Notion, a database is basically a collection of pages that have properties. With this command, you can create a document based on these pages, grouped and ordered.",
        mixinStandardHelpOptions = true,
        showDefaultValues = true)
class DatabaseExportCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DatabaseExportCommand.class);

    @Mixin
    private Authentication authentication = new Authentication();

    @Mixin
    private DatabaseOption options = new DatabaseOption();

    @Mixin
    private Output output = new Output();

    @Option(names = {"-ti", "--title"},
            description = "The title to use in the exported document. " +
                    "When this property is not defined, the database's title is used.")
    private String title;

    private final DocumentFactory factory;
    private final FormatStrategy writerStrategy;

    public DatabaseExportCommand(DocumentFactory factory,
                                 FormatStrategy writerStrategy) {
        this.factory = factory;
        this.writerStrategy = writerStrategy;
    }

    @Override
    public void run() {
        try {
            var folder = output.configureOutputFolder();
            var database = new NotionDatabase(options.getDatabaseId(), title, options.getGroupPropertyName(), options.getOrderPropertyName(), folder);
            LOG.info("Exporting database with configuration: {}", database);
            var writer = writerStrategy.findDatabaseWriter(output.getFormat());
            LOG.info("Writer selected: {}", writer.getName());
            var document = factory.create(database, authentication.getNotionToken());
            var result = writer.write(document, folder);
            LOG.info("{} groups, {} pages, {} blocks, written to {}", document.getNumberOfGroups(), document.getNumberOfPages(), document.getNumberOfBlocks(), result.toAbsolutePath());
            exit(OK);
        } catch (IllegalArgumentException e) {
            LOG.error("Configuration error", e);
            exit(USAGE);
        } catch (DocumentFactoryException e) {
            LOG.error("Error when reading the datasource", e);
            exit(SOFTWARE);
        } catch (DocumentWriteException e) {
            LOG.error("Error when writing file.", e);
            exit(SOFTWARE);
        } catch (Exception e) {
            LOG.error("An error occurred", e);
            exit(SOFTWARE);
        }
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public DatabaseOption getOptions() {
        return options;
    }

    public void setOptions(DatabaseOption options) {
        this.options = options;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
