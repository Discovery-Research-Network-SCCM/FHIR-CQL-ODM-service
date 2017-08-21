package org.partners.usciitg_prep.fhir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.spi.ResourceFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.partners.usciitg_prep.terminology.jpa.FhirTerminologyProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nk940
 * Integration tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { FhirServiceTestConfig.class })
public class FhirServiceIT {
	@Autowired
	FhirService fhirService;
	@Autowired
	FhirTerminologyProviderService fhirTerminologyProviderService;
	
	private static Dispatcher dispatcher;
	
	@BeforeClass
	public static void contextSetup() throws Exception {
		if(System.getProperty("usciitg.db") != null && !System.getProperty("usciitg.db").isEmpty()){
			setupDriverManagerDataSource();
		}else{
			setupEmbeddedDatabase();
		}
	}

	private static void setupDriverManagerDataSource() throws NamingException{
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder
				.emptyActivatedContextBuilder();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(System.getProperty("usciitg.db.driver"));
		dataSource.setUrl(System.getProperty("usciitg.db"));
		dataSource.setUsername(System.getProperty("usciitg.db.user"));
		dataSource.setPassword(System.getProperty("usciitg.db.password"));

		builder.bind(System.getProperty("usciitg.datasource"), dataSource);	
	}
	
	private static void setupEmbeddedDatabase() throws NamingException{
		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder
				.emptyActivatedContextBuilder();
		
		EmbeddedDatabaseBuilder dbBuilder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = dbBuilder
				.setType(EmbeddedDatabaseType.DERBY)
				.setName("usciitg")            
				.setScriptEncoding("UTF-8")
				.ignoreFailedDrops(true)
				.addScript("org/partners/usciitg_prep/terminology/jpa/create-db.sql")
				.addScript("org/partners/usciitg_prep/fhir/cql/insert-data.sql")
				.addScript("org/partners/usciitg_prep/fhir/create-db.sql")
				.addScript("org/partners/usciitg_prep/fhir/insert-data.sql")
				.build();

		builder.bind(System.getProperty("usciitg.datasource"), db);	
	}
	
	@Before
	public void setUp() throws Exception {		
		dispatcher = MockDispatcherFactory.createDispatcher();
		
		fhirService.initialize(fhirTerminologyProviderService);
		ResourceFactory noDefaults = new SingletonResource(fhirService);
		dispatcher.getRegistry().addResourceFactory(noDefaults);		
	}

	@Test
	public void testGetPatientHapi() {
		MockHttpRequest request = null;
		try {
			request = MockHttpRequest.get("/fhir/patient/fhirTest?group_id=4");
			//http://fhirtest.uhn.ca/baseDstu2/Patient?_id=fhirTest
		} catch (URISyntaxException e) {
			fail(e.getMessage());				
		}
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());	
		assertTrue(response.getContentAsString().contains("fhirTest"));	
	}
	
	@Test
	public void testGetPatientOpenEpic() {
		MockHttpRequest request = null;
		try {
			request = MockHttpRequest.get("/fhir/patient/Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB?group_id=2");
			//https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/Patient?_id=Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB
		} catch (URISyntaxException e) {
			fail(e.getMessage());				
		}
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());		
		assertTrue(response.getContentAsString().contains("Argonaut, Jason"));		
	}
	
	@Test
	public void testGetOdmHapi() {
		MockHttpRequest request = null;
		try {
			request = MockHttpRequest.get(					
					"/fhir/patient/fhirTest/odm?project_id=29&instrument=usciit_prep_flu_study&group_id=4&encounter_start=2016-05-29T09:00:00&encounter_end=2016-06-20");
		} catch (URISyntaxException e) {
			fail(e.getMessage());				
		}
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());		
		assertTrue(response.getContentAsString().contains("fhirTest"));		
	}
	
	@Test
	public void testGetOdmOpenEpic() {
		MockHttpRequest request = null;
		try {
			request = MockHttpRequest.get(
					"/fhir/patient/Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB/odm?project_id=29&instrument=usciit_prep_flu_study&group_id=2&encounter_start=2016-04-18&encounter_end=2016-04-20");
		} catch (URISyntaxException e) {
			fail(e.getMessage());				
		}
		MockHttpResponse response = new MockHttpResponse();

		dispatcher.invoke(request, response);

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());		
		assertTrue(response.getContentAsString().contains("Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB"));		
	}
}
