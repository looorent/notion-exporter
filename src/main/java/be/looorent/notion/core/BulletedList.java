package be.looorent.notion.core;

import notion.api.v1.model.blocks.BulletedListItemBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class BulletedList extends DocumentChunk {
    private final BulletedListItemBlock block;
    private final int number;
    private final List<DocumentChunk> children;

    public BulletedList(BulletedListItemBlock block, int number, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.number = number;
        this.children = children;
    }

    public int getNumber() {
        return number;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getBulletedListItem().getRichText();
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

    public boolean isFirstInList() {
        return number == 1;
    }
}
