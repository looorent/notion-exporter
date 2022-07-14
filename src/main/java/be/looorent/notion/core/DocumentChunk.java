package be.looorent.notion.core;

import java.util.Objects;

public abstract class DocumentChunk {
    private final String id;

    public DocumentChunk(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DocumentChunk composite = (DocumentChunk) other;
        return Objects.equals(id, composite.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract void accept(ChunkVisitor visitor);

    public int getNumberOfBlocks() {
        var visitor = new BlockCounterVisitor();
        accept(visitor);
        return visitor.getNumberOfBlocks();
    }

    public int getNumberOfPages() {
        var visitor = new BlockCounterVisitor();
        accept(visitor);
        return visitor.getNumberOfPages();
    }

    public int getNumberOfGroups() {
        var visitor = new BlockCounterVisitor();
        accept(visitor);
        return visitor.getNumberOfGroups();
    }
}
