 package fr.sparna.rdf.extractor.cli.list;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;

import fr.sparna.cli.SpaceSplitter;

public class ArgumentsProcessList {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to the file containing the list of URI to process",
			required = true
	)
	private File input;
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path to output directory where output files will be written",
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-e", "--exclude" },
			description = "Path to the file containing the list of URI to exclude (because they were already processed)",
			required = false
	)
	private String exclude;
	
	@Parameter(
			names = "-ns",
			description = "Namespace prefixes, in the form <key1>,<ns1> <key2>,<ns2> e.g. skos,http://www.w3.org/2004/02/skos/core# dct,http://purl.org/dc/terms/",
			variableArity = true,
			splitter = SpaceSplitter.class
	)
	private List<String> namespaceMappingsStrings;
	
	public Map<String, String> getNamespaceMappings() {
		if(this.namespaceMappingsStrings == null) {
			return null;
		}
		Map<String, String> result = new HashMap<String, String>();
		for (String aMappingString : this.namespaceMappingsStrings) {
			result.put(aMappingString.split(",")[0],aMappingString.split(",")[1]);
		}
		return result;
	}	

	public List<String> getNamespaceMappingsStrings() {
		return namespaceMappingsStrings;
	}

	public void setNamespaceMappingsStrings(List<String> namespaceMappingsStrings) {
		this.namespaceMappingsStrings = namespaceMappingsStrings;
	}

	public File getInput() {
		return input;
	}

	public void setInput(File input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}
	
	
	
}
