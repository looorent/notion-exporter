package be.looorent.notion.command;

import be.looorent.notion.command.option.FormatStrategy;
import be.looorent.notion.command.option.Output;
import be.looorent.notion.port.DocumentFactory;
import be.looorent.notion.port.DocumentFactoryException;
import be.looorent.notion.port.DocumentWriteException;
import be.looorent.notion.port.NotionDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import static java.lang.System.exit;
import static picocli.CommandLine.ExitCode.*;

@ApplicationScoped
class CommandService {
    private static final Logger LOG = LoggerFactory.getLogger(CommandService.class);

    private final DocumentFactory factory;
    private final FormatStrategy writerStrategy;

    public CommandService(DocumentFactory factory,
                          FormatStrategy writerStrategy) {
        this.factory = factory;
        this.writerStrategy = writerStrategy;
    }

    public void export(Output output, NotionDatabase database, String token) {
        try {
            var folder = output.configureOutputFolder();
            LOG.info("Exporting database with configuration: {}", database);
            var writer = writerStrategy.findDatabaseWriter(output.getFormat());
            LOG.info("Writer selected: {}", writer.getName());
            var document = factory.create(database, token);
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
}
