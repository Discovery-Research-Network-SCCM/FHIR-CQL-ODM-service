package org.partners.usciitg_prep.fhir.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.ResourceType;
import org.opencds.cqf.cql.execution.LibraryLoader;
import org.opencds.cqf.cql.runtime.DateTime;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.cql.terminology.fhir.JpaFhirTerminologyProvider;
import org.partners.usciitg_prep.data.model.PatientInformation;
import org.partners.usciitg_prep.fhir.cql.CqlExecutor;
import org.partners.usciitg_prep.fhir.cql.CqlFhirExecutorImpl;
import org.partners.usciitg_prep.fhir.cql.LibraryLoaderImpl;
import org.partners.usciitg_prep.fhir.cql.UsciitgFhirDataProviderHL7;
import org.partners.usciitg_prep.fhir.cql.UsciitgLibraryManager;
import org.partners.usciitg_prep.terminology.jpa.FhirTerminologyProviderService;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;

public class FhirUtil {
	private static Logger logger = Logger.getLogger(FhirUtil.class);

	private static UsciitgFhirDataProviderHL7 dataProvider;
	private static TerminologyProvider terminologyProvider;
	private static Map<String, Object> contextValues; 	
	private static Map<String, Object> contextParameters;
	private static Map<String, UsciitgLibraryManager> libraryManagers;

	FhirTerminologyProviderService fhirTerminologyProviderService;	
	
	public FhirUtil(FhirTerminologyProviderService fhirTerminologyProviderService){
		this.fhirTerminologyProviderService = fhirTerminologyProviderService;
		terminologyProvider = new JpaFhirTerminologyProvider(fhirTerminologyProviderService.getValueSetProvider(),
				fhirTerminologyProviderService.getCodeSystemProvider());		
		
		contextValues = new HashMap<String, Object>();
		contextParameters = new HashMap<String, Object>();
		
		libraryManagers = new HashMap<String, UsciitgLibraryManager>();
	}
	
	public PatientInformation getPatient(String endpoint, String PatientId) throws UnsupportedEncodingException {
		FhirContext ctx = FhirContext.forDstu2Hl7Org();// ca.uhn.fhir.model.dstu2.resource.Patient
		IGenericClient client = ctx.newRestfulGenericClient(endpoint);
		org.hl7.fhir.instance.model.Bundle bundleResult = client.search()
				.byUrl("/Patient?_id=" + URLEncoder.encode(PatientId, "UTF-8"))
				.returnBundle(org.hl7.fhir.instance.model.Bundle.class)
				.execute();

		if (bundleResult != null && bundleResult.hasEntry()) {
			for (Bundle.BundleEntryComponent bundleEntryComponent : bundleResult
					.getEntry()) {
				if (bundleEntryComponent.getResource().getResourceType() == ResourceType.Patient) {
					Patient patient = (Patient) bundleEntryComponent
							.getResource();
					String resultName = "";
					if (patient.getName() != null
							&& patient.getName().size() > 0) {
						if (patient.getName().get(0).getFamily() != null
								&& patient.getName().get(0).getFamily()
										.size() > 0) {
							resultName = patient.getName().get(0)
									.getFamily().get(0).getValueNotNull();
						}
						if (patient.getName().get(0).getGiven() != null
								&& patient.getName().get(0).getGiven()
										.size() > 0) {
							if (!resultName.isEmpty()) {
								resultName += ", ";
							}
							resultName += patient.getName().get(0)
									.getGiven().get(0).getValueNotNull();
						}
					}

					PatientInformation patientInformation = new PatientInformation();
					patientInformation.setName(resultName);
					patientInformation.setId(patient.getId());

					if (patient.getBirthDate() == null) {
						patientInformation.setDoB(null);
					} else {
						patientInformation.setDoB(patient.getBirthDate()
								.toString());
					}

					return patientInformation;
				}
			}
		}
		return null;
	}
	
	public Map<String, Object> getData(String path, VersionedIdentifier libraryIdentifier, String endpoint, 
			String PatientId, String encounterStart, String encounterEnd) throws JAXBException {
		setupDataProvider(endpoint);	
		LibraryLoader libraryLoader = new LibraryLoaderImpl(getLibraryManager(path));

		contextValues.clear();
		contextValues.put("Patient", PatientId);

		contextParameters.clear();
		DateTime encounterStartCqf = createCqfDateTime(encounterStart); 
		contextParameters.put("EncounterStart", encounterStartCqf);
		if(encounterEnd != null){
			DateTime encounterEndCqf = createCqfDateTime(encounterEnd); 	
			contextParameters.put("EncounterEnd", encounterEndCqf);	
		}

		Map<String, Object> results = null;
		
		CqlExecutor executor = new CqlFhirExecutorImpl(dataProvider, terminologyProvider, libraryIdentifier, libraryLoader);		
		logger.debug("Start executor Patient: " + PatientId);
		results = executor.execute(contextValues, contextParameters);
		logger.debug("End executor Patient: " + PatientId);

		return results;
	}
	
	private void setupDataProvider(String url){
		if(dataProvider == null){
			dataProvider = new UsciitgFhirDataProviderHL7();
			dataProvider.setFhirContext(FhirContext.forDstu2Hl7Org());
			dataProvider.setPackageName("org.hl7.fhir.instance.model");
			dataProvider.setTerminologyProvider(terminologyProvider);
		}

		dataProvider.setEndpoint(url);		
	}
	
	private UsciitgLibraryManager getLibraryManager(String path){
		UsciitgLibraryManager libraryManager = libraryManagers.get(path);
		
		if(libraryManager == null){			
			libraryManager = new UsciitgLibraryManager(new ModelManager(), path);
			libraryManagers.put(path, libraryManager);
		}
		
		return libraryManager;
	}

	private DateTime createCqfDateTime(String dateTimeString){
		DateTimeType d = new DateTimeType(dateTimeString);
		return DateTime.fromJavaDate(d.getValue());
	}
}
