package org.partners.usciitg_prep.fhir.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.partners.usciitg_prep.fhir.model.DagFhirServer;

@Stateless
public class DagFHIRServerService {
	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<org.partners.usciitg_prep.fhir.model.DagFhirServer> findAll() {
		try {
			return (List<DagFhirServer>) em.createNamedQuery("DagFhirServer.findAll").getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public DagFhirServer findByGroupId(Integer group_id) {
		try {
			return (DagFhirServer) em.createNamedQuery("DagFhirServer.findByGroupId").setParameter("group_id", group_id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
