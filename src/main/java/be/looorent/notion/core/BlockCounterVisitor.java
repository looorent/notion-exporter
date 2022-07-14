package be.looorent.notion.core;

public class BlockCounterVisitor implements ChunkVisitor {
    private int numberOfBlocks = 0;
    private int numberOfPages = 0;
    private int numberOfGroups = 0;

    @Override
    public void visitBefore(Audio audio) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Audio audio) {}

    @Override
    public void visitBefore(Bookmark bookmark) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Bookmark bookmark) {}

    @Override
    public void visitBefore(Breadcrumb breadcrumb) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Breadcrumb breadcrumb) {}

    @Override
    public void visitBefore(BulletedList bulletedList) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(BulletedList bulletedList) {}

    @Override
    public void visitBefore(Callout callout) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Callout callout) {}

    @Override
    public void visitBefore(ChildDatabase childDatabase) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(ChildDatabase childDatabase) {}

    @Override
    public void visitBefore(ChildPage childPage) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(ChildPage childPage) {}

    @Override
    public void visitBefore(Code code) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Code code) {}

    @Override
    public void visitBefore(Column column) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Column column) {}

    @Override
    public void visitBefore(ColumnList columnList) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(ColumnList columnList) {}

    @Override
    public void visitBefore(Divider divider) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Divider divider) {}

    @Override
    public void visitBefore(Embed embed) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Embed embed) {}

    @Override
    public void visitBefore(Equation equation) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Equation equation) {}

    @Override
    public void visitBefore(File file) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(File file) {}

    @Override
    public void visitBefore(HeadingOne headingOne) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(HeadingOne headingOne) {}

    @Override
    public void visitBefore(HeadingTwo headingTwo) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(HeadingTwo headingTwo) {}

    @Override
    public void visitBefore(HeadingThree headingThree) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(HeadingThree headingThree) {}

    @Override
    public void visitBefore(Image image) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Image image) {}

    @Override
    public void visitBefore(LinkPreview linkPreview) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(LinkPreview linkPreview) {}

    @Override
    public void visitBefore(LinkToPage linkToPage) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(LinkToPage linkToPage) {}

    @Override
    public void visitBefore(NumberedList numberedList) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(NumberedList numberedList) {}

    @Override
    public void visitBefore(PageComposite pageComposite) {
        numberOfPages++;
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(PageComposite pageComposite) {}

    @Override
    public void visitBefore(Paragraph paragraph) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Paragraph paragraph) {}

    @Override
    public void visitBefore(Pdf pdf) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Pdf pdf) {}

    @Override
    public void visitBefore(Quote quote) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Quote quote) {}

    @Override
    public void visitBefore(Synced synced) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Synced synced) {}

    @Override
    public void visitBefore(Table table) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Table table) {}

    @Override
    public void visitBefore(TableRow tableRow) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(TableRow tableRow) {}

    @Override
    public void visitBefore(TableOfContent tableOfContent) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(TableOfContent tableOfContent) {}

    @Override
    public void visitBefore(Template template) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Template template) {}

    @Override
    public void visitBefore(ToDo toDo) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(ToDo toDo) {}

    @Override
    public void visitBefore(Toggle toggle) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Toggle toggle) {}

    @Override
    public void visitBefore(Unsupported unsupported) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Unsupported unsupported) {}

    @Override
    public void visitBefore(Video video) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Video video) {}

    @Override
    public void visitBefore(PageGroup pageGroup) {
        numberOfGroups++;
    }

    @Override
    public void visitAfter(PageGroup pageGroup) {}

    @Override
    public void visitBefore(Document document) {
        numberOfBlocks++;
    }

    @Override
    public void visitAfter(Document document) {}

    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public int getNumberOfGroups() {
        return numberOfGroups;
    }
}
