package be.looorent.notion.command;

import be.looorent.notion.command.option.AuthenticationOption;
import be.looorent.notion.command.option.OutputOption;
import be.looorent.notion.command.option.PageOption;
import be.looorent.notion.port.NotionPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

@Command(name = "export-page",
        description = "In Notion, a page can contain a collection of subpages. With this command, you can create a document based on this hierarchy of pages.",
        mixinStandardHelpOptions = true,
        showDefaultValues = true)
class PageExportCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PageExportCommand.class);

    @Mixin
    private AuthenticationOption authentication = new AuthenticationOption();

    @Mixin
    private PageOption options = new PageOption();

    @Mixin
    private OutputOption output = new OutputOption();

    @Option(names = {"-ti", "--title"},
            description = "The title to use in the exported document. " +
                    "When this property is not defined, the main page's title is used.")
    private String title;

    private final CommandService service;

    public PageExportCommand(CommandService service) {
        this.service = service;
    }

    @Override
    public void run() {
        var page = new NotionPage(options.getPageId(), title, options.isIncludeNestedPages(), output.toOutputFormats());
        LOG.info("Exporting page with configuration: {}", page);
        service.export(page, authentication.getNotionToken());
    }

    public AuthenticationOption getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AuthenticationOption authentication) {
        this.authentication = authentication;
    }

    public PageOption getOptions() {
        return options;
    }

    public void setOptions(PageOption options) {
        this.options = options;
    }

    public OutputOption getOutput() {
        return output;
    }

    public void setOutput(OutputOption output) {
        this.output = output;
    }
}
