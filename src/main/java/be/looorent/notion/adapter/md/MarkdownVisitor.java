package be.looorent.notion.adapter.md;

import be.looorent.notion.core.*;
import notion.api.v1.model.blocks.TableRowBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static be.looorent.notion.adapter.md.MarkdownFormatter.*;
import static be.looorent.notion.adapter.md.MarkdownLine.emptyLine;
import static be.looorent.notion.adapter.md.MarkdownLine.mdLine;
import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

class MarkdownVisitor implements ChunkVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownVisitor.class);
    private static final String FILE_NO_CAPTION = "file-without-caption";
    private static final String IMAGE_NO_CAPTION = "image-without-caption";
    private static final String PDF_NO_CAPTION = "pdf-without-caption";
    private static final String VIDEO_NO_CAPTION = "video-without-caption";
    private static final String AUDIO_NO_CAPTION = "audio-without-caption";
    private static final String REFERENCED_FAILED = "referenced-failed";
    private static final String DIVIDER = "---";
    private static final String CODE_BLOCK = "```";
    private static final String CALLOUT_BEGIN_BLOCK = "<aside>";
    private static final String CALLOUT_END_BLOCK = "</aside>";

    private final List<MarkdownLine> lines;
    private final ReferenceContainer references;
    private final AtomicInteger indentation;
    private final AtomicInteger headingLevel;

    public MarkdownVisitor(ReferenceContainer references) {
        this.lines = new ArrayList<>();
        this.indentation = new AtomicInteger(0);
        this.headingLevel = new AtomicInteger(1);
        this.references = references;
    }

    @Override
    public void visitBefore(Audio audio) {
        LOG.debug("Format block {} of type 'audio' into Markdown", audio.getId());
        breakLine();
        audio.getFile().ifPresentOrElse(
                file -> addLine("["+formatCaption(audio.getCaption(), AUDIO_NO_CAPTION)+"]("+file.getUrl()+")"),
                () -> LOG.warn("No URL for Audio block '{}'", audio.getId()));
    }

    @Override
    public void visitAfter(Audio audio) {}

    @Override
    public void visitBefore(Bookmark bookmark) {
        LOG.debug("Format block {} of type 'bookmark' into Markdown", bookmark.getId());
        bookmark.getUrl().ifPresentOrElse(url -> {
            var caption = formatCaption(bookmark.getCaption().orElse(emptyList()), url);
            addLine("["+caption+"]("+url+")");
        }, () -> LOG.warn("No URL found for bookmark block '{}'", bookmark.getId()));
    }

    @Override
    public void visitAfter(Bookmark bookmark) {}

    @Override
    public void visitBefore(Breadcrumb breadcrumb) {
        LOG.debug("Format block {} of type 'breadcrumb' into Markdown", breadcrumb.getId());
        breakLine();
    }

    @Override
    public void visitAfter(Breadcrumb breadcrumb) {}

    @Override
    public void visitBefore(BulletedList bulletedList) {
        LOG.debug("Format block {} of type 'bulletedList' into Markdown", bulletedList.getId());
        if (bulletedList.isFirstInList()) {
            breakLine();
        }
        addLine(formatUnorderedItem(bulletedList.getRichText(), references));
        indent();
    }

    @Override
    public void visitAfter(BulletedList bulletedList) {
        outdent();
    }

    @Override
    public void visitBefore(Callout callout) {
        LOG.debug("Format block {} of type 'callout' into Markdown", callout.getId());
        addLine(CALLOUT_BEGIN_BLOCK);
        addLine(format(callout.getRichText(), callout.getIcon(), references));
        indent();
    }

    @Override
    public void visitAfter(Callout callout) {
        outdent();
        addLine(CALLOUT_END_BLOCK);
        breakLine();
    }

    @Override
    public void visitBefore(ChildDatabase childDatabase) {
        LOG.warn("Block 'child database' not implemented for id = '{}'", childDatabase.getId());
    }

    @Override
    public void visitAfter(ChildDatabase childDatabase) {
        breakLine();
    }

    @Override
    public void visitBefore(ChildPage childPage) {
        LOG.debug("Format block {} of type 'childPage' into Markdown", childPage.getId());
        breakLine();
        addLine(formatHeading(childPage.getTitle(), headingLevel.get()));
        headingLevel.incrementAndGet();
    }

    @Override
    public void visitAfter(ChildPage childPage) {
        headingLevel.decrementAndGet();
        breakLine();
    }

    @Override
    public void visitBefore(Code code) {
        LOG.debug("Format block {} of type 'code' into Markdown", code.getId());
        addLine(CODE_BLOCK);
        addLine(format(code.getRichText(), references));
        addLine(CODE_BLOCK);
        // caption is not supported
    }

    @Override
    public void visitAfter(Code code) {}

    @Override
    public void visitBefore(Column column) {
        LOG.debug("Format block {} of type 'column' into Markdown", column.getId());
        breakLine();
        indent();
    }

    @Override
    public void visitAfter(Column column) {
        outdent();
        breakLine();
    }

    @Override
    public void visitBefore(ColumnList columnList) {
        LOG.debug("Format block {} of type 'columnList' into Markdown", columnList.getId());
        breakLine();
        indent();
    }

    @Override
    public void visitAfter(ColumnList columnList) {
        outdent();
        breakLine();
    }

    @Override
    public void visitBefore(Divider divider) {
        LOG.debug("Format block {} of type 'divider' into Markdown", divider.getId());
        breakLine();
        addLine(DIVIDER);
        breakLine();
    }

    @Override
    public void visitAfter(Divider divider) {}

    @Override
    public void visitBefore(Embed embed) {
        LOG.warn("Block 'embed' not implemented for id = '{}'", embed.getId());
        breakLine();
    }

    @Override
    public void visitAfter(Embed embed) {}

    @Override
    public void visitBefore(Equation equation) {
        LOG.debug("Format block {} of type 'equation' into Markdown", equation.getId());
        equation.getExpression().ifPresent(expression -> addLine(addCodeTo(expression)));
    }

    @Override
    public void visitAfter(Equation equation) {}

    @Override
    public void visitBefore(File file) {
        LOG.debug("Format block {} of type 'file' into Markdown", file.getId());
        file.getExternal().ifPresent(external -> addLine("["+formatCaption(file.getCaption(), FILE_NO_CAPTION)+"]("+external.getUrl()+")"));
        // TODO do better
    }

    @Override
    public void visitAfter(File file) {}

    @Override
    public void visitBefore(HeadingOne headingOne) {
        LOG.debug("Format block {} of type 'heading1' into Markdown", headingOne.getId());
        breakLine();
        addLine(formatHeading(format(headingOne.getRichText(), references), headingLevel.get()));
    }

    @Override
    public void visitAfter(HeadingOne headingOne) {}

    @Override
    public void visitBefore(HeadingTwo headingTwo) {
        LOG.debug("Format block {} of type 'heading2' into Markdown", headingTwo.getId());
        breakLine();
        addLine(formatHeading(format(headingTwo.getRichText(), references), headingLevel.get() + 1));
    }

    @Override
    public void visitAfter(HeadingTwo headingTwo) {}

    @Override
    public void visitBefore(HeadingThree headingThree) {
        LOG.debug("Format block {} of type 'heading3' into Markdown", headingThree.getId());
        breakLine();
        addLine(formatHeading(format(headingThree.getRichText(), references), headingLevel.get() + 2));
    }

    @Override
    public void visitAfter(HeadingThree headingThree) {}

    @Override
    public void visitBefore(Image image) {
        LOG.debug("Format block {} of type 'image' into Markdown", image.getId());
        image.getAsset().ifPresentOrElse(asset -> {
            var caption = formatCaption(image.getCaption(), IMAGE_NO_CAPTION);
            addLine(formatImage(caption, asset.getRelativePath()));
        }, () -> LOG.warn("No path found for the image block '{}'", image.getId()));
    }

    @Override
    public void visitAfter(Image image) {}

    @Override
    public void visitBefore(LinkPreview linkPreview) {
        LOG.warn("Block 'link preview' not supported: {}", linkPreview.getId());
        breakLine();
    }

    @Override
    public void visitAfter(LinkPreview linkPreview) {}

    @Override
    public void visitBefore(LinkToPage linkToPage) {
        LOG.debug("Format block {} of type 'linkToPage' into Markdown", linkToPage.getId());
        var text = linkToPage.getPageId()
                .flatMap(references::findReference)
                .map(MarkdownFormatter::format)
                .orElseGet(() -> {
                    LOG.error("'Link to Page' to {} has not been found.", linkToPage.getPageId());
                    return REFERENCED_FAILED;
                });
        addLine(text);
    }

    @Override
    public void visitAfter(LinkToPage linkToPage) {}

    @Override
    public void visitBefore(NumberedList numberedList) {
        LOG.debug("Format block {} of type 'numberedList' into Markdown", numberedList.getId());
        if (numberedList.isFirstInList()) {
            breakLine();
        }
        addLine(formatOrderedItem(numberedList.getNumber(), numberedList.getRichText(), references));
        indent();
    }

    @Override
    public void visitAfter(NumberedList numberedList) {
        outdent();
    }

    @Override
    public void visitBefore(PageComposite pageComposite) {
        breakLine();
        addLine(formatComment(pageComposite.getPage().getPage().getUrl()));
        addLine(formatHeading(pageComposite.getTitle(), headingLevel.get()));
        headingLevel.incrementAndGet();
    }

    @Override
    public void visitAfter(PageComposite pageComposite) {
        headingLevel.decrementAndGet();
        breakLine();
    }

    @Override
    public void visitBefore(Paragraph paragraph) {
        LOG.debug("Format block {} of type 'paragraph' into Markdown", paragraph.getId());
        addLine(format(paragraph.getRichText(), references));
        indent();
    }

    @Override
    public void visitAfter(Paragraph paragraph) {
        outdent();
    }

    @Override
    public void visitBefore(Pdf pdf) {
        LOG.debug("Format block {} of type 'pdf' into Markdown", pdf.getId());
        var line = pdf.getAsset()
                .map(asset -> mdLine("["+formatCaption(pdf.getCaption(), PDF_NO_CAPTION)+"]("+asset.getRelativePath()+")"))
                .orElseGet(() -> {
                    LOG.error("PDF not found for block '{}'", pdf.getId());
                    return emptyLine();
                });
        lines.add(line);
    }

    @Override
    public void visitAfter(Pdf pdf) {}

    @Override
    public void visitBefore(Quote quote) {
        LOG.debug("Format block {} of type 'quote' into Markdown", quote.getId());
        addLine("> "+ format(quote.getRichText(), references));
        indent();
    }

    @Override
    public void visitAfter(Quote quote) {
        outdent();
    }

    @Override
    public void visitBefore(Synced synced) {
        LOG.debug("Format block {} of type 'syncedBlock' into Markdown", synced.getId());
    }

    @Override
    public void visitAfter(Synced synced) {
        breakLine();
    }

    @Override
    public void visitBefore(Table table) {
        LOG.debug("Format block {} of type 'table' into Markdown", table.getId());

        var rowBlocks = table.getChildren()
                .stream()
                .map(TableRow::getBlock)
                .toList();

        if (rowBlocks.isEmpty()) {
            breakLine();
        } else {
            lines.add(toRow(rowBlocks.get(0), references));
            lines.add(toHeader(table.getTableWidth()));
            if (lines.size() > 1) {
                lines.addAll(rowBlocks.subList(1, rowBlocks.size()).stream().map(row -> toRow(row, references)).toList());
            }
        }
    }

    private MarkdownLine toHeader(int numberOfColumns) {
        return mdLine("| " + generate(() -> "---").limit(numberOfColumns).collect(joining(" | ")) + " |");
    }

    private MarkdownLine toRow(TableRowBlock block, ReferenceContainer references) {
        var cells = block.getTableRow()
                .getCells()
                .stream()
                .map(cell -> format(cell, references)).toList();
        return mdLine("| " + join(" | ", cells) + " |");
    }

    @Override
    public void visitAfter(Table table) {
        breakLine();
    }

    @Override
    public void visitBefore(TableRow tableRow) {
        // Format of type 'tableRow' into Markdown. This is not supposed to happen considering everything is managed at the Table level
    }

    @Override
    public void visitAfter(TableRow tableRow) {}

    @Override
    public void visitBefore(TableOfContent tableOfContent) {
        LOG.warn("Block 'table fo content' not supported: {}", tableOfContent.getId());
    }

    @Override
    public void visitAfter(TableOfContent tableOfContent) {}

    @Override
    public void visitBefore(Template template) {
        LOG.warn("Block 'template' not implemented for id = '{}'", template.getId());
        indent();
    }

    @Override
    public void visitAfter(Template template) {
        outdent();
    }

    @Override
    public void visitBefore(ToDo toDo) {
        LOG.debug("Format block {} of type 'toDo' into Markdown", toDo.getId());
        addLine("* ["+(toDo.isChecked() ? "x" : " ")+"] " + format(toDo.getRichText(), references));
        indent();
    }

    @Override
    public void visitAfter(ToDo toDo) {
        outdent();
        breakLine();
    }

    @Override
    public void visitBefore(Toggle toggle) {
        LOG.debug("Format block {} of type 'toggle' into Markdown", toggle.getId());
        addLine("* " + format(toggle.getRichText(), references));
        indent();
    }

    @Override
    public void visitAfter(Toggle toggle) {
        outdent();
        breakLine();
    }

    @Override
    public void visitBefore(Unsupported unsupported) {
        LOG.warn("Block 'unsupported' not implemented for id = '{}'", unsupported.getId());
        breakLine();
    }

    @Override
    public void visitAfter(Unsupported unsupported) {}

    @Override
    public void visitBefore(Video video) {
        LOG.debug("Format block {} of type 'video' into Markdown", video.getId());
        video.getExternal().ifPresentOrElse(
                external -> addLine("["+formatCaption(video.getCaption(), VIDEO_NO_CAPTION)+"]("+external.getUrl()+")"),
                () -> LOG.warn("No URL for Video block '{}'", video.getId()));
    }

    @Override
    public void visitAfter(Video video) {}

    @Override
    public void visitBefore(PageGroup pageGroup) {
        if (!pageGroup.isIgnored()) {
            addLine(formatHeading(pageGroup.getTitle(), headingLevel.get()));
            headingLevel.incrementAndGet();
        } else {
            LOG.debug("Ignore page group '{}'", pageGroup.getTitle());
        }
    }

    @Override
    public void visitAfter(PageGroup pageGroup) {
        if (!pageGroup.isIgnored()) {
            headingLevel.decrementAndGet();
        }
    }

    @Override
    public void visitBefore(Document document) {
        addLine(formatComment("Generate " + document.getTitle()));
    }

    @Override
    public void visitAfter(Document document) {}

    private void indent() {
        indentation.incrementAndGet();
    }

    private void outdent() {
        indentation.decrementAndGet();
    }

    private void breakLine() {
        lines.add(emptyLine(indentation.get()));
    }

    private void addLine(String text) {
        lines.add(mdLine(text, indentation.get()));
    }

    public List<MarkdownLine> getLines() {
        return lines;
    }
}
