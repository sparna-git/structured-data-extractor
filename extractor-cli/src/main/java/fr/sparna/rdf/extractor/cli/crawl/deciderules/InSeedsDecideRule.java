package fr.sparna.rdf.extractor.cli.crawl.deciderules;

import edu.uci.ics.crawler4j.url.WebURL;
import fr.sparna.rdf.extractor.cli.crawl.Seeds;

public class InSeedsDecideRule extends PredicatedDecideRule {

    protected Seeds seeds;
    
	public InSeedsDecideRule(Seeds seeds) {
		super();
		this.seeds = seeds;
	}

	@Override
	protected boolean evaluate(WebURL url) {
		return seeds.getUrls().contains(url.getURL());
	}

}
