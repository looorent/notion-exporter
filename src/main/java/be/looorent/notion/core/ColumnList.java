package be.looorent.notion.core;

import notion.api.v1.model.blocks.ColumnListBlock;

import java.util.List;

public class ColumnList extends DocumentChunk {
    private final ColumnListBlock block;
    private final List<DocumentChunk> children;

    public ColumnList(ColumnListBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
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

    public ColumnListBlock getBlock() {
        return block;
    }
}
