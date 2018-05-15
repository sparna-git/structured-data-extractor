package fr.sparna.rdf.extractor.cli.list;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.extractor.DataExtractionSource;
import fr.sparna.rdf.extractor.DataExtractionSourceFactory;
import fr.sparna.rdf.extractor.DataExtractor;
import fr.sparna.rdf.extractor.DataExtractorHandlerFactory;
import fr.sparna.rdf.extractor.WebPageExtractorFactory;
import fr.sparna.rdf.extractor.cli.ExtractorCliCommandIfc;
import fr.sparna.rdf.extractor.cli.RepositoryFactoryFromString;

public class ProcessList implements ExtractorCliCommandIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Override
	public void execute(Object args) throws Exception {
		ArgumentsProcessList a = (ArgumentsProcessList)args;
		
		// create Extractor
		WebPageExtractorFactory wpef = new WebPageExtractorFactory();
		// don(t extract content
		wpef.setExtractContent(false);
		wpef.setExtractMicrodata(false);
		wpef.setExtractJsonLd(false);
		wpef.setAttemptXhtmlParsing(false);
		

		// create output directory if not existing
		if(!a.getOutput().exists()) {
			a.getOutput().mkdirs();
		}

		
		java.util.List<String> lines = Files.lines(a.getInput().toPath()).collect(Collectors.toList());
		log.debug("Entries in input file : {} ", lines.size());
		
		if(a.getExclude() != null) {
			File excludeFile = new File(a.getExclude());
			if(excludeFile.exists()) {
				java.util.List<String> linesExclude = Files.lines(new File(a.getExclude()).toPath()).collect(Collectors.toList());
				log.debug("Entries in exclude file : {} ", linesExclude.size());
				
				lines.removeAll(linesExclude);
				log.debug("Entries after exluding files : {} ", lines.size());
			} else {
				log.debug("File of excluded URI does not exist : {} ", a.getExclude());
			}

		}
		
		// create a source factory
		DataExtractionSourceFactory desf = new DataExtractionSourceFactory();
		// create an extractor
		DataExtractor extractor = wpef.buildExtractor(); 
		// create a factory for our handlers
		DataExtractorHandlerFactory dataExtractorHandlerFactory = new DataExtractorHandlerFactory();
		// set namespaces
		Map<String, String> ns = a.getNamespaceMappings();
		
		for (String aLine : lines) {
			
			if(!aLine.equals("")) {
				File outputFile = new File(a.getOutput(), URLEncoder.encode(aLine, "UTF-8")+".ttl");
				
				if(a.isNoOverwrite() && outputFile.exists()) {
					log.debug("Output file already exists, will skip it : {} ", outputFile.getName());
					continue;
				}
				
				RepositoryFactoryFromString repositoryFactory = new RepositoryFactoryFromString(outputFile.getAbsolutePath());
				Repository r = repositoryFactory.newRepository();
				
				DataExtractionSource source = desf.buildSource(
						SimpleValueFactory.getInstance().createIRI(aLine)
				);
				
				// extract
				try(RepositoryConnection connection = r.getConnection()) {
					
					// set namespaces
					if(ns != null) {
						ns.forEach((key,uri) -> connection.setNamespace(key, uri));
					}				
					
					// create handler
					RDFHandler handler = dataExtractorHandlerFactory.newHandler(connection, source.getDocumentIri());							
					
					synchronized(extractor) {
						extractor.extract(
								source,
								handler
						);
					}
					
				}
				
				try(RepositoryConnection connection = r.getConnection()) {
					// dump the content of the repo in a file
					RDFWriter writer = Rio.createWriter(
							Rio.getParserFormatForFileName(repositoryFactory.getRepositoryString()).orElse(RDFFormat.RDFXML),
							new FileOutputStream(repositoryFactory.getRepositoryString())
					);
					
					connection.export(writer);
				}
				
				log.info("{}", aLine);				
			}

		}
	}

}
