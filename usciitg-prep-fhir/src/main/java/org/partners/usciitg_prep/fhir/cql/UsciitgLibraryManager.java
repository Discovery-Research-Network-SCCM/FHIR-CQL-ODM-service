package org.partners.usciitg_prep.fhir.cql;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorException;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.ObjectFactory;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.jboss.logging.Logger;
import org.opencds.cqf.cql.elm.execution.ObjectFactoryEx;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * File based library repository with cache for testing purposes. Added support for loading ELM XML.
 *
 */
public class UsciitgLibraryManager extends LibraryManager {	
	private ModelManager modelManager;	
	private Logger logger = Logger.getLogger(UsciitgLibraryManager.class);
	
	/**
	 * @param path Absolute path of directory in which to look for library files.
	 */
	public UsciitgLibraryManager(ModelManager modelManager, String path){
		super(modelManager);
		this.modelManager = modelManager;		
		this.getLibrarySourceLoader().registerProvider(new UsciitgLibrarySourceProvider(path));	
	}
	
	private final LoadingCache<VersionedIdentifier, Library> libraryCache = CacheBuilder
			.newBuilder().softValues().initialCapacity(300).maximumSize(300)
			//TODO move library cache expiration to properties file
			.concurrencyLevel(4).expireAfterAccess(10, TimeUnit.HOURS)
			.build(new CacheLoader<VersionedIdentifier, Library>() {
				@Override
				public Library load(VersionedIdentifier libraryIdentifier) throws Exception {
					logger.info("Loading library: " + libraryIdentifier.getId());
					return getCachedLibrary(libraryIdentifier);
				}
			});

	/**
	 * @param libraryIdentifier Retrieves Library created from file with name libraryIdentifier.Id. 
	 * If no extension of either .cql or .xml provided, attempts to load file Id with extension .cql first, then .xml.
	 * @return Library object from Id file.
	 * @throws ExecutionException
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	public Library getLibrary(VersionedIdentifier libraryIdentifier) throws FileNotFoundException, ExecutionException, JAXBException {
		return libraryCache.get(libraryIdentifier);		
	}
	
	public void clearCache(){
		libraryCache.invalidateAll();
	}
	
	private Library getCachedLibrary(VersionedIdentifier libraryIdentifier) {
		org.hl7.elm.r1.VersionedIdentifier vi = new org.hl7.elm.r1.VersionedIdentifier()
		.withId(libraryIdentifier.getId())
		.withSystem(libraryIdentifier.getSystem())
		.withVersion(libraryIdentifier.getVersion());
		InputStream is = this.getLibrarySourceLoader().getLibrarySource(vi);			
				
		if(is == null){
			return null;
		}

		Library library = null;
		try {
			library = readLibrary(is);
		} catch (JAXBException e) {
			// failed to parse xml, try cql
			// not ideal, but caching so should not need to load often		
			try {
				is = this.getLibrarySourceLoader().getLibrarySource(vi);	
				library = readCqlLibrary(is);
			} catch (JAXBException | IOException e1) {
				throw new IllegalArgumentException(String.format("Could not load source for library %s.", libraryIdentifier.getId()), e1);
			}
		}
		
		return library;
	}
		
    private Library readLibrary(InputStream is) throws JAXBException {
        //JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName(), ObjectFactory.class.getClassLoader());
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new ObjectFactoryEx());
        // com.sun.xml.internal.bind.ObjectFactory causes javax.xml.bind.PropertyException

        //Library library = (Library) jaxbUnmarshaller.unmarshal(is);
        @SuppressWarnings("unchecked")
		JAXBElement<Library> je = (JAXBElement<Library>) jaxbUnmarshaller.unmarshal(is);
        Library library = je.getValue();
        return library;
    }   
    
    private Library readCqlLibrary(InputStream is) throws JAXBException, IOException {
        Library library;            
        InputStream targetStream = null;

        try {
            ArrayList<CqlTranslator.Options> options = new ArrayList<>();
            options.add(CqlTranslator.Options.EnableDateRangeOptimization);                        
            
            CqlTranslator translator = CqlTranslator.fromStream(is, 
            		modelManager, this, options.toArray(new CqlTranslator.Options[options.size()]));
            
            if (translator.getErrors().size() > 0) {
                System.err.println("Translation failed due to errors:");
                ArrayList<String> errors = new ArrayList<>();
                for (CqlTranslatorException error : translator.getErrors()) {
                    TrackBack tb = error.getLocator();
                    String lines = tb == null ? "[n/a]" : String.format("[%d:%d, %d:%d]",
                            tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
                    System.err.printf("%s %s%n", lines, error.getMessage());
                    errors.add(lines + error.getMessage());
                }
                throw new IllegalArgumentException(errors.toString());
            }            

            targetStream = new ByteArrayInputStream(translator.toXml().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        library = readLibrary(targetStream); //CqlLibraryReader.read(xmlFile);
        return library;
    }
}
