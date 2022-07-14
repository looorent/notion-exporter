package be.looorent.notion.core;

import notion.api.v1.model.blocks.ChildDatabaseBlock;

import java.util.List;

public class ChildDatabase extends DocumentChunk {
    private final ChildDatabaseBlock block;
    private final List<DocumentChunk> children;

    public ChildDatabase(ChildDatabaseBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public String getTitle() {
        return block.getChildDatabase().getTitle();
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
}
