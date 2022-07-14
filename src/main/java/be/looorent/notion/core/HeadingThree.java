package be.looorent.notion.core;

import notion.api.v1.model.blocks.HeadingThreeBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class HeadingThree extends DocumentChunk {
    private final HeadingThreeBlock block;

    public HeadingThree(HeadingThreeBlock block) {
        super(block.getId());
        this.block = block;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getHeading3().getRichText();
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
