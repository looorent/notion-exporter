package be.looorent.notion.core;

import notion.api.v1.model.blocks.TableBlock;

import java.util.List;

public class Table extends DocumentChunk {
    private final TableBlock block;
    private final List<TableRow> children;

    public Table(TableBlock block, List<DocumentChunk> children) {
        super(block.getId());
        this.block = block;
        this.children = children.stream()
                .filter(child -> child instanceof TableRow)
                .map(TableRow.class::cast)
                .toList();
    }

    public int getTableWidth() {
        return block.getTable().getTableWidth();
    }

    public boolean hasColumnHeader() {
        return block.getTable().getHasColumnHeader();
    }

    public boolean hasRowHeader() {
        return block.getTable().getHasRowHeader();
    }

    public List<TableRow> getChildren() {
        return children;
    }

    public TableBlock getBlock() {
        return block;
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        children.forEach(child -> child.accept(visitor));
        visitor.visitAfter(this);
    }
}
