package be.looorent.notion.command;

import be.looorent.notion.port.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import static java.lang.System.exit;
import static java.nio.file.Files.createDirectories;
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

    public void export(NotionDocumentable configuration, String token) {
        try {
            LOG.info("Fetching with configuration: {}...", configuration);
            var document = factory.create(configuration, token);
            for (var output : configuration.getOutputs()) {
                LOG.info("Fetched with success. Exporting to {}...", output.getFormat().getId());
                var folder = createDirectories(output.getFolder());
                var writer = writerStrategy.findDatabaseWriter(output.getFormat());
                var result = writer.write(document, folder);
                LOG.info("{} groups, {} pages, {} blocks, written to {}", document.getNumberOfGroups(), document.getNumberOfPages(), document.getNumberOfBlocks(), result.toAbsolutePath());
            }
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
