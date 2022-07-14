package be.looorent.notion.command;

import be.looorent.notion.command.option.Authentication;
import be.looorent.notion.command.option.FormatStrategy;
import be.looorent.notion.command.option.Output;
import be.looorent.notion.command.option.PageOption;
import be.looorent.notion.port.DocumentFactory;
import be.looorent.notion.port.DocumentFactoryException;
import be.looorent.notion.port.DocumentWriteException;
import be.looorent.notion.port.NotionPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import static java.lang.System.exit;
import static picocli.CommandLine.ExitCode.*;

@Command(name = "export-page",
        description = "In Notion, a page can contain a collection of subpages. With this command, you can create a document based on this hierarchy of pages.",
        mixinStandardHelpOptions = true,
        showDefaultValues = true)
class PageExportCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PageExportCommand.class);

    @Mixin
    private Authentication authentication = new Authentication();

    @Mixin
    private PageOption options = new PageOption();

    @Mixin
    private Output output = new Output();

    @CommandLine.Option(names = {"-ti", "--title"},
            description = "The title to use in the exported document. " +
                    "When this property is not defined, the main page's title is used.")
    private String title;

    private final DocumentFactory factory;
    private final FormatStrategy writerStrategy;

    public PageExportCommand(DocumentFactory factory,
                             FormatStrategy writerStrategy) {
        this.factory = factory;
        this.writerStrategy = writerStrategy;
    }

    @Override
    public void run() {
        try {
            var folder = output.configureOutputFolder();
            var mainPage = new NotionPage(options.getPageId(), title, options.isIncludeNestedPages(), folder);
            LOG.info("Exporting page with configuration: {}", mainPage);
            var writer = writerStrategy.findDatabaseWriter(output.getFormat());
            LOG.info("Writer selected: {}", writer.getName());
            var document = factory.create(mainPage, authentication.getNotionToken());
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

    public PageOption getOptions() {
        return options;
    }

    public void setOptions(PageOption options) {
        this.options = options;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }
}
