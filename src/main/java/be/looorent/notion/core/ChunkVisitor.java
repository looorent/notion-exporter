package be.looorent.notion.core;

public interface ChunkVisitor {
    void visitBefore(Audio audio);
    void visitAfter(Audio audio);

    void visitBefore(Bookmark bookmark);
    void visitAfter(Bookmark bookmark);

    void visitBefore(Breadcrumb breadcrumb);
    void visitAfter(Breadcrumb breadcrumb);

    void visitBefore(BulletedList bulletedList);
    void visitAfter(BulletedList bulletedList);

    void visitBefore(Callout callout);
    void visitAfter(Callout callout);

    void visitBefore(ChildDatabase childDatabase);
    void visitAfter(ChildDatabase childDatabase);

    void visitBefore(ChildPage childPage);
    void visitAfter(ChildPage childPage);

    void visitBefore(Code code);
    void visitAfter(Code code);

    void visitBefore(Column column);
    void visitAfter(Column column);

    void visitBefore(ColumnList columnList);
    void visitAfter(ColumnList columnList);

    void visitBefore(Divider divider);
    void visitAfter(Divider divider);

    void visitBefore(Embed embed);
    void visitAfter(Embed embed);

    void visitBefore(Equation equation);
    void visitAfter(Equation equation);

    void visitBefore(File file);
    void visitAfter(File file);

    void visitBefore(HeadingOne headingOne);
    void visitAfter(HeadingOne headingOne);

    void visitBefore(HeadingTwo headingTwo);
    void visitAfter(HeadingTwo headingTwo);

    void visitBefore(HeadingThree headingThree);
    void visitAfter(HeadingThree headingThree);

    void visitBefore(Image image);
    void visitAfter(Image image);

    void visitBefore(LinkPreview linkPreview);
    void visitAfter(LinkPreview linkPreview);

    void visitBefore(LinkToPage linkToPage);
    void visitAfter(LinkToPage linkToPage);

    void visitBefore(NumberedList numberedList);
    void visitAfter(NumberedList numberedList);

    void visitBefore(PageComposite pageComposite);
    void visitAfter(PageComposite pageComposite);

    void visitBefore(Paragraph paragraph);
    void visitAfter(Paragraph paragraph);

    void visitBefore(Pdf pdf);
    void visitAfter(Pdf pdf);

    void visitBefore(Quote quote);
    void visitAfter(Quote quote);

    void visitBefore(Synced synced);
    void visitAfter(Synced synced);

    void visitBefore(Table table);
    void visitAfter(Table table);

    void visitBefore(TableRow tableRow);
    void visitAfter(TableRow tableRow);

    void visitBefore(TableOfContent tableOfContent);
    void visitAfter(TableOfContent tableOfContent);

    void visitBefore(Template template);
    void visitAfter(Template template);

    void visitBefore(ToDo toDo);
    void visitAfter(ToDo toDo);

    void visitBefore(Toggle toggle);
    void visitAfter(Toggle toggle);

    void visitBefore(Unsupported unsupported);
    void visitAfter(Unsupported unsupported);

    void visitBefore(Video video);
    void visitAfter(Video video);

    void visitBefore(PageGroup pageGroup);
    void visitAfter(PageGroup pageGroup);

    void visitBefore(Document document);
    void visitAfter(Document document);
}
