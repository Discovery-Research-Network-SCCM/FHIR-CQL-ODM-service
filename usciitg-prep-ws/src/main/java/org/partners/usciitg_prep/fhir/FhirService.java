package org.partners.usciitg_prep.fhir;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.cdisc.ns.odm.v1.ODM;
import org.cdisc.ns.odm.v1.ODMcomplexTypeDefinitionClinicalData;
import org.cdisc.ns.odm.v1.ODMcomplexTypeDefinitionFormData;
import org.cdisc.ns.odm.v1.ODMcomplexTypeDefinitionItemData;
import org.cdisc.ns.odm.v1.ODMcomplexTypeDefinitionItemGroupData;
import org.cdisc.ns.odm.v1.ODMcomplexTypeDefinitionStudyEventData;
import org.cdisc.ns.odm.v1.ODMcomplexTypeDefinitionSubjectData;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.jboss.logging.Logger;
import org.partners.usciitg_prep.data.model.PatientInformation;
import org.partners.usciitg_prep.fhir.model.CrfElmQuery;
import org.partners.usciitg_prep.fhir.model.DagFhirServer;
import org.partners.usciitg_prep.fhir.services.CrfElmQueryService;
import org.partners.usciitg_prep.fhir.services.DagFHIRServerService;
import org.partners.usciitg_prep.fhir.util.FhirUtil;
import org.partners.usciitg_prep.odm.OdmUtil;
import org.partners.usciitg_prep.terminology.jpa.FhirTerminologyProviderService;

@Path("/fhir")
public class FhirService {
	Logger logger = Logger.getLogger(FhirService.class);

	@Inject
	private DagFHIRServerService dagFHIRServerService;
	@Inject
	private CrfElmQueryService crfElmQueryService;

	private FhirUtil fhirUtil;

	public void initialize(
			FhirTerminologyProviderService fhirTerminologyProviderService)
			throws JAXBException {
		this.fhirUtil = new FhirUtil(fhirTerminologyProviderService);
	}

	/**
	 * @param mrn Patient local identifier.
	 * @param group_id Group identifier associated with clinical user
	 * @return Patient object with name and date of birth associated with mrn
	 */
	@Path("/patient/{mrn}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPatient(@PathParam("mrn") String mrn,
			@QueryParam("group_id") Integer group_id) {
		logger.info("Find patient with MRN : "
				+ mrn
				+ ", group id: "
				+ (group_id == null ? "no group id provided" : group_id
						.toString()));

		DagFhirServer srv = dagFHIRServerService.findByGroupId(group_id);
		if (srv == null) {
			logger.error("(Find Patient) FHIR server not found for MRN : "
					+ mrn + ", group id: "
					+ (group_id == null ? "no group id provided" : group_id));
			return Response.serverError()
					.entity("FHIR server not found for group_id " + group_id)
					.build();
		}

		try {
			PatientInformation patientInformation = fhirUtil.getPatient(
					srv.getFhirEndpointUrl(), mrn);

			if (patientInformation != null) {
				return Response.ok().entity(patientInformation).build();
			}
		} catch (Exception ex) {
			logger.error("Unable to return services: " + ex.getMessage(), ex);
			return Response
					.serverError()
					.entity("Error retrieving patient information for mrn: "
							+ mrn).build();
		}

		return Response.serverError().entity("Unable to retrieve patient.")
				.build();
	}

	/**
	 * 	/**
	 * Executes project, event and instrument specific CQL scripts using patient data associated with group and patient identifier.
	 * Creates simplified CDISC ODM ClinicalData section from CQL execution results. Does not currently support Item Group or repeating sections. 
	 * @param project_id Project identifier
	 * @param event_id Event identifier
	 * @param instrument Instrument name
	 * @param group_id Group identifier associated with clinical user
	 * @param patientId Local patient identifier associated with group site (MRN)
	 * @param encounter_start Start date of encounter
	 * @param encounter_end End date of encounter
	 * @return
	 */
	@Path("/patient/{mrn}/odm")
	@GET
	@Produces("application/xml")
	public Response getODM(@QueryParam("project_id") Integer project_id,
			@QueryParam("event_id") Integer event_id,
			@QueryParam("instrument") String instrument,
			@QueryParam("group_id") Integer group_id,
			@PathParam("mrn") String patientId,
			@QueryParam("encounter_start") String encounter_start,
			@QueryParam("encounter_end") String encounter_end) {
		logger.debug("Get ODM for patientId: "
				+ patientId
				+ ", project_id: "
				+ (project_id == null ? "no project_id id provided" : project_id.toString())
				+ ", group_id: "
				+ (group_id == null ? "no group_id provided" : group_id.toString())
				+ ", event_id id: "
				+ (event_id == null ? "no event_id id provided" : event_id.toString()) 
				+ ", instrument: "
				+ (instrument == null ? "no instrument provided" : instrument));

		// validate patientId, encounter_start, encounter_end
		if (patientId == null || patientId.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Missing required Patient ID").build();
		} else if (encounter_start == null || encounter_start.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Missing required encounter start").build();
		} else if (encounter_end == null || encounter_end.isEmpty()) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Missing required encounter end").build();
		}

		DagFhirServer srv = dagFHIRServerService.findByGroupId(group_id);
		if (srv == null) {
			logger.error("(Request ODM) FHIR server not found for patientId: "
					+ patientId
					+ ", group_id: "
					+ (group_id == null ? "no group_id provided" : group_id.toString()));
			return Response.serverError()
					.entity("FHIR server not found for group_id " + group_id.toString()).build();
		}

		List<CrfElmQuery> elmQueries = crfElmQueryService.findByGroupId(
				project_id, event_id, instrument);

		if (elmQueries.isEmpty()) {
			return Response
					.serverError()
					.entity("No queries available for project ID: "
							+ project_id + ", instrument: " + instrument)
					.build();
		}

		try {						
			VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
					.withId(elmQueries.get(0).getIdentifier())
					.withSystem(elmQueries.get(0).getSystem())
					.withVersion(elmQueries.get(0).getVersion());
			
			Map<String, Object> results = fhirUtil.getData(
					elmQueries.get(0).getPath(), 
					libraryIdentifier,
					srv.getFhirEndpointUrl(), patientId, encounter_start,
					encounter_end);

			ODM studyOdm = createODM(results, project_id, event_id, instrument,
					patientId);
			String odmXML = OdmUtil.getODMXMLString(studyOdm);
			logger.debug("Get ODM results for patientId " + patientId + ": "
					+ odmXML);

			logger.debug("Get ODM completed for patientId: "
					+ patientId
					+ ", project_id: "
					+ (project_id == null ? "no project_id id provided"
							: project_id.toString())
					+ ", group_id: "
					+ (group_id == null ? "no group id provided" : group_id.toString())
					+ ", event_id id: "
					+ (event_id == null ? "no event_id id provided" : event_id.toString())
					+ ", instrument: "
					+ (instrument == null ? "no instrument provided"
							: instrument));
			return Response.ok().entity(odmXML).build();
		} catch (Exception ex) {
			logger.error(
					"Error retrieving patient data for patientId: "
							+ patientId
							+ ", project_id: "
							+ (project_id == null ? "no project_id id provided"
									: project_id.toString())
							+ ", group id: "
							+ (group_id == null ? "no group id provided"
									: group_id.toString())
							+ ", event_id id: "
							+ (event_id == null ? "no event_id id provided"
									: event_id.toString())
							+ ", instrument: "
							+ (instrument == null ? "no instrument provided"
									: instrument), ex);
			return Response
					.serverError()
					.entity("Error retrieving patient data for mrn: "
							+ patientId).build();
		}
	}


	private ODM createODM(Map<String, Object> results, Integer project_id,
			Integer event_id, String instrument, String patientId)
			throws JAXBException, DatatypeConfigurationException {
		// List<ODMcomplexTypeDefinitionItemData> items =
		// OdmUtil.getOdmItems(studyODM);

		ODM studyODM = createOdmTemplate(project_id, event_id, instrument,
				patientId);
		List<ODMcomplexTypeDefinitionItemData> items = OdmUtil
				.getOdmGroupData(studyODM);
		for (String key : results.keySet()) {
			if (results.get(key) != null) { // do not add to response if null
				ODMcomplexTypeDefinitionItemData item = new ODMcomplexTypeDefinitionItemData();
				item.setItemOID(key);
				item.setValue(results.get(key).toString());
				items.add(item);
			}
		}

		return studyODM;
	}

	private ODM createOdmTemplate(Integer project_id, Integer event_id,
			String instrument, String patientId)
			throws DatatypeConfigurationException {
		ODM studyTemplate = new ODM();
		studyTemplate.setODMVersion("1.3.2");
		studyTemplate.setCreationDateTime(getXMLGregorianCalendarNow());

		ODMcomplexTypeDefinitionClinicalData clinicalData = new ODMcomplexTypeDefinitionClinicalData();
		studyTemplate.getClinicalData().add(clinicalData);
		clinicalData.setStudyOID(project_id.toString());
		ODMcomplexTypeDefinitionSubjectData subjectData = new ODMcomplexTypeDefinitionSubjectData();
		clinicalData.getSubjectData().add(subjectData);
		subjectData.setSubjectKey(patientId);
		ODMcomplexTypeDefinitionStudyEventData studyEventData = new ODMcomplexTypeDefinitionStudyEventData();
		subjectData.getStudyEventData().add(studyEventData);
		if (event_id != null) {
			studyEventData.setStudyEventOID(event_id.toString());
		}
		ODMcomplexTypeDefinitionFormData formData = new ODMcomplexTypeDefinitionFormData();
		studyEventData.getFormData().add(formData);
		formData.setFormOID(instrument);
		ODMcomplexTypeDefinitionItemGroupData itemGroupData = new ODMcomplexTypeDefinitionItemGroupData();
		formData.getItemGroupData().add(itemGroupData);
		itemGroupData.setItemGroupOID(instrument);

		return studyTemplate;
	}

	private XMLGregorianCalendar getXMLGregorianCalendarNow()
			throws DatatypeConfigurationException {
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
		XMLGregorianCalendar now = datatypeFactory
				.newXMLGregorianCalendar(gregorianCalendar);
		return now;
	}
}
