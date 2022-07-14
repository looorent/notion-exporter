package be.looorent.notion.core;

import notion.api.v1.model.blocks.DividerBlock;

public class Divider extends DocumentChunk {
    private final DividerBlock block;

    public Divider(DividerBlock block) {
        super(block.getId());
        this.block = block;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }

    public DividerBlock getBlock() {
        return block;
    }
}
