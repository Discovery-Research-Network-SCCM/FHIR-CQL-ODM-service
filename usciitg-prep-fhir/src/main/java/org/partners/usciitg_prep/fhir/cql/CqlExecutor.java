package org.partners.usciitg_prep.fhir.cql;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.cqframework.cql.elm.execution.AccessModifier;
import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.IncludeDef;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.Library.Includes;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.data.DataProvider;
import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.execution.CqlEngine;
import org.opencds.cqf.cql.terminology.TerminologyProvider;

/**
 * Created by maxidroms83 on 2/21/17.
 */
public abstract class CqlExecutor extends CqlEngine {
    protected Context context;
    protected VersionedIdentifier libraryIdentifier;
    protected org.opencds.cqf.cql.execution.LibraryLoader libraryLoader;     
    protected Library library;
    private DataProvider dataProvider;
    private TerminologyProvider terminologyProvider;
    
    private static Logger logger = Logger.getLogger(CqlExecutor.class);

    public CqlExecutor(DataProvider dataProvider, TerminologyProvider terminologyProvider,
    		VersionedIdentifier libraryIdentifier, org.opencds.cqf.cql.execution.LibraryLoader libraryLoader){
        this.libraryIdentifier = libraryIdentifier;
        this.libraryLoader = libraryLoader;
        library = libraryLoader.load(libraryIdentifier);
        this.dataProvider = dataProvider;
        this.terminologyProvider = terminologyProvider;
        
        context = new Context(library);
        context.setExpressionCaching(true);
        context.registerTerminologyProvider(terminologyProvider);
        if(libraryLoader != null){
            context.registerLibraryLoader(libraryLoader);
        }
    }

    public Context getContext(){
        return context;
    }

    public void setContextValues(Map<String, Object> contextValues){
        if(contextValues == null){
            return;
        }

        for(Map.Entry<String, Object> entry: contextValues.entrySet()){
            context.setContextValue(entry.getKey(), entry.getValue());
        }
    }

    public void setContextParameters(VersionedIdentifier libraryIdentifier, Map<String, Object> contextParameters){
        if(contextParameters == null){
            return;
        }

        for(Map.Entry<String, Object> entry: contextParameters.entrySet()){
            //only sets params in top level library
            context.setParameter(library.getLocalId(), entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Object> execute(Map<String, Object> contextValues, Map<String, Object> contextParameters){
        setContextValues(contextValues);
        setContextParameters(libraryIdentifier, contextParameters); //assumes no overlap between library params

        return execute();
    }

    public Map<String, Object> execute() {    	    	
    	if(logger.getLevel() != null && logger.getLevel().equals(Level.TRACE)){
    		context.setEnableTraceLogging(true);
    	}else{
    		context.setEnableTraceLogging(false);
    	}
    	
    	Library library = libraryLoader.load(libraryIdentifier);    
    	setupFhirHelpers(library);
        Map<String, Object> results = new HashMap<String, Object>();
        
        try{
	        for (ExpressionDef expressionDef : library.getStatements().getDef()) {
	            if(expressionDef.getAccessLevel() == AccessModifier.PUBLIC){
	                Object result = context.resolveExpressionRef(expressionDef.getName()).evaluate(context);
	                results.put(expressionDef.getName(), result);
	            }
	        }
        }finally{
            if(logger.getLevel() != null && logger.getLevel().equals(Level.TRACE)){
            	String report = context.getTrace();
            	logger.trace(report);
            }
        }
               
        return results;
    }
    
    @Override
    public Object evaluate(Library library, String expressionName, Map<String, Object> parameters) {
        Context localContext = new Context(library);
        localContext.setExpressionCaching(true);
        localContext.registerTerminologyProvider(terminologyProvider);
        if(libraryLoader != null){
        	localContext.registerLibraryLoader(libraryLoader);
        }
        setupFhirHelpers(library);
        
    	if(logger.getLevel() != null && logger.getLevel().equals(Level.TRACE)){
    		localContext.setEnableTraceLogging(true);
    	}else{
    		localContext.setEnableTraceLogging(false);
    	}
    	    	
    	if(parameters != null){
            for(Map.Entry<String, Object> entry: parameters.entrySet()){
                //only sets params in top level library
            	localContext.setParameter(library.getLocalId(), entry.getKey(), entry.getValue());
            }
    	}

        try{
	        for (ExpressionDef expressionDef : library.getStatements().getDef()) {
	        	if(expressionDef.getName().equals(expressionName)){
	        		return localContext.resolveExpressionRef(expressionDef.getName()).evaluate(localContext);
	        	}
	        }
        }finally{
            if(logger.getLevel() != null && logger.getLevel().equals(Level.TRACE)){
            	String report = localContext.getTrace();
            	logger.trace(report);
            }
        }
        
		return null;                      
    }
    
    private void setupFhirHelpers(Library library){
    	if(dataProvider instanceof UsciitgFhirDataProviderHL7){
    		UsciitgFhirDataProviderHL7 dp = (UsciitgFhirDataProviderHL7) dataProvider;
        	Includes includes = library.getIncludes();
        	
        	if(includes != null){
        		for(IncludeDef def : includes.getDef()){
        			if(def.getLocalIdentifier().equalsIgnoreCase("fhirhelpers")){
        				dp.setIncludesFhirHelpers(true);
        				return;
        			}
        		}    		
        		dp.setIncludesFhirHelpers(false);
        	}
    	}    
    }
}