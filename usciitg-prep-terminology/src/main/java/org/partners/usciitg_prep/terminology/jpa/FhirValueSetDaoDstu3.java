package org.partners.usciitg_prep.terminology.jpa;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.DaoMethodOutcome;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.dao.SearchParameterMap;
import ca.uhn.fhir.jpa.entity.BaseHasResource;
import ca.uhn.fhir.jpa.entity.ResourceTable;
import ca.uhn.fhir.jpa.entity.TagTypeEnum;
import ca.uhn.fhir.jpa.search.PersistedJpaBundleProvider;
import ca.uhn.fhir.jpa.util.DeleteConflict;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.model.api.TagList;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.IBundleProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * stripped down version of value set DAO for testing purposes
 * supports only the read operation
 */
@Repository
public class FhirValueSetDaoDstu3 implements IFhirResourceDao<ValueSet> {
	@Autowired(required = true)
	private DaoConfig myConfig;
	
	private FhirContext myContext;
	
	@PersistenceContext(type = PersistenceContextType.TRANSACTION)
	protected EntityManager myEntityManager;
	
	public FhirContext getContext() {
		return myContext;
	}

	public void injectDependenciesIntoBundleProvider(
			PersistedJpaBundleProvider theProvider) {
		throw new UnsupportedOperationException();
	}

	public <R extends IBaseResource> Set<Long> processMatchUrl(
			String theMatchUrl, Class<R> theResourceType) {
		throw new UnsupportedOperationException();
	}

	public IBaseResource toResource(BaseHasResource theEntity,
			boolean theForHistoryOperation) {
		throw new UnsupportedOperationException();
	}

	public <R extends IBaseResource> R toResource(Class<R> theResourceType,
			BaseHasResource theEntity, boolean theForHistoryOperation) {
		return null;
	}

	public void populateFullTextFields(IBaseResource theResource,
			ResourceTable theEntity) {
		throw new UnsupportedOperationException();
	}

	public void addTag(IIdType theId, TagTypeEnum theTagType, String theScheme,
			String theTerm, String theLabel) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome create(ValueSet theResource) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome create(ValueSet theResource,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome create(ValueSet theResource, String theIfNoneExist) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome create(ValueSet theResource, String theIfNoneExist,
			boolean thePerformIndexing, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome create(ValueSet theResource, String theIfNoneExist,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome delete(IIdType theResource) {
		throw new UnsupportedOperationException();
	}

	public ResourceTable delete(IIdType theResource,
			List<DeleteConflict> theDeleteConflictsListToPopulate,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome delete(IIdType theResource,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public List<ResourceTable> deleteByUrl(String theUrl,
			List<DeleteConflict> theDeleteConflictsListToPopulate,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome deleteByUrl(String theString,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public TagList getAllResourceTags(RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public Class<ValueSet> getResourceType() {
		return null;
	}

	public TagList getTags(IIdType theResourceId,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public IBundleProvider history(Date theSince, Date theUntil,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public IBundleProvider history(IIdType theId, Date theSince, Date theUntil,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public <MT extends IBaseMetaType> MT metaAddOperation(IIdType theId1,
			MT theMetaAdd, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public <MT extends IBaseMetaType> MT metaDeleteOperation(IIdType theId1,
			MT theMetaDel, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType,
			IIdType theId, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public <MT extends IBaseMetaType> MT metaGetOperation(Class<MT> theType,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public Set<Long> processMatchUrl(String theMatchUrl) {
		throw new UnsupportedOperationException();
	}

	public ValueSet read(IIdType theId) {
		return read(theId, null);
	}

	public ValueSet read(IIdType theId, RequestDetails theRequestDetails) {	
		ValueSet retVal = myEntityManager.find(ValueSet.class, theId.getIdPart());
		
		if (retVal == null) {
			throw new ResourceNotFoundException(theId);
		}				
		
		return retVal;
	}

	public BaseHasResource readEntity(IIdType theId) {
		throw new UnsupportedOperationException();
	}

	public BaseHasResource readEntity(IIdType theId, boolean theCheckForForcedId) {
		throw new UnsupportedOperationException();
	}

	public void reindex(ValueSet theResource, ResourceTable theEntity) {
		throw new UnsupportedOperationException();
	}

	public void removeTag(IIdType theId, TagTypeEnum theTagType,
			String theScheme, String theTerm, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public IBundleProvider search(Map<String, IQueryParameterType> theParams) {
		throw new UnsupportedOperationException();
	}

	public IBundleProvider search(SearchParameterMap theMap) {
		throw new UnsupportedOperationException();
	}

	public IBundleProvider search(String theParameterName,
			IQueryParameterType theValue) {
		throw new UnsupportedOperationException();
	}

	public Set<Long> searchForIds(Map<String, IQueryParameterType> theParams) {
		throw new UnsupportedOperationException();
	}

	public Set<Long> searchForIds(String theParameterName,
			IQueryParameterType theValue) {
		throw new UnsupportedOperationException();
	}

	public Set<Long> searchForIdsWithAndOr(SearchParameterMap theParams) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome update(ValueSet theResource) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome update(ValueSet theResource,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome update(ValueSet theResource, String theMatchUrl) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome update(ValueSet theResource, String theMatchUrl,
			boolean thePerformIndexing, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome update(ValueSet theResource, String theMatchUrl,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public MethodOutcome validate(ValueSet theResource, IIdType theId,
			String theRawResource, EncodingEnum theEncoding,
			ValidationModeEnum theMode, String theProfile,
			RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}

	public DaoMethodOutcome patch(IIdType theId, PatchTypeEnum thePatchType,
			String thePatchBody, RequestDetails theRequestDetails) {
		throw new UnsupportedOperationException();
	}
}
