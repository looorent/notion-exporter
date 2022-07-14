package be.looorent.notion.core;

import java.util.Collection;
import java.util.HashSet;

public class ReferenceVisitor implements ChunkVisitor {

    private final Collection<Reference> references;

    public ReferenceVisitor() {
        this.references = new HashSet<>();
    }

    public ReferenceContainer getReferences() {
        return new ReferenceContainer(this.references);
    }

    @Override
    public void visitBefore(Audio audio) {}

    @Override
    public void visitAfter(Audio audio) {}

    @Override
    public void visitBefore(Bookmark bookmark) {}

    @Override
    public void visitAfter(Bookmark bookmark) {}

    @Override
    public void visitBefore(Breadcrumb breadcrumb) {}

    @Override
    public void visitAfter(Breadcrumb breadcrumb) {}

    @Override
    public void visitBefore(BulletedList bulletedList) {}

    @Override
    public void visitAfter(BulletedList bulletedList) {}

    @Override
    public void visitBefore(Callout callout) {}

    @Override
    public void visitAfter(Callout callout) {}

    @Override
    public void visitBefore(ChildDatabase childDatabase) {}

    @Override
    public void visitAfter(ChildDatabase childDatabase) {}

    @Override
    public void visitBefore(ChildPage childPage) {
        this.references.add(new Reference(childPage));
    }

    @Override
    public void visitAfter(ChildPage childPage) {}

    @Override
    public void visitBefore(Code code) {}

    @Override
    public void visitAfter(Code code) {}

    @Override
    public void visitBefore(Column column) {}

    @Override
    public void visitAfter(Column column) {}

    @Override
    public void visitBefore(ColumnList columnList) {}

    @Override
    public void visitAfter(ColumnList columnList) {}

    @Override
    public void visitBefore(Divider divider) {}

    @Override
    public void visitAfter(Divider divider) {}

    @Override
    public void visitBefore(Embed embed) {}

    @Override
    public void visitAfter(Embed embed) {}

    @Override
    public void visitBefore(Equation equation) {}

    @Override
    public void visitAfter(Equation equation) {}

    @Override
    public void visitBefore(File file) {}

    @Override
    public void visitAfter(File file) {}

    @Override
    public void visitBefore(HeadingOne headingOne) {
    }

    @Override
    public void visitAfter(HeadingOne headingOne) {}

    @Override
    public void visitBefore(HeadingTwo headingTwo) {}

    @Override
    public void visitAfter(HeadingTwo headingTwo) {}

    @Override
    public void visitBefore(HeadingThree headingThree) {}

    @Override
    public void visitAfter(HeadingThree headingThree) {}

    @Override
    public void visitBefore(Image image) {}

    @Override
    public void visitAfter(Image image) {}

    @Override
    public void visitBefore(LinkPreview linkPreview) {}

    @Override
    public void visitAfter(LinkPreview linkPreview) {}

    @Override
    public void visitBefore(LinkToPage linkToPage) {}

    @Override
    public void visitAfter(LinkToPage linkToPage) {}

    @Override
    public void visitBefore(NumberedList numberedList) {}

    @Override
    public void visitAfter(NumberedList numberedList) {}

    @Override
    public void visitBefore(PageComposite pageComposite) {
        this.references.add(new Reference(pageComposite));
    }

    @Override
    public void visitAfter(PageComposite pageComposite) {}

    @Override
    public void visitBefore(Paragraph paragraph) {}

    @Override
    public void visitAfter(Paragraph paragraph) {}

    @Override
    public void visitBefore(Pdf pdf) {}

    @Override
    public void visitAfter(Pdf pdf) {}

    @Override
    public void visitBefore(Quote quote) {}

    @Override
    public void visitAfter(Quote quote) {}

    @Override
    public void visitBefore(Synced synced) {}

    @Override
    public void visitAfter(Synced synced) {}

    @Override
    public void visitBefore(Table table) {}

    @Override
    public void visitAfter(Table table) {}

    @Override
    public void visitBefore(TableRow tableRow) {}

    @Override
    public void visitAfter(TableRow tableRow) {}

    @Override
    public void visitBefore(TableOfContent tableOfContent) {}

    @Override
    public void visitAfter(TableOfContent tableOfContent) {}

    @Override
    public void visitBefore(Template template) {}

    @Override
    public void visitAfter(Template template) {}

    @Override
    public void visitBefore(ToDo toDo) {}

    @Override
    public void visitAfter(ToDo toDo) {}

    @Override
    public void visitBefore(Toggle toggle) {}

    @Override
    public void visitAfter(Toggle toggle) {}

    @Override
    public void visitBefore(Unsupported unsupported) {}

    @Override
    public void visitAfter(Unsupported unsupported) {}

    @Override
    public void visitBefore(Video video) {}

    @Override
    public void visitAfter(Video video) {}

    @Override
    public void visitBefore(PageGroup pageGroup) {}

    @Override
    public void visitAfter(PageGroup pageGroup) {}

    @Override
    public void visitBefore(Document document) {}

    @Override
    public void visitAfter(Document document) {}
}
