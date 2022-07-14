package be.looorent.notion.core;

import notion.api.v1.model.pages.Page;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class PageDetail {
    private final Page page;
    private final String title;
    private final int order;
    private final String groupId;

    public PageDetail(Page page,
                      String title,
                      int order,
                      String groupId) {
        this.page = page;
        this.title = title;
        this.order = order;
        this.groupId = groupId;
    }

    public String getId() {
        return page.getId();
    }

    public Page getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public int getOrder() {
        return order;
    }

    public Optional<String> getGroupId() {
        return ofNullable(groupId);
    }
}
