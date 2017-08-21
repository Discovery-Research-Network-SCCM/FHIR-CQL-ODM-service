package org.partners.usciitg_prep.fhir.cql;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.LibrarySourceLoader;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.junit.Test;

public class UsciitgLibraryManagerTest {
	@Test
	public void testGetLibraryCql() throws ExecutionException, JAXBException, IOException {
		ModelManager modelManager = new ModelManager();
		UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
				modelManager,
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");		

		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("test_flu_study");
		Library library = libraryManager.getLibrary(libraryIdentifier);
		assertNotNull(library);
	}

	@Test
	public void testGetLibraryXml() throws FileNotFoundException, ExecutionException, JAXBException {
		UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
				new ModelManager(),
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("usciitg_flu_study");
		Library library = libraryManager.getLibrary(libraryIdentifier);
		assertNotNull(library);
	}

	@Test
	public void testGetLibrarySourceLoader() throws ExecutionException, JAXBException, IOException {
		String path = System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql";
		
		ModelManager modelManager = new ModelManager();
		UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
				modelManager,
				path);						
		
		LibrarySourceLoader loader = libraryManager.getLibrarySourceLoader();		
		assertNotNull(loader);
	}
}
