package be.looorent.notion.core;

import notion.api.v1.model.blocks.ToggleBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class Toggle extends DocumentChunk {
    private final ToggleBlock block;
    private final List<DocumentChunk> children;

    public Toggle(ToggleBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getToggle().getRichText();
    }

    public List<DocumentChunk> getChildren() {
        // the block itself has a List<Block>, we ignore it
        return children;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }
}
