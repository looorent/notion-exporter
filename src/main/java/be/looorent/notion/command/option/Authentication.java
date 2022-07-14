package be.looorent.notion.command.option;

import picocli.CommandLine.Option;

public class Authentication {

    @Option(names = {"-to", "--notion-token"},
            description = "The Notion API Token. See Notion's developer documentation to create a token.",
            defaultValue = "")
    private String notionToken;

    public String getNotionToken() {
        return notionToken;
    }

    public void setNotionToken(String notionToken) {
        this.notionToken = notionToken;
    }
}
