package be.looorent.notion.adapter.md;

import be.looorent.notion.core.Reference;
import be.looorent.notion.core.ReferenceContainer;
import notion.api.v1.model.common.*;
import notion.api.v1.model.pages.PageProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

class MarkdownFormatter {
    private static final Logger LOG = LoggerFactory.getLogger(MarkdownFormatter.class);
    private static final String HEADING = "#";
    private static final String STRIKE = "~~";
    private static final String BOLD = "**";
    private static final String ITALIC = "_";
    private static final String INLINE_CODE = "`";
    private static final String UNDERLINE = "__";
    private static final String UNORDERED_ITEM = "* ";
    private static final String ORDERED_ITEM = ". ";

    private MarkdownFormatter() {}

    static String addStyleTo(String content, PageProperty.RichText.Annotations annotations) {
        var wrapped = content;
        if (TRUE.equals(annotations.getBold())) {
            wrapped = addBoldTo(wrapped);
        }
        if (TRUE.equals(annotations.getItalic())) {
            wrapped = addItalicTo(wrapped);
        }
        if (TRUE.equals(annotations.getStrikethrough())) {
            wrapped = strike(wrapped);
        }
        if (TRUE.equals(annotations.getUnderline())) {
            wrapped = underline(wrapped);
        }
        if (TRUE.equals(annotations.getCode())) {
            wrapped = addCodeTo(wrapped);
        }
        return wrapped;
    }

    static String strike(String text) {
        return wrap(text, STRIKE);
    }

    static String addBoldTo(String text) {
        return wrap(text, BOLD);
    }

    static String addItalicTo(String text) {
        return wrap(text, ITALIC);
    }

    static String addCodeTo(String text) {
        return wrap(text, INLINE_CODE);
    }

    static String underline(String text) {
        return wrap(text, UNDERLINE);
    }

    static String wrap(String text, String prefixAndSuffix) {
        return text.isEmpty() ? "" : prefixAndSuffix + text + prefixAndSuffix;
    }

    static String format(PageProperty.RichText text, ReferenceContainer references) {
        return switch (text.getType()) {
            case Text -> ofNullable(text.getText()).map(value -> format(value, text.getAnnotations(), references)).orElse("");
            case Mention -> ofNullable(text.getMention()).map(mention -> format(mention, text.getPlainText(), text.getAnnotations(), references)).orElse("");
            case Equation -> format(text.getEquation(), text.getPlainText(), text.getAnnotations(), references);
            default -> throw new IllegalStateException("Unexpected value: " + text.getType());
        };
    }

    static String format(Icon icon) {
        if (icon instanceof Emoji emoji) {
            return emoji.getEmoji() + " ";
        } else if (icon instanceof File || icon instanceof SyncedFrom || icon instanceof LinkToPage) {
            LOG.warn("Icon not supported: {}", icon);
            return "";
        } else {
            return "";
        }
    }

    static String format(Optional<List<PageProperty.RichText>> texts, Optional<Icon> icon, ReferenceContainer references) {
        return icon.map(MarkdownFormatter::format).orElse("") + texts.map(t -> format(t, references)).orElse("");
    }

    static String format(Optional<List<PageProperty.RichText>> texts, ReferenceContainer references) {
        return texts.map(t -> format(t, references)).orElse("");
    }

    static String format(Equation equation, String plainText, PageProperty.RichText.Annotations annotations, ReferenceContainer references) {
        return addCodeTo(plainText);
    }

    static String format(PageProperty.RichText.Mention mention, String plainText, PageProperty.RichText.Annotations annotations, ReferenceContainer references) {
        var text = plainText;
        if (mention.getType() == RichTextMentionType.Page && mention.getPage() != null) {
            text = addLinkPerPageId(text, mention.getPage().getId(), references);
        }
        return addStyleTo(text, annotations);
    }

    static String format(PageProperty.RichText.Text text, PageProperty.RichText.Annotations annotations, ReferenceContainer references) {
        return addStyleTo(addLink(text.getContent(), text.getLink(), references), annotations);
    }

    static String format(List<PageProperty.RichText> texts, ReferenceContainer references) {
        return texts
                .stream()
                .map(text -> format(text, references))
                .collect(joining());
    }

    static String formatHeading(String title, int level) {
        return HEADING.repeat(level) + " " + title;
    }

    static String formatPlainText(List<PageProperty.RichText> texts) {
        return texts
                .stream()
                .map(PageProperty.RichText::getPlainText)
                .collect(joining());
    }

    static String format(Reference reference) {
        return "["+reference.getTitle()+"]("+ createAnchor(reference)+")";
    }

    static String formatUnorderedItem(List<PageProperty.RichText> texts, ReferenceContainer references) {
        return UNORDERED_ITEM + format(texts, references);
    }

    static String formatOrderedItem(int number, List<PageProperty.RichText> texts, ReferenceContainer references) {
        return number + ORDERED_ITEM + format(texts, references);
    }

    static String formatImage(String caption, String path) {
        return "!["+caption+"]("+path+")";
    }

    static String createAnchor(Reference reference) {
        return "#" + reference.getTitle().replace(" ", "-").toLowerCase();
    }

    static String addLink(String text, PageProperty.RichText.Link link, ReferenceContainer references) {
        return ofNullable(link)
                .map(PageProperty.RichText.Link::getUrl)
                .map(url -> addLinkPerUrl(text, url, references))
                .orElse(text);
    }

    static String addLinkPerUrl(String text, String url, ReferenceContainer references) {
        if (url.startsWith("/")) {
            return references.findReference(url)
                    .map(reference -> "["+text+"]("+ createAnchor(reference)+")")
                    .orElseGet(() -> {
                LOG.warn("Link not possible to URL '{}' for text '{}'", url, text);
                return text;
            });
        } else {
            return "["+text+"]("+url+")";
        }
    }

    private static String addLinkPerPageId(String text, String pageId, ReferenceContainer references) {
        return references.findReference(pageId)
                .map(reference -> "["+text+"]("+ createAnchor(reference)+")")
                .orElseGet(() -> {
            LOG.warn("Link not possible to Page ID '{}' for text '{}'", pageId, text);
            return text;
        });
    }

    static String formatCaption(List<PageProperty.RichText> texts, String noCaptionLabel) {
        return formatCaption(ofNullable(texts), noCaptionLabel);
    }

    static String formatCaption(Optional<List<PageProperty.RichText>> texts, String noCaptionLabel) {
        return texts.map(MarkdownFormatter::formatPlainText)
                .filter(not(String::isBlank))
                .orElse(noCaptionLabel);
    }

    static String formatComment(String comment) {
        return "<!--- "+comment+" --->";
    }
}
