package be.looorent.notion.core;

import notion.api.v1.model.blocks.ToDoBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class ToDo extends DocumentChunk {
    private final ToDoBlock block;
    private final List<DocumentChunk> children;

    public ToDo(ToDoBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children;
    }

    public List<PageProperty.RichText> getRichText() {
        return block.getToDo().getRichText();
    }

    public boolean isChecked() {
        return block.getToDo().getChecked();
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
