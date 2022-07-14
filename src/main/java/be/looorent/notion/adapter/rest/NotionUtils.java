package be.looorent.notion.adapter.rest;

import be.looorent.notion.core.PageComposite;
import be.looorent.notion.core.PageGroup;
import notion.api.v1.model.databases.Database;
import notion.api.v1.model.databases.DatabaseProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static be.looorent.notion.core.PageGroup.noGroup;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

class NotionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(NotionUtils.class);

    private NotionUtils() {}

    static String readTitle(Database database) {
        return database.getTitle()
                .stream()
                .map(DatabaseProperty.RichText::getPlainText)
                .collect(joining());
    }

    static List<PageGroup> createDefaultGroup(List<PageComposite> pages) {
        var group = noGroup(pages);
        LOG.debug("Use default Group {}", group);
        return singletonList(noGroup(pages));
    }
}
