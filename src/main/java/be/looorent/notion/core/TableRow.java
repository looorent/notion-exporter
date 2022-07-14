package be.looorent.notion.core;

import notion.api.v1.model.blocks.TableRowBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;

public class TableRow extends DocumentChunk {
    private final TableRowBlock block;

    public TableRow(TableRowBlock block) {
        super(block.getId());
        this.block = block;
    }

    public List<List<PageProperty.RichText>> getCells() {
        return block.getTableRow().getCells();
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }

    public TableRowBlock getBlock() {
        return block;
    }
}
