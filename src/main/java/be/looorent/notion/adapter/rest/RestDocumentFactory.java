package be.looorent.notion.adapter.rest;

import be.looorent.notion.core.*;
import be.looorent.notion.port.*;
import notion.api.v1.model.blocks.*;
import notion.api.v1.model.databases.DatabaseProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static notion.api.v1.model.blocks.BlockType.ChildPage;
import static notion.api.v1.model.blocks.BlockType.SyncedBlock;

@ApplicationScoped
class RestDocumentFactory implements DocumentFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RestDocumentFactory.class);

    private final NotionRepository repository;
    private final AssetRepository assetRepository;

    public RestDocumentFactory(NotionRepository repository, AssetRepository assetRepository) {
        this.repository = repository;
        this.assetRepository = assetRepository;
    }

    @Override
    public Document create(NotionDocumentable configuration, String token) throws DocumentFactoryException {
        var root = fetchRoot(configuration, token);
        var pages = root.loadPages(repository, token);

        var blocksPerPageId = pages.stream().collect(toMap(PageDetail::getId, page -> fetchBlocksRecursively(page.getId(), root.includesChildPages(), token)));
        var blocks = flattenPages(pages, blocksPerPageId);
        var assetsPerBlockId = downloadAssets(blocks, configuration.getOutputs()).stream().collect(toMap(Asset::getId, identity()));
        LOG.info("{} assets downloaded", assetsPerBlockId.size());
        var pageTree = createPage(pages, blocksPerPageId, assetsPerBlockId);
        return new Document(root.getId(), root.getTitle(), root.group(pageTree));
    }

    private List<PageComposite> createPage(List<PageDetail> pages,
                                           Map<String, List<BlockComposite>> blocksPerPage,
                                           Map<String, Asset> assetsPerBlockId) {
        return pages.stream()
                .map(page -> new PageComposite(
                        page,
                        page.getTitle(),
                        page.getOrder(),
                        createComposites(blocksPerPage.getOrDefault(page.getId(), emptyList()), assetsPerBlockId)))
                .collect(toList());
    }

    private List<BlockComposite> flattenPages(List<PageDetail> pages, Map<String, List<BlockComposite>> blocksPerPageId) {
        var blocks = pages.stream()
                .flatMap(page -> blocksPerPageId.get(page.getId()).stream())
                .collect(toList());
        LOG.info("{} blocks found in Notion", blocks.stream().mapToInt(BlockComposite::getNumberOfBlocks).sum());
        return blocks;
    }

    private Collection<Asset> downloadAssets(Collection<BlockComposite> blocks, Collection<OutputFormat> outputs) {
        var assets = new ArrayList<Asset>();
        for (var block : blocks) {
            switch (block.block().getType()) {
                case Image -> this.assetRepository.downloadAssetIn(block.block().asImage(), outputs).ifPresent(assets::add);
                case PDF -> this.assetRepository.downloadAssetIn(block.block().asPDF(), outputs).ifPresent(assets::add);
            }
            assets.addAll(downloadAssets(block.children(), outputs));
        }
        return assets;
    }

    private List<BlockComposite> fetchBlocksRecursively(String blockId, boolean fetchChildPages, String token) {
        return repository.findChildren(blockId, token)
                .stream()
                .map(block -> new BlockComposite(block, fetchChildren(block, fetchChildPages, token)))
                .collect(toList());
    }

    private List<BlockComposite> fetchChildren(Block block, boolean fetchChildPages, String token) {
        if (block.getType() == SyncedBlock) {
            return fetchSyncedBlock(block.asSyncedBlock(), fetchChildPages, token);
        } else if (block.getType() == ChildPage) {
            return fetchChildPage(block, fetchChildPages, token);
        } else if (TRUE.equals(block.getHasChildren())) {
            return fetchBlocksRecursively(block.getId(), fetchChildPages, token);
        } else {
            return emptyList();
        }
    }

    private List<BlockComposite> fetchSyncedBlock(SyncedBlock block, boolean fetchChildPages, String token) {
        var nestedBlocks = ofNullable(block.getSyncedBlock()).map(notion.api.v1.model.blocks.SyncedBlock.Element::getChildren).orElseGet(Collections::emptyList);
        if (nestedBlocks.isEmpty()) {
            return ofNullable(block.getSyncedBlock())
                    .map(notion.api.v1.model.blocks.SyncedBlock.Element::getSyncedFrom)
                    .map(synced -> fetchBlocksRecursively(synced.getBlockId(), fetchChildPages, token))
                    .orElseGet(Collections::emptyList);
        } else {
            return nestedBlocks.stream()
                    .map(nestedBlock -> new BlockComposite(nestedBlock, emptyList()))
                    .collect(toList()); // is it possible to have nested blocks with children
        }
    }

    private List<BlockComposite> fetchChildPage(Block block, boolean fetchChildPages, String token) {
        if (fetchChildPages) {
            return this.fetchBlocksRecursively(block.getId(), fetchChildPages , token);
        } else {
            return singletonList(new BlockComposite(block, emptyList()));
        }
    }

    private List<DocumentChunk> createComposites(List<BlockComposite> blocks, Map<String, Asset> assetsPerBlockId) {
        DocumentChunk previous = null;
        var composites = new ArrayList<DocumentChunk>();
        for (var block : blocks) {
            var composite = createChunk(block, previous, assetsPerBlockId);
            composites.add(composite);
            previous = composite;
        }
        return composites;
    }

    private DocumentChunk createChunk(BlockComposite blockAndChildren,
                                      DocumentChunk previous,
                                      Map<String, Asset> assetsPerBlockId) {
        var block = blockAndChildren.block();
        var children = blockAndChildren.children();
        return switch (block.getType()) {
            case Paragraph -> createChunk(block.asParagraph(), children, assetsPerBlockId);
            case HeadingOne -> createChunk(block.asHeadingOne());
            case HeadingTwo -> createChunk(block.asHeadingTwo());
            case HeadingThree -> createChunk(block.asHeadingThree());
            case BulletedListItem -> createChunk(block.asBulletedListItem(), previous, children, assetsPerBlockId);
            case NumberedListItem -> createChunk(block.asNumberedListItem(), previous, children, assetsPerBlockId);
            case LinkToPage -> createChunk(block.asLinkToPage());
            case LinkPreview -> createChunk(block.asLinkPreview());
            case Equation -> createChunk(block.asEquation());
            case Bookmark -> createChunk(block.asBookmark());
            case Callout -> createChunk(block.asCallout(), children, assetsPerBlockId);
            case Column -> createChunk(block.asColumn(), children, assetsPerBlockId);
            case ColumnList -> createChunk(block.asColumnList(), children, assetsPerBlockId);
            case Breadcrumb -> createChunk(block.asBreadcrumb());
            case TableOfContents -> createChunk(block.asTableOfContents());
            case Divider -> createChunk(block.asDivider());
            case Video -> createChunk(block.asVideo());
            case Quote -> createChunk(block.asQuote(), children, assetsPerBlockId);
            case ToDo -> createChunk(block.asToDo(), children, assetsPerBlockId);
            case Toggle -> createChunk(block.asToggle(), children, assetsPerBlockId);
            case Code -> createChunk(block.asCode());
            case Embed -> createChunk(block.asEmbed());
            case Image -> createChunk(block.asImage(), assetsPerBlockId);
            case File -> createChunk(block.asFile());
            case PDF -> createChunk(block.asPDF(), assetsPerBlockId);
            case ChildPage -> createChunk(block.asChildPage(), children, assetsPerBlockId);
            case ChildDatabase -> createChunk(block.asChildDatabase(), children, assetsPerBlockId);
            case SyncedBlock -> createChunk(block.asSyncedBlock(), children, assetsPerBlockId);
            case Table -> createChunk(block.asTable(), children, assetsPerBlockId);
            case TableRow -> createChunk(block.asTableRow());
            case Template -> createChunk(block.asTemplate(), children, assetsPerBlockId);
            case Audio -> createChunk(block.asAudio());
            case Unsupported -> createChunk(block.asUnsupported());
        };
    }

    private DocumentChunk createChunk(ParagraphBlock paragram, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Paragraph(paragram, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(HeadingOneBlock heading) {
        return new HeadingOne(heading);
    }

    private DocumentChunk createChunk(HeadingTwoBlock heading) {
        return new HeadingTwo(heading);
    }

    private DocumentChunk createChunk(HeadingThreeBlock heading) {
        return new HeadingThree(heading);
    }

    private DocumentChunk createChunk(BulletedListItemBlock list, DocumentChunk previous, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        var number = (previous instanceof BulletedList bulletedList) ? bulletedList.getNumber() + 1 : 1;
        return new BulletedList(list, number, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(NumberedListItemBlock list, DocumentChunk previous, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        var number = (previous instanceof NumberedList numberedList) ? numberedList.getNumber() + 1 : 1;
        return new NumberedList(list, number, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(LinkToPageBlock linkToPage) {
        return new LinkToPage(linkToPage);
    }

    private DocumentChunk createChunk(LinkPreviewBlock linkPreview) {
        return new LinkPreview(linkPreview);
    }

    private DocumentChunk createChunk(EquationBlock equation) {
        return new Equation(equation);
    }

    private DocumentChunk createChunk(BookmarkBlock bookmark) {
        return new Bookmark(bookmark);
    }

    private DocumentChunk createChunk(ColumnListBlock columnList, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new ColumnList(columnList, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(ColumnBlock column, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Column(column, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(CalloutBlock callout, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Callout(callout, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(VideoBlock video) {
        return new Video(video);
    }

    private DocumentChunk createChunk(DividerBlock divider) {
        return new Divider(divider);
    }

    private DocumentChunk createChunk(TableOfContentsBlock tableOfContents) {
        return new TableOfContent(tableOfContents);
    }

    private DocumentChunk createChunk(BreadcrumbBlock breadcrumb) {
        return new Breadcrumb(breadcrumb);
    }

    private DocumentChunk createChunk(ToggleBlock toggle, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Toggle(toggle, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(ToDoBlock toDo, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new ToDo(toDo, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(QuoteBlock quote, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Quote(quote, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(SyncedBlock syncedBlock, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Synced(syncedBlock, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(ChildDatabaseBlock childDatabase, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new ChildDatabase(childDatabase, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(ChildPageBlock childPage, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new ChildPage(childPage, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(PDFBlock pdf, Map<String, Asset> assetsPerBlockId) {
        return new Pdf(pdf, assetsPerBlockId.get(pdf.getId()));
    }

    private DocumentChunk createChunk(FileBlock file) {
        return new File(file);
    }

    private DocumentChunk createChunk(ImageBlock image, Map<String, Asset> assetsPerBlockId) {
        return new Image(image, assetsPerBlockId.get(image.getId()));
    }

    private DocumentChunk createChunk(EmbedBlock embed) {
        return new Embed(embed);
    }

    private DocumentChunk createChunk(CodeBlock code) {
        return new Code(code);
    }

    private DocumentChunk createChunk(UnsupportedBlock unsupported) {
        return new Unsupported(unsupported);
    }

    private DocumentChunk createChunk(AudioBlock block) {
        return new Audio(block);
    }

    private DocumentChunk createChunk(TemplateBlock template, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Template(template, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(TableBlock table, List<BlockComposite> children, Map<String, Asset> assetsPerBlockId) {
        return new Table(table, createComposites(children, assetsPerBlockId));
    }

    private DocumentChunk createChunk(TableRowBlock row) {
        return new TableRow(row);
    }

    private Root fetchRoot(NotionDocumentable documentable, String token) throws RestFactoryException {
        if (documentable instanceof NotionDatabase database) {
            return fetchRoot(database, token);
        } else if (documentable instanceof NotionPage page) {
            return fetchRoot(page, token);
        } else {
            throw new IllegalArgumentException("Value not supported: " + documentable);
        }
    }

    private Root fetchRoot(NotionDatabase documentable, String token) throws RestFactoryException {
        var database = repository.findDatabase(documentable.getId(), token);
        List<GroupDetail> groupNames;
        Optional<String> groupPropertyId;
        Optional<String> orderPropertyId = !documentable.useNaturalOrder() ? ofNullable(database.getProperties().get(documentable.getOrderPropertyName())).map(DatabaseProperty::getId) : empty();

        if (documentable.useGroups()) {
            var groupProperty =  ofNullable(database.getProperties().get(documentable.getGroupPropertyName()))
                    .orElseThrow(() -> new RestFactoryException("Group: Property '"+documentable.getGroupPropertyName()+"' has not been found on database '"+database.getId()+"'"));
            groupNames = ofNullable(database.getProperties().get(documentable.getGroupPropertyName()))
                    .map(DatabaseProperty::getSelect)
                    .map(DatabaseProperty.Select::getOptions)
                    .map(options -> options.stream().map(option -> new GroupDetail(option.getId(), option.getName())).collect(toList()))
                    .orElseThrow(() -> new RestFactoryException("Failure when finding the values of Group in the property " + groupProperty.getName() + ". This must be a single-select option."));
            LOG.info("{} groups found in property '{}'", groupNames.size(), groupProperty.getName());
            groupPropertyId = ofNullable(database.getProperties().get(documentable.getGroupPropertyName())).map(DatabaseProperty::getId);
        } else {
            groupNames = emptyList();
            groupPropertyId = empty();
        }
         return new Root.DatabaseRoot(
                documentable.getTitle(),
                database,
                orderPropertyId,
                groupPropertyId,
                groupNames);
    }

    private Root fetchRoot(NotionPage documentable, String token) {
        var page = repository.findPage(documentable.getId(), token);
        var title = repository.findPageTitle(documentable.getId(), token);
        return new Root.PageRoot(documentable.getTitle(), page, ofNullable(title), documentable.isIncludeNestedPages());
    }
}
