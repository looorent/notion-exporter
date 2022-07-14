package be.looorent.notion.core;

import notion.api.v1.model.blocks.ChildPageBlock;

import java.util.List;

public class ChildPage extends DocumentChunk {
    private final ChildPageBlock block;
    private final List<DocumentChunk> children;

    public ChildPage(ChildPageBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public String getTitle() {
        return block.getChildPage().getTitle();
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }


}
