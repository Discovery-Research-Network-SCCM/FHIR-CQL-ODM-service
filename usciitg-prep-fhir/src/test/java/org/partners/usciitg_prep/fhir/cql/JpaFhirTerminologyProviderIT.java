package org.partners.usciitg_prep.fhir.cql;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.cql.terminology.fhir.JpaFhirTerminologyProvider;
import org.partners.usciitg_prep.terminology.jpa.FhirTerminologyProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.context.FhirContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {FhirServerTestConfigDstu3.class})
public class JpaFhirTerminologyProviderIT {
    private static FileBasedFhirDstu2Provider dataProvider;
    private static TerminologyProvider terminologyProvider;
    private static Map<String, Object> contextValues;
    private static Map<String, Object> contextParameters;

    @Autowired
    FhirTerminologyProviderService fhirTerminologyProviderService;

    @BeforeClass
    public static void setup(){
        dataProvider = new FileBasedFhirDstu2Provider(
        		System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql/data"
        		, null
        		, true);
        dataProvider.setFhirContext(FhirContext.forDstu2Hl7Org());
        dataProvider.setPackageName("org.hl7.fhir.instance.model");

        contextValues = new HashMap<String, Object>();
        contextParameters = new HashMap<String, Object>();
    }
    
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
				.build();

		builder.bind(System.getProperty("usciitg.datasource"), db);	
	}
	
    @Test
    public void testInValueSetCql() throws IOException, JAXBException {
        terminologyProvider = new JpaFhirTerminologyProvider(fhirTerminologyProviderService.getValueSetProvider(),
                fhirTerminologyProviderService.getCodeSystemProvider());
        dataProvider.setTerminologyProvider(terminologyProvider);
       
		UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
				new ModelManager(),
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");
		LibraryLoader libraryLoader = new LibraryLoaderImpl(libraryManager);
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("terminology_test");

        contextValues.clear();
        contextValues.put("Patient", "fhirTest");

        Map<String, Object> results;

        CqlExecutor executor = new CqlFhirExecutorImpl(dataProvider, terminologyProvider, libraryIdentifier, libraryLoader);
        results = executor.execute(contextValues, contextParameters);

        assertEquals(true, (Boolean) results.get("fluinvalueset"));
        assertEquals(true, (Boolean) results.get("antiviralinvalueset"));
    }
}
