package org.partners.usciitg_prep.fhir.cql;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.junit.Test;

public class UsciitgLibrarySourceProviderTest {

	@Test
	public void testGetLibrarySourceCql() throws FileNotFoundException, ExecutionException, JAXBException {
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("test_flu_study");

		LibrarySourceProvider provider = new UsciitgLibrarySourceProvider(
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");	
		InputStream is = provider.getLibrarySource(libraryIdentifier);

		assertNotNull(is);
	}

	@Test
	public void testGetLibrarySourceXml() throws FileNotFoundException, ExecutionException, JAXBException {
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("usciitg_flu_study");
		
		LibrarySourceProvider provider = new UsciitgLibrarySourceProvider(
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");	
		InputStream is = provider.getLibrarySource(libraryIdentifier);

		assertNotNull(is);
	}
}
