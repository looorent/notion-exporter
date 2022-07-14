# notion-exporter

This project is a CLI to export multiple Notion pages/databases at once, to multiple output formats such as Markdown, AsciiDoctor, Word, etc.
If you need to export Notion content to a document, this project could fit your needs.

A common use case is to backup and version (in Git, for instance) your Notion databases as Markdown.

## Pre-requisites

* Docker must be installed: this command-line is served as a Docker Image
* A Notion Token

## Configuration

Notion can be configured with your own integration to define a token. (https://www.notion.so/help/create-integrations-with-the-notion-api)
Capabilities:
* Content Capabilities: "Read Content" only
* User Capabililities: "No user information"

## Output

Multiple formats are supported, or will be added in the future:
* Markdown
* JSON (which is basically the Notion API payloads)
* Word with template (wip)
* AsciiDoctor (wip)

All the assets (images and PDF) refered by the Notion pages are downloaded in an `assets` folder.

## Feature - Export a database

In Notion, a database is basically a collection of pages that have properties.
With this command, you can create a document based on these pages, grouped and ordered.

### CLI command

```
    $ docker run --rm -ti -v `pwd`/your-local-output-folder:/output looorent/notion-exporter:latest export-database [args]
```

### Options

```
Usage: notion export-database [-hV] [-d=<databaseId>] [-f=<format>]
                              [-gp=<groupPropertyName>] [-o=<folderPath>]
                              [-op=<orderPropertyName>] [-ti=<title>]
                              [-to=<notionToken>]
In Notion, a database is basically a collection of pages that have properties.
With this command, you can create a document based on these pages, grouped and
ordered.
  -d, --database-id=<databaseId>
                             The Database to export
                               Default:
  -f, --format=<format>      The output format of the exported document.
                               Available values: json, md, markdown
                               Default: json
      -gp, --group-property=<groupPropertyName>
                             The exported document contains all the exported
                               pages, grouped by this property. Grouping the
                               pages creates an additional level in the
                               exported document. These groups are ordered
                               according to the natural order of this property
                               in Notion. This property must be a single-select
                               property. When this property is not defined,
                               pages are not grouped.
                               Default:
  -h, --help                 Show this help message and exit.
  -o, --output-folder=<folderPath>
                             The path to the output folder. If the folder does
                               not exist, it is created. Considering this CLI
                               runs in a Docker container, this option does not
                               really matter. The important value is the volume
                               you define when running the CLI with Docker. By
                               default, you should map the expected output
                               folder on this default value. For example `-v
                               /your-folder:/output`
                               Default: /output
      -op, --order-property=<orderPropertyName>
                             The exported document contains all the exported
                               pages, ordered by this property. When the pages
                               are grouped, the order is only applied within
                               the group. This property must be an integer
                               property. When this property is not defined, the
                               natural order of Notion is used.
      -ti, --title=<title>   The title to use in the exported document. When
                               this property is not defined, the database's
                               title is used.
      -to, --notion-token=<notionToken>
                             The Notion API Token. See Notion's developer
                               documentation to create a token.
                               Default:
  -V, --version              Print version information and exit.
```

## Feature - Export a page

In Notion, a page can contain a collection of subpages. With this command, you can create a document based on this hierarchy of pages.

### CLI Command

```
    $ docker run --rm -ti -v `pwd`/your-local-output-folder:/output looorent/notion-exporter:latest export-page [args]
```

### Options

```
Usage: notion export-page [-hnV] [-f=<format>] [-o=<folderPath>] [-p=<pageId>]
                          [-to=<notionToken>]
In Notion, a page can contain a collection of subpages. With this command, you
can create a document based on this hierarchy of pages.
  -f, --format=<format>    The output format of the exported document.
                             Available values: json, md, markdown
                             Default: json
  -h, --help               Show this help message and exit.
  -n, --include-nested-pages
                           Whether the nested pages must be loaded and
                             exported. If this option is false, only the page
                             mentioned is exported.
  -o, --output-folder=<folderPath>
                           The path to the output folder. If the folder does
                             not exist, it is created. Considering this CLI
                             runs in a Docker container, this option does not
                             really matter. The important value is the volume
                             you define when running the CLI with Docker. By
                             default, you should map the expected output folder
                             on this default value. For example `-v
                             /your-folder:/output`
                             Default: /output
  -p, --page-id=<pageId>   The Page to export
                             Default:
      -to, --notion-token=<notionToken>
                           The Notion API Token. See Notion's developer
                             documentation to create a token.
                             Default:
  -V, --version            Print version information and exit.
```


## Next steps - Roadmap

* Support grouping and ordering on different types of properties
* Export to AsciiDoctor
* Export to Word
* After a list of bulleted items, add a break line

## GraalVM Support

Considering the library `com.github.seratch:notion-sdk-jvm-core` depends on several libraries that do not support GraalVM, this project is not build with GraalVM.
Building the image with `./gradlew clean build -Dquarkus.package.type=native -Dquarkus.native.container-build=true` produces an executable that raise:
```
Exception in thread "main" com.oracle.svm.core.jdk.UnsupportedFeatureError: Code that was considered unreachable by closed-world analysis was reached.
```
For now, the Docker image is based on `ubi8/openjdk17`.