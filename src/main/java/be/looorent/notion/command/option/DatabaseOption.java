package be.looorent.notion.command.option;

import picocli.CommandLine.Option;

public class DatabaseOption {
    @Option(names = {"-d", "--database-id"},
            description = "The Database to export",
            defaultValue = "")
    private String databaseId;

    @Option(names = {"-gp", "--group-property"},
            description = "The exported document contains all the exported pages, grouped by this property. " +
                    "Grouping the pages creates an additional level in the exported document. " +
                    "These groups are ordered according to the natural order of this property in Notion. " +
                    "This property must be a single-select property. " +
                    "When this property is not defined, pages are not grouped.",
            defaultValue = "")
    private String groupPropertyName;

    @Option(names = {"-op", "--order-property"},
            description = "The exported document contains all the exported pages, ordered by this property. " +
                    "When the pages are grouped, the order is only applied within the group. " +
                    "This property must be an integer property. " +
                    "When this property is not defined, the natural order of Notion is used.")
    private String orderPropertyName;

    public String getGroupPropertyName() {
        return groupPropertyName;
    }

    public void setGroupPropertyName(String groupPropertyName) {
        this.groupPropertyName = groupPropertyName;
    }

    public String getOrderPropertyName() {
        return orderPropertyName;
    }

    public void setOrderPropertyName(String orderPropertyName) {
        this.orderPropertyName = orderPropertyName;
    }

    public String getDatabaseId() {
        return databaseId;
    }
}
