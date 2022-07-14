package be.looorent.notion.core;

import notion.api.v1.model.blocks.HeadingTwoBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class HeadingTwo extends DocumentChunk {
    private final HeadingTwoBlock block;

    public HeadingTwo(HeadingTwoBlock block) {
        super(block.getId());
        this.block = block;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getHeading2().getRichText();
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
