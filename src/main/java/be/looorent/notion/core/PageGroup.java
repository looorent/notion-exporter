package be.looorent.notion.core;

import java.util.List;

import static java.util.UUID.randomUUID;

public class PageGroup extends DocumentChunk implements Comparable<PageGroup> {
    private final String title;
    private final int order;
    private final boolean ignored;
    private final List<PageComposite> pages;

    public static PageGroup noGroup(List<PageComposite> pages) {
        return new PageGroup(randomUUID().toString(), "no-group", 0, true, pages);
    }

    public PageGroup(String id,
                     String title,
                     int order,
                     boolean ignored,
                     List<PageComposite> pages) {
        super(id);
        this.title = title;
        this.order = order;
        this.ignored = ignored;
        this.pages = pages.stream().sorted().toList();
    }

    public String getTitle() {
        return title;
    }

    public int getOrder() {
        return order;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public List<PageComposite> getPages() {
        return pages;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        pages.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }

    @Override
    public int compareTo(PageGroup other) {
        return order - other.order;
    }
}
