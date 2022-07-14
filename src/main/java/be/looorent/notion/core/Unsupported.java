package be.looorent.notion.core;

import notion.api.v1.model.blocks.UnsupportedBlock;

public class Unsupported extends DocumentChunk {
    private final UnsupportedBlock block;

    public Unsupported(UnsupportedBlock block) {
        super(block.getId());
        this.block = block;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }

    public UnsupportedBlock getBlock() {
        return block;
    }
}
