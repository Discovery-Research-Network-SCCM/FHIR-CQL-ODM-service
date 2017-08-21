package org.partners.usciitg_prep.fhir.cql;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.partners.usciitg_prep.fhir.cql.UsciitgLibraryManager;

public class LibraryLoaderImpl implements LibraryLoader {
	private Map<String, Library> libraries = new HashMap<String, Library>();
	private UsciitgLibraryManager libraryManager;
	
	public LibraryLoaderImpl(UsciitgLibraryManager libraryManager){
		if (libraryManager == null) {
            throw new IllegalArgumentException("libraryManager is null");
        }

		this.libraryManager = libraryManager;
	}
    
    @Override
    public Library load(VersionedIdentifier libraryIdentifier) {
        try {
			return resolveLibrary(libraryIdentifier);
		} catch (FileNotFoundException | ExecutionException | JAXBException e) {
			throw new IllegalArgumentException("Unable to load Library identifier.", e);
		}
    }
    
    private Library resolveLibrary(VersionedIdentifier libraryIdentifier) throws FileNotFoundException, ExecutionException, JAXBException {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("Library identifier is null.");
        }

        if (libraryIdentifier.getId() == null) {
            throw new IllegalArgumentException("Library identifier id is null.");
        }

        Library library = libraries.get(libraryIdentifier.getId());
        if (library != null && libraryIdentifier.getVersion() != null && !libraryIdentifier.getVersion().equals(library.getIdentifier().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load library %s, version %s because version %s is already loaded.",
                    libraryIdentifier.getId(), libraryIdentifier.getVersion(), library.getIdentifier().getVersion()));
        }
        else {
            library = loadLibrary(libraryIdentifier);
            libraries.put(libraryIdentifier.getId(), library);
        }

        return library;
    }   
    
    private Library loadLibrary(VersionedIdentifier libraryIdentifier) throws FileNotFoundException, ExecutionException, JAXBException {       
        return libraryManager.getLibrary(libraryIdentifier);
    }
}
