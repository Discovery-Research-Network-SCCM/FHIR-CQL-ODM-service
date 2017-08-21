package org.partners.usciitg_prep.terminology.jpa;

import javax.naming.NamingException;

import junit.framework.TestCase;

import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IIdType;
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

import ca.uhn.fhir.jpa.provider.dstu3.JpaResourceProviderDstu3;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { FhirServerTestConfigDstu3.class })
public class FhirTerminologyProviderServiceIT extends TestCase {
	@Autowired
	FhirTerminologyProviderService fhirTerminologyProviderService;
	
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
				.addScript("org/partners/usciitg_prep/terminology/jpa/insert-data.sql")
				.build();

		builder.bind(System.getProperty("usciitg.datasource"), db);	
	}

	@Test
	public void testValueSetDaoDstu3() {
		JpaResourceProviderDstu3<ValueSet> valueSetProvider = fhirTerminologyProviderService
				.getValueSetProvider();

		ValueSet vs = new ValueSet();
		vs.setId("Influenza.Test.OID");
		IIdType id = vs.getIdElement();
		vs = valueSetProvider.getDao().read(id);

		assertEquals("Influenza.Test.OID", vs.getId());
		assertEquals("1", vs.getVersion());
		assertEquals(1, vs.getExpansion().getContains().size());
		assertEquals("76077-7", vs.getExpansion().getContains().get(0)
				.getCode());
		assertEquals("http://loinc.org", vs.getExpansion().getContains().get(0)
				.getSystem());
	}
}
