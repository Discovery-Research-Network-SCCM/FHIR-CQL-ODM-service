package org.partners.usciitg_prep.fhir.cql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.model.Version;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 * Based on import org.cqframework.cql.cql2elm.DefaultLibrarySourceProvider
 * Extended to allow ELM XML libraries
 *
 */
public class UsciitgLibrarySourceProvider implements LibrarySourceProvider {
	private String pathString;
    private Path path;	
	
    /**
     * @param path Absolute path to directory containing library files
     */
    public UsciitgLibrarySourceProvider(String path) {      
    	this.pathString = path;
    	
        if(this.path == null){
        	this.path = Paths.get(path);				
        }
    }    

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        String libraryName = libraryIdentifier.getId();
        Path libraryPath = null;
        
		libraryPath = this.path.resolve(String.format("%s%s.xml", libraryName,
				libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : ""));
				
		try {
			return loadLibrarySource(libraryIdentifier, libraryPath, libraryName);
		} catch (FileNotFoundException e) {
			libraryPath = this.path.resolve(String.format("%s%s.cql", libraryName,
					libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : ""));
			try {
				return loadLibrarySource(libraryIdentifier, libraryPath, libraryName);
			} catch (FileNotFoundException e1) {
				throw new IllegalArgumentException(String.format("Could not load source for library %s.", libraryIdentifier.getId()), e);
			}
		}			        		
    }
    
    private InputStream loadLibrarySource(VersionedIdentifier libraryIdentifier, Path libraryPath, String libraryName) throws FileNotFoundException {
        File libraryFile = libraryPath.toFile();
        if (!libraryFile.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File path, String name) {
                    return (name.startsWith(libraryName) && name.endsWith(".xml")
                    		|| name.startsWith(libraryName) && name.endsWith(".cql"));
                }
            };

            File mostRecentFile = null;
            Version mostRecent = null;
            for (File file : path.toFile().listFiles(filter)) {            	
                String fileName = file.getName();
                int indexOfExtension = fileName.lastIndexOf(".");
                if (indexOfExtension >= 0) {
                    fileName = fileName.substring(0, indexOfExtension);
                }

                int indexOfVersionSeparator = fileName.indexOf("-");
                if (indexOfVersionSeparator >= 0) {
                    Version version = new Version(fileName.substring(indexOfVersionSeparator + 1));
                    if (mostRecent == null || version.compareTo(mostRecent) > 0) {
                        mostRecent = version;
                        mostRecentFile = file;
                    }
                }
                else {
                    // If the file is named correctly, but has no version, consider it the most recent version
                    if (mostRecent == null) {
                        mostRecentFile = file;
                    }
                }
            }

            // Do not throw, allow the loader to throw, just report null
            //if (mostRecentFile == null) {
            //    throw new IllegalArgumentException(String.format("Could not resolve most recent source library for library %s.", libraryIdentifier.getId()));
            //}

            libraryFile = mostRecentFile;
        }
        
        if (libraryFile != null) {
        	URL url = ClassLoader.getSystemResource(pathString);
    		if(url != null){
    			return ClassLoader.getSystemResourceAsStream(pathString +
    					(pathString.endsWith("/") ? "":"/") +
    					libraryFile.getName());
    		}else{
    			return new FileInputStream(libraryFile);   
    		}        	     	
        }
		return null;
    }
}
