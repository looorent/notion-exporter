package be.looorent.notion.core;

import notion.api.v1.model.blocks.HeadingOneBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class HeadingOne extends DocumentChunk {
    private final HeadingOneBlock block;

    public HeadingOne(HeadingOneBlock block) {
        super(block.getId());
        this.block = block;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getHeading1().getRichText();
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
