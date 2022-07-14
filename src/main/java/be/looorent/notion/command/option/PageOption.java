package be.looorent.notion.command.option;

import picocli.CommandLine.Option;

public class PageOption {
    @Option(names = {"-p", "--page-id"},
            description = "The Page to export",
            defaultValue = "")
    private String pageId;

    @Option(names = {"-n", "--include-nested-pages"},
            description = "Whether the nested pages must be loaded and exported. If this option is false, only the page mentioned is exported.",
            defaultValue = "true")
    private boolean includeNestedPages;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public boolean isIncludeNestedPages() {
        return includeNestedPages;
    }

    public void setIncludeNestedPages(boolean includeNestedPages) {
        this.includeNestedPages = includeNestedPages;
    }
}
