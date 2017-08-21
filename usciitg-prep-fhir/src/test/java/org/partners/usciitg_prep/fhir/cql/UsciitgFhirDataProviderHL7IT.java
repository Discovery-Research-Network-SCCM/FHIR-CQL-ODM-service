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
import org.hl7.fhir.instance.model.DateType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opencds.cqf.cql.execution.Context;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.terminology.TerminologyProvider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

public class UsciitgFhirDataProviderHL7IT {
	@Test
	public void testExecuteFluStudyHapi() throws IOException, JAXBException {
		Map<String, Object> results = testExecuteFluStudy("http://fhirtest.uhn.ca/baseDstu2", "fhirTest", "2016-05-29T09:00:00", "2016-06-30");
		
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
	public void testExecuteFluStudyOpenEpic() throws IOException, JAXBException {		
		//jason argonaut
		Map<String, Object> results = testExecuteFluStudy("https://open-ic.epic.com/FHIR/api/FHIR/DSTU2", "Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB", "2016-04-18", "2016-04-20");
		             
        assertEquals("", (String) results.get("icufluinclusion"));	
        assertEquals("999", (String) results.get("confirmatory_test"));
        assertEquals(String.valueOf(1), (String) results.get("sex"));		
        assertEquals("7", (String) results.get("brthdat"));
        assertEquals(String.valueOf(999), (String) results.get("icuadmitdat"));	        
        assertEquals("", (String) results.get("fluvac"));
        assertEquals("0", (String) results.get("plateletyn"));
        assertEquals(null, results.get("platelet"));
        assertEquals("", (String) results.get("urineyn"));
        assertEquals(null, results.get("urine"));      
        assertEquals("1", (String) results.get("creatinineyn"));
        assertEquals(BigDecimal.valueOf(0.8), (BigDecimal) results.get("creatinine"));
        assertEquals("2", (String) results.get("creatinineunt"));
        assertEquals("", (String) results.get("vasopressup"));
        assertEquals("", (String) results.get("vasopresstype___1"));       
        assertEquals("", (String) results.get("vasopresstype___2"));               
        assertEquals("", (String) results.get("vasopresstype___3"));
        assertEquals("", (String) results.get("vasopresstype___4"));
        assertEquals("", (String) results.get("vasopresstype___5"));  
        assertEquals("", (String) results.get("vasopresstype___6"));  
        assertEquals("", (String) results.get("vasopresstype___7"));  
        assertEquals("", (String) results.get("vasopresstype___8"));  
        assertEquals("", (String) results.get("vasopresstype___9"));          
        assertEquals("", (String) results.get("antiviral"));
        assertEquals("", (String) results.get("av_names___1"));
        assertEquals("", (String) results.get("av_names___2"));        
        assertEquals("", (String) results.get("av_names___4"));
        assertEquals("", (String) results.get("av_names___5"));
        assertEquals("", (String) results.get("av_names___6"));
        assertEquals("", (String) results.get("av_names___7"));
        assertEquals("", (String) results.get("osel_tube"));
        assertEquals("", (String) results.get("osel_days"));
        assertEquals("", (String) results.get("osel_dose"));
        assertEquals("", (String) results.get("antibiotic"));
        assertEquals("", (String) results.get("corticosteroid"));
        assertEquals("", (String) results.get("antifungal"));  
	}
	
	@Ignore
	public void testExecuteFluStudyCerner() throws IOException, JAXBException {
		//open Cerner server unable to handle datetime format in search params, okay with date only but CQL datetime automatically adds T00:00:00.000
		//error 400 bad request date: invalid format: the value must be in FHIR date, dateTime, or instant format
		//alternate patient 2744010
		Map<String, Object> results = testExecuteFluStudy("https://fhir-open.sandboxcerner.com/dstu2/0b8a0111-e8e6-4c26-a91c-5069cbc6b1ca", "1316024", "2016-04-18", "2016-04-28");
		      
        assertEquals(String.valueOf(2), (String) results.get("icufluinclusion"));	
        //assertEquals("1", (String) results.get("confirmatory_test"));
        assertEquals(String.valueOf(1), (String) results.get("sex"));		
        assertEquals("8", (String) results.get("brthdat"));
        assertEquals(String.valueOf(999), (String) results.get("icuadmitdat"));	        
        assertEquals("", (String) results.get("fluvac"));
        assertEquals("0", (String) results.get("plateletyn"));
        assertEquals(null, results.get("platelet"));
        assertEquals("", (String) results.get("antiviral"));
        //private expressions
        assertEquals(null, results.get("IcuAdmitDate"));    
	}
	
	private Map<String, Object> testExecuteFluStudy(String endpoint, String patientId, String encounterStart, String encounterEnd) throws IOException, JAXBException {
		UsciitgFhirDataProviderHL7 dataProvider = new UsciitgFhirDataProviderHL7();
		dataProvider.setFhirContext(FhirContext.forDstu2Hl7Org());
		dataProvider.setPackageName("org.hl7.fhir.instance.model");
		dataProvider.setEndpoint(endpoint);

		TerminologyProvider terminologyProvider = new TestTerminologyProvider();
		dataProvider.setTerminologyProvider(terminologyProvider);			
		
		UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
				new ModelManager(),
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");
		LibraryLoader libraryLoader = new LibraryLoaderImpl(libraryManager);
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("usciitg_flu_study");
		
		Map<String, Object> contextValues = new HashMap<String, Object>();
		Map<String, Object> contextParameters = new HashMap<String, Object>();
		Map<String, Object> results;

		contextValues.put("Patient", patientId);

		DateTime encounterStartDt = createCqfDateTime(encounterStart);
		DateTime encounterEndDt = createCqfDateTime(encounterEnd);
		contextParameters.put("EncounterStart", encounterStartDt);
		contextParameters.put("EncounterEnd", encounterEndDt);

		CqlExecutor executor = new CqlFhirExecutorImpl(dataProvider, terminologyProvider, libraryIdentifier, libraryLoader);
		results = executor.execute(contextValues, contextParameters);
		
		return results;
	}

	private DateTime createCqfDateTime(String dateTimeString){
		if(dateTimeString.contains("T")){
			DateTimeType d = new DateTimeType(dateTimeString);
			return DateTime.fromJavaDate(d.getValue());
		}else{
			DateType d = new DateType(dateTimeString);
			return DateTime.fromJavaDate(d.getValue());
		}
	}

	/**
	 * Load test data files onto HAPI test server using FileBasedFhirDstu2Provider
	 * Assumes each resource has an identifier with system http://mcm.usciitg-prep.org 
	 * and value equal to json file resource Id
	 * 
	 * @throws JAXBException
	 */
	@BeforeClass  
	public static void loadHapiTestData() throws JAXBException {
		FhirContext fhirContext = FhirContext.forDstu2Hl7Org();
		FileBasedFhirDstu2Provider provider = new FileBasedFhirDstu2Provider(System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql/data", null);
		provider.setFhirContext(fhirContext);
		provider.setPackageName("org.hl7.fhir.instance.model");

		UsciitgLibraryManager libraryManager = new UsciitgLibraryManager(
				new ModelManager(),
				System.getProperty("user.dir") + "/src/test/resources/org/partners/usciitg_prep/fhir/cql");
		LibraryLoader libraryLoader = new LibraryLoaderImpl(libraryManager);
		VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
			.withId("usciitg_flu_study");
		Context context = new Context(libraryLoader.load(libraryIdentifier));

		TerminologyProvider terminologyProvider = new TestTerminologyProvider();
		provider.setTerminologyProvider(terminologyProvider);

		context.registerDataProvider("http://hl7.org/fhir", provider);
		context.registerTerminologyProvider(terminologyProvider);

		context.setContextValue("Patient", "fhirTest");

		IGenericClient client = fhirContext.newRestfulGenericClient("http://fhirtest.uhn.ca/baseDstu2");

		Iterable<Object> resources = provider.retrieve("Patient", "fhirTest", "Patient", null, null, null, null, null, null, null, null);		
		createServerResources(client, resources, "Patient");		
		resources = provider.retrieve("Patient", "fhirTest", "Observation", null, null, null, null, null, null, null, null);
		deleteServerResources(client, resources, "Observation");
		createServerResources(client, resources, "Observation");
		resources = provider.retrieve("Patient", "fhirTest", "MedicationOrder", null, null, null, null, null, null, null, null);
		deleteServerResources(client, resources, "MedicationOrder");
		createServerResources(client, resources, "MedicationOrder");
		resources = provider.retrieve("Patient", "fhirTest", "Immunization", null, null, null, null, null, null, null, null);
		deleteServerResources(client, resources, "Immunization");
		createServerResources(client, resources, "Immunization");
	}

	private static void createServerResources(IGenericClient client, Iterable<Object> resources, String dataType){
		for(Object obj : resources){
			IBaseResource resource = (IBaseResource) obj;
		
			//attempt to update existing
			MethodOutcome outcome = client.update()
					.resource(resource)
					.conditional()
					.where(new TokenClientParam("identifier").exactly()
						.systemAndCode("http://mcm.usciitg-prep.org", resource.getIdElement().getIdPart()))
					.execute();

			// This will return Boolean.TRUE if the server responded with an HTTP 201 created,
			// otherwise it will return null.
			Boolean created = outcome.getCreated();
			if(created == null){
				//try creating new
				outcome = client.create()
						.resource(resource)
						.conditional()
						.where(new TokenClientParam("identifier").exactly()
								.systemAndCode("http://mcm.usciitg-prep.org", resource.getIdElement().getIdPart()))
						.execute();	
				created = outcome.getCreated();
			}
						
			IIdType id = (IIdType) outcome.getId();
			System.out.println(dataType + ":" + resource.getIdElement().getValue() + " created:" + created + " ID:" + id.getValue());
		}
	}	
	
	private static void deleteServerResources(IGenericClient client, Iterable<Object> resources, String dataType){
		for(Object obj : resources){
			IBaseResource resource = (IBaseResource) obj;
			try{
				//delete existing
				client.delete()
					.resourceConditionalByType(resource.getClass())
					.where(new TokenClientParam("identifier").exactly()
						.systemAndCode("http://mcm.usciitg-prep.org", resource.getIdElement().getIdPart()))
					.execute();
				System.out.println(dataType + ":" + resource.getIdElement().getValue() + " deleted ID:" + resource.getIdElement().getIdPart());
			}catch(Exception e){
				System.out.println(dataType + ":" + resource.getIdElement().getValue() + " deleted: failure" + " ID:" + resource.getIdElement().getIdPart());
			}			
		}
	}
}