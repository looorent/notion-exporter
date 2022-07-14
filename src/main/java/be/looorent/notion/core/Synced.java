package be.looorent.notion.core;

import notion.api.v1.model.blocks.SyncedBlock;

import java.util.List;

public class Synced extends DocumentChunk {
    private final SyncedBlock block;
    private final List<DocumentChunk> children;

    public Synced(SyncedBlock block, List<DocumentChunk> children) {
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

    public SyncedBlock getBlock() {
        return block;
    }
}
