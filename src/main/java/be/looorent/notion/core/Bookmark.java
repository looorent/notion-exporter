package be.looorent.notion.core;

import notion.api.v1.model.blocks.BookmarkBlock;
import notion.api.v1.model.pages.PageProperty;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Bookmark extends DocumentChunk {
    private final BookmarkBlock block;

    public Bookmark(BookmarkBlock block) {
        super(block.getId());
        this.block = block;
    }

    public Optional<String> getUrl() {
        return ofNullable(block.getBookmark()).map(BookmarkBlock.Element::getUrl);
    }

    public Optional<List<PageProperty.RichText>> getCaption() {
        return ofNullable(block.getBookmark()).map(BookmarkBlock.Element::getCaption);
    }

    @Override
    public void accept(ChunkVisitor visitor) {
        visitor.visitBefore(this);
        visitor.visitAfter(this);
    }
}
