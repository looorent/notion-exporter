package be.looorent.notion.core;

import java.util.List;

public class PageComposite extends DocumentChunk implements Comparable<PageComposite> {
    private final PageDetail page;
    private final int order;
    private final String title;
    private final List<DocumentChunk> children;

    public PageComposite(PageDetail page, String title, int order, List<DocumentChunk> children) {
        super(page.getId());
        this.page = page;
        this.title = title;
        this.children = children;
        this.order = order;
    }

    public PageDetail getPage() {
        return page;
    }

    public List<DocumentChunk> getChildren() {
        return children;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int compareTo(PageComposite other) {
        return order - other.order;
    }
}
