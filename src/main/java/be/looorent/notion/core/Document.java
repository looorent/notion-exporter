package be.looorent.notion.core;

import java.util.List;

public class Document extends DocumentChunk {
    private final String title;
    private final List<PageGroup> groups;

    public Document(String id,
                    String title,
                    List<PageGroup> groups) {
        super(id);
        this.title = title;
        this.groups = groups.stream().sorted().toList();
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        groups.forEach(group -> group.accept(visitor));
        visitor.visitAfter(this);
    }
}
