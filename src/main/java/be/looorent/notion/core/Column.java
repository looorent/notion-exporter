package be.looorent.notion.core;

import notion.api.v1.model.blocks.ColumnBlock;

import java.util.List;

public class Column extends DocumentChunk {
    private final ColumnBlock block;
    private final List<DocumentChunk> children;

    public Column(ColumnBlock block, List<DocumentChunk> children) {
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

    public ColumnBlock getBlock() {
        return block;
    }
}
