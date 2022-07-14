package be.looorent.notion.core;

import notion.api.v1.model.blocks.TableOfContentsBlock;

public class TableOfContent extends DocumentChunk {
    private final TableOfContentsBlock block;

    public TableOfContent(TableOfContentsBlock block) {
        super(block.getId());
        this.block = block;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }

    public TableOfContentsBlock getBlock() {
        return block;
    }
}
