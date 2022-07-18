package be.looorent.notion.adapter.rest;

import notion.api.v1.NotionClient;
import notion.api.v1.exception.NotionAPIError;
import notion.api.v1.logging.Slf4jLogger;
import notion.api.v1.model.blocks.Block;
import notion.api.v1.model.databases.Database;
import notion.api.v1.model.databases.DatabaseProperty;
import notion.api.v1.model.pages.Page;
import notion.api.v1.model.pages.PageProperty;
import notion.api.v1.model.pages.PagePropertyItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.join;
import static java.net.URLDecoder.decode;
import static java.util.Collections.emptyList;
import static java.util.Optional.*;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
class NotionRepository {
    private static final Logger LOG = LoggerFactory.getLogger(NotionRepository.class);
    private static final String TITLE_PROPERTY_ID = "title";
    public static final String UTF_8 = "UTF-8";

    public Database findDatabase(String databaseId, String notionToken) {
        LOG.info("Loading database '{}' from Notion...", databaseId);
        var client = createClient(notionToken);
        var database = client.retrieveDatabase(databaseId);
        LOG.debug("Database found in Notion.");
        return database;
    }

    public Page findPage(String pageId, String notionToken) {
        LOG.info("Loading page '{}' from Notion...", pageId);
        var client = createClient(notionToken);
        var page = client.retrievePage(pageId);
        LOG.debug("Page found in Notion.");
        return page;
    }

    public List<Page> findPagesInDatabase(String databaseId, String notionToken) {
        LOG.info("Loading pages in database '{}' from Notion...", databaseId);
        var client = createClient(notionToken);

        String nextCursor = null;
        var hasMore = true;
        var pageDefinitions = new ArrayList<Page>();
        while (hasMore) {
            var result = client.queryDatabase(databaseId, null, emptyList(), nextCursor, null);
            pageDefinitions.addAll(result.getResults());
            hasMore = result.getHasMore();
            nextCursor = result.getNextCursor();
        }

        LOG.info("{} pages found in Notion.", pageDefinitions.size());
        return pageDefinitions;
    }

    public List<Block> findChildren(String blockId, String notionToken) {
        var client = createClient(notionToken);
        String nextCursor = null;
        var hasMore = true;
        var blocks = new ArrayList<Block>();
        while (hasMore) {
            var result = client.retrieveBlockChildren(blockId, nextCursor, null);
            blocks.addAll(result.getResults());
            hasMore = result.getHasMore();
            nextCursor = result.getNextCursor();
        }
        LOG.debug("{} blocks found under block {}", blocks.size(), blockId);
        return blocks;
    }

    public String findPageTitle(String pageId, String notionToken) {
        LOG.debug("Loading page's title '{}' from Notion...", pageId);
        var client = createClient(notionToken);
        var title = of(client.retrievePagePropertyItem(pageId, TITLE_PROPERTY_ID, null, null))
                .map(PagePropertyItem::getResults)
                .map(property -> property.stream().map(PagePropertyItem::getTitle).filter(Objects::nonNull).map(PageProperty.RichText::getPlainText).collect(toList()))
                .orElse(emptyList());
        if (title.isEmpty()) {
            LOG.warn("Title cannot be found for page with id = '{}'", pageId);
        }
        return join("", title);
    }

    public int findPageOrder(String pageId, String orderPropertyId, String notionToken) {
        LOG.debug("Loading page's order '{}' from Notion with property '{}'...", pageId, orderPropertyId);
        var client = createClient(notionToken);
        try {
            return of(client.retrievePagePropertyItem(pageId, decodePropertyId(orderPropertyId), null, null))
                    .filter(property -> property.getNumber() != null)
                    .map(PagePropertyItem::getNumber)
                    .map(Number::intValue)
                    .orElseGet(() -> {
                        LOG.warn("Order cannot be found for page with id = '{}'. Property id = '{}'", pageId, orderPropertyId);
                        return 0;
                    });
        } catch (NotionAPIError e) {
            if (e.getError().getStatus() == 400) {
                LOG.warn("Order cannot be found for page with id = '{}'. Property id = '{}'. Error = {}", pageId, orderPropertyId, e.getError().getMessage());
                return 0;
            } else {
                throw e;
            }
        }
    }

    public Optional<String> findPageGroupId(String pageId, String groupPropertyId, String notionToken) {
        try {
            LOG.debug("Loading page's group '{}' from Notion with property '{}'...", pageId, groupPropertyId);
            var client = createClient(notionToken);
            var groupId = of(client.retrievePagePropertyItem(pageId, decodePropertyId(groupPropertyId), null, null))
                    .filter(property -> property.getSelect() != null)
                    .map(PagePropertyItem::getSelect)
                    .map(DatabaseProperty.Select.Option::getId);
            if (groupId.isEmpty()) {
                LOG.warn("Group cannot be found for page with id = '{}'. Property id = '{}'", pageId, groupPropertyId);
            }
            return groupId;
        } catch (NotionAPIError e) {
            if (e.getError().getStatus() == 400) {
                LOG.warn("Group cannot be found for page with id = '{}'. Property id = '{}'. Error = {}", pageId, groupPropertyId, e.getError().getMessage());
                return empty();
            } else {
                throw e;
            }
        }
    }

    private NotionClient createClient(String notionToken) {
        var client = new NotionClient(notionToken);
        client.setLogger(new Slf4jLogger());
        return client;
    }

    private String decodePropertyId(String id) {
        try {
            return decode(id, UTF_8);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Encoding of the property id '{}' failed. Using '{}'", id, id, e);
            return id;
        }
    }
}
