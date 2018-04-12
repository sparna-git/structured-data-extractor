package fr.sparna.rdf.handler;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters statements with an IRI object property identical to the subject.
 * This can happen in cases where an empty value was used for a "resource" RDFa attribute, instead of simply omitting the tag.
 * 
 * @author Thomas Francart
 *
 */
public class FilterReflexiveStatementHandler extends RDFHandlerWrapper implements RDFHandler {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public FilterReflexiveStatementHandler() {
		super();
	}
	
	public FilterReflexiveStatementHandler(RDFHandler handler) {
		super(handler);
	}

	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {
		boolean isFiltered = 
				(s.getObject() instanceof IRI)
				&&
				(s.getSubject() instanceof IRI)
				&&
				(s.getObject().stringValue().equals(s.getSubject().stringValue()))
				;
		if(
				!isFiltered
		) {
			super.handleStatement(s);
		}
	}
	
}
