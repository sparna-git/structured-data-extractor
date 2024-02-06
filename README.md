# structured-data-extractor

A framework for extracting RDFa, JSON-LD, Microdata and text content from webpages. In general, it relies and uses on RDF4J framework.


_(note; this documentation was written 6 years after initial development)_.

# Modules

The project contains the following modules:

- **`microdata-parser`** : code for parsing [Microdata](https://developer.mozilla.org/fr/docs/Web/HTML/Microdata) markup, initialy copied from the [Any23 project](https://any23.apache.org/) if I remember.
- **`extractor-core`** : this is where the actual extraction happens. The main interface is `fr.sparna.rdf.extractor.DataExtractor`. There are 4 implementations :
  - `fr.sparna.rdf.extractor.jsonld.JsonLDExtractor` - this is JSON-LD 1.0 only (remember this was developped in 2018). It relies on RDF4J JSON-LD parser
  - `fr.sparna.rdf.extractor.rdfa.RdfaExtractor` - this relies on [semargl RDFa parser](https://github.com/semarglproject/semargl)
  - `fr.sparna.rdf.extractor.microdata.MicrodataExtractor` - this relies on the `microdata-parser` module
  - `fr.sparna.rdf.extractor.content.ContentExtractor` - this one extracts the actual text of the page, removing HTML markup etc, trying to remove menus, etc. This is relying on the [boilerpipe library](https://boilerpipe-web.appspot.com/). This is not useful for the ELI project and was developped for other purposes.

  In addition, the resulting triples are being filtered and post-processed by a bunch of filters inside the `fr.sparna.rdf.handler` package
- **`extractor-cli`** : a command-line interface to run the extractor, coupled with a simple crawler implementation. The idea is to crawl a set of webpages, and pass each webpage content to the extractor.
- **`extractor-server`** : the encapsulation of the extractor in an API (e.g. something like `curl --header "Accept: application/ld+json" http://localhost:8080/extractor-server/api/v1/extract?uri=http://sparna.fr`). The idea is that this server can connect to an underlying RDF repository, storing the resulting triples in named graphs.

# How to run the command-line interface

- Try `java -jar extractor-cli-onejar-{version}-onejar.jar --help` tp get the help message
- There are 2 commands :
	- `crawl`
	- `list`
- `crawl` was an attempt to run free-form crawler, relying on [Crawler4J](https://github.com/yasserg/crawler4j), with additionnal deciderules taken from [Heritrix](http://crawler.archive.org/index.html). The main class is `fr.sparna.rdf.extractor.cli.crawl.ExtractorCrawler`. This is really "poor man's crawling" and I don't think this should be reused; more rebust crawlers should be used for free crawling
- `processList` is simply reading a text file containing one URL per line, and applies the extraction to each of those URLs one by one. This was originally applied on list of ELI URIs sent by the Member States.

Here is a complete command-line example:

`java -Xms512M -Xmx2048M -jar extractor-cli-1.0-SNAPSHOT-onejar.jar list \
--input random-elis-test.txt \
--output output \
--exclude processed-urls.log \
-namespaces eli,http://data.europa.eu/eli/ontology# xsd,http://www.w3.org/2001/XMLSchema# ev,http://eurovoc.europa.eu/ corp,http://publications.europa.eu/resource/authority/corporate-body/ lang,http://publications.europa.eu/resource/authority/language/ m-app,http://www.iana.org/assignments/media-types/application/ res-oj,http://publications.europa.eu/resource/oj/ res-celex,http://publications.europa.eu/resource/celex/`

The file `random-elis-test.txt` contains the following URIs:

```
http://data.europa.eu/eli/reg/2003/20/oj
http://data.europa.eu/eli/reg/2002/41/oj
http://data.europa.eu/eli/dec/2002/95/oj
http://data.europa.eu/eli/dir/1998/91/oj
http://data.europa.eu/eli/dec/1983/59/oj
http://data.europa.eu/eli/reg/2002/43/oj
http://data.europa.eu/eli/dir/2001/5/oj
http://data.europa.eu/eli/dec/1982/39/oj
http://data.europa.eu/eli/dir/1997/53/oj
```

_Note : somehow (I can't remember where exactly) the successfully processed URLs are logged into `processed-urls.log`, so that if the process fails for any reason, this file is being passed back to the second run and URLs already processed are not extracted a second time._

The folder under `extractor-cli/src/test/resources/URI-lists` contains the files of the URI lists that were used to extract dataset from Portugal, Eur-Lex, Ireland, Denmark and Italy, for the 2018 Datathon. The resulting datasets were published on the EU ODP, see https://data.europa.eu/data/datasets?locale=en&minScoring=0&query=ELI&page=1