package org.partners.usciitg_prep.fhir.cql;

import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.data.DataProvider;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.terminology.TerminologyProvider;

public class CqlFhirExecutorImpl extends CqlExecutor {	
    public CqlFhirExecutorImpl(DataProvider dataProvider, TerminologyProvider terminologyProvider,
    		VersionedIdentifier libraryIdentifier, LibraryLoader libraryLoader){
        super(dataProvider, terminologyProvider, libraryIdentifier, libraryLoader);

        //assumes modelUri is fhir
        context.registerDataProvider("http://hl7.org/fhir", dataProvider);
    }
}
