package org.partners.usciitg_prep.fhir.cql;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.terminology.TerminologyProvider;

import ca.uhn.fhir.context.FhirContext;

public class FileBasedFhirDstu2ProviderIT {
    private static FileBasedFhirDstu2Provider dataProvider;
    private static TerminologyProvider terminologyProvider;
    private static Map<String, Object> contextValues; 	private static Map<String, Object> contextParameters;

    @BeforeClass
    public static void setup(){
        dataProvider = new FileBasedFhirDstu2Provider(System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql/data", null);
        dataProvider.setFhirContext(FhirContext.forDstu2Hl7Org());
        dataProvider.setPackageName("org.hl7.fhir.instance.model");

        terminologyProvider = new TestTerminologyProvider();
        dataProvider.setTerminologyProvider(terminologyProvider);

        contextValues = new HashMap<String, Object>();
        contextParameters = new HashMap<String, Object>();
    }

    @Test
    public void testExecuteFluStudy() throws IOException, JAXBException {             
    	UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
    			new ModelManager(),
    			System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");
		LibraryLoader libraryLoader = new LibraryLoaderImpl(libraryManager);
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("usciitg_flu_study");

        contextValues.clear();
        contextValues.put("Patient", "fhirTest");

        contextParameters.clear();
        DateTime encounterStart = createCqfDateTime("2016-05-29T09:00:00");
        DateTime encounterEnd = createCqfDateTime("2016-06-30");
        contextParameters.put("EncounterStart", encounterStart);
        contextParameters.put("EncounterEnd", encounterEnd);

        Map<String, Object> results;

        CqlExecutor executor = new CqlFhirExecutorImpl(dataProvider, terminologyProvider, libraryIdentifier, libraryLoader);
        results = executor.execute(contextValues, contextParameters);
 
        assertEquals(String.valueOf(1), (String) results.get("icufluinclusion"));	
        assertEquals("1", (String) results.get("confirmatory_test"));
        assertEquals(String.valueOf(2), (String) results.get("sex"));		
        assertEquals("8", (String) results.get("brthdat"));
        assertEquals(String.valueOf(4), (String) results.get("icuadmitdat"));	        
        assertEquals("1", (String) results.get("fluvac"));
        assertEquals("1", (String) results.get("plateletyn"));
        assertEquals(BigDecimal.valueOf(150), (BigDecimal) results.get("platelet"));
        assertEquals("1", (String) results.get("urineyn"));
        assertEquals(BigDecimal.valueOf(250), (BigDecimal) results.get("urine"));      
        assertEquals("1", (String) results.get("creatinineyn"));
        assertEquals(BigDecimal.valueOf(3.5), (BigDecimal) results.get("creatinine"));
        assertEquals("2", (String) results.get("creatinineunt"));
        assertEquals("1", (String) results.get("vasopressup"));
        assertEquals("", (String) results.get("vasopresstype___1"));       
        assertEquals("1", (String) results.get("vasopresstype___2"));               
        assertEquals("", (String) results.get("vasopresstype___3"));
        assertEquals("", (String) results.get("vasopresstype___4"));
        assertEquals("", (String) results.get("vasopresstype___5"));  
        assertEquals("", (String) results.get("vasopresstype___6"));  
        assertEquals("", (String) results.get("vasopresstype___7"));  
        assertEquals("", (String) results.get("vasopresstype___8"));  
        assertEquals("", (String) results.get("vasopresstype___9"));          
        assertEquals("1", (String) results.get("antiviral"));
        assertEquals("1", (String) results.get("av_names___1"));
        assertEquals("1", (String) results.get("av_names___2"));        
        assertEquals("", (String) results.get("av_names___4"));
        assertEquals("", (String) results.get("av_names___5"));
        assertEquals("", (String) results.get("av_names___6"));
        assertEquals("", (String) results.get("av_names___7"));
        assertEquals("1", (String) results.get("osel_tube"));
        assertEquals(Integer.valueOf(2), (Integer) results.get("osel_days"));
        assertEquals("2", (String) results.get("osel_dose"));
        assertEquals("1", (String) results.get("antibiotic"));
        assertEquals("1", (String) results.get("corticosteroid"));
        assertEquals("1", (String) results.get("antifungal"));     
    }

    @Test
    public void testExecuteFluStudyCql() throws IOException, JAXBException {       
    	FileBasedFhirDstu2Provider cqlDataProvider = new FileBasedFhirDstu2Provider(
    			System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql/data", 
    			null,
    			true);
    	UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
    			new ModelManager(),
    			System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");
		LibraryLoader libraryLoader = new LibraryLoaderImpl(libraryManager);
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("test_flu_study");
		
        contextValues.clear();
        contextValues.put("Patient", "fhirTest");

        contextParameters.clear();
        DateTime encounterStart = createCqfDateTime("2016-05-29T09:00:00");
        DateTime encounterEnd = createCqfDateTime("2016-06-30");
        contextParameters.put("EncounterStart", encounterStart);
        contextParameters.put("EncounterEnd", encounterEnd);

        Map<String, Object> results;

        CqlExecutor executor = new CqlFhirExecutorImpl(cqlDataProvider, terminologyProvider, libraryIdentifier, libraryLoader);
        results = executor.execute(contextValues, contextParameters);

        assertEquals(String.valueOf(2), (String) results.get("sex"));        
        assertEquals("true", (String) results.get("flu"));
    }

    private DateTime createCqfDateTime(String dateTimeString){
        DateTimeType d = new DateTimeType(dateTimeString);
        return DateTime.fromJavaDate(d.getValue());
    }
}