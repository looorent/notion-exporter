package be.looorent.notion.adapter.rest;

import be.looorent.notion.core.GroupDetail;
import be.looorent.notion.core.PageComposite;
import be.looorent.notion.core.PageDetail;
import be.looorent.notion.core.PageGroup;
import notion.api.v1.model.databases.Database;
import notion.api.v1.model.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static be.looorent.notion.adapter.rest.NotionUtils.createDefaultGroup;
import static be.looorent.notion.adapter.rest.NotionUtils.readTitle;
import static java.lang.Integer.MAX_VALUE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

abstract class Root {
    private final String id;
    private final String title;

    Root(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    abstract List<PageGroup> group(List<PageComposite> pages);
    abstract boolean includesChildPages();
    abstract List<PageDetail> loadPages(NotionRepository repository, String token);

    static class DatabaseRoot extends Root {
        private static final Logger LOG = LoggerFactory.getLogger(DatabaseRoot.class);

        private final Database database;
        private final String orderPropertyId;
        private final String groupPropertyId;
        private final List<GroupDetail> groups;

        public DatabaseRoot(String title,
                            Database database,
                            Optional<String> orderPropertyId,
                            Optional<String> groupPropertyId,
                            List<GroupDetail> groups) {
            super(database.getId(), ofNullable(title).filter(not(String::isBlank)).orElseGet(() -> readTitle(database)));
            this.database = database;
            this.orderPropertyId = orderPropertyId.map(String::trim).filter(not(String::isBlank)).orElse(null);
            this.groupPropertyId = groupPropertyId.map(String::trim).filter(not(String::isBlank)).orElse(null);
            this.groups = groups;
        }

        @Override
        List<PageGroup> group(List<PageComposite> pages) {
            if (groupPropertyId != null && !groups.isEmpty()) {
                var pagesPerGroupId = pages.stream().collect(groupingBy(page -> page.getPage().getGroupId().orElse("")));
                var index = new AtomicInteger(1);
                var groupOfPages = groups.stream()
                        .map(group -> new PageGroup(group.getId(), group.getName(), index.getAndIncrement(), false, pagesPerGroupId.getOrDefault(group.getId(), emptyList())))
                        .sorted()
                        .collect(toList());

                var pagesWithoutGroupId = pagesPerGroupId.getOrDefault("", emptyList());
                var groups = new ArrayList<>(groupOfPages);
                if (!pagesWithoutGroupId.isEmpty()) {
                    LOG.warn("{} pages are added to a group of page that does not have any title", pagesWithoutGroupId.size());
                    groups.add(new PageGroup("none", "", MAX_VALUE, false, pagesWithoutGroupId));
                }
                LOG.info("{} groups found", groups.size());
                return groups;
            } else {
                return createDefaultGroup(pages);
            }
        }

        @Override
        boolean includesChildPages() {
            return false;
        }

        @Override
        List<PageDetail> loadPages(NotionRepository repository, String token) {
            final var orderSequence = new AtomicInteger(-10000);
            return repository.findPagesInDatabase(getId(), token).stream().map(page -> {
                var title = repository.findPageTitle(page.getId(), token);
                var order = ofNullable(orderPropertyId).map(id -> repository.findPageOrder(page.getId(), id, token)).orElseGet(orderSequence::incrementAndGet);
                var groupId = ofNullable(groupPropertyId).flatMap(id -> repository.findPageGroupId(page.getId(), id, token)).orElse(null);
                return new PageDetail(page, title, order, groupId);
            }).collect(toList());
        }
    }

    static class PageRoot extends Root {
        private final Page page;
        private final boolean includeNestedPages;

        public PageRoot(String givenTitle, Page page, Optional<String> pageTitle, boolean includeNestedPages) {
            super(page.getId(), ofNullable(givenTitle).filter(not(String::isBlank)).orElseGet(() -> pageTitle.orElse("")));
            this.page = page;
            this.includeNestedPages = includeNestedPages;
        }

        @Override
        List<PageGroup> group(List<PageComposite> pages) {
            return createDefaultGroup(pages);
        }

        @Override
        boolean includesChildPages() {
            return includeNestedPages;
        }

        @Override
        List<PageDetail> loadPages(NotionRepository repository, String token) {
            var page = repository.findPage(getId(), token);
            var title = repository.findPageTitle(page.getId(), token);
            return singletonList(new PageDetail(page, title, 1, null));
        }
    }
}
