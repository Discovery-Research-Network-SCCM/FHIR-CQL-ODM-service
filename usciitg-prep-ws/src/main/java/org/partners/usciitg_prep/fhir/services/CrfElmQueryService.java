package org.partners.usciitg_prep.fhir.services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.partners.usciitg_prep.fhir.model.CrfElmQuery;

@Stateless
public class CrfElmQueryService {
	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<CrfElmQuery> findAll() {
		try {
			return (List<CrfElmQuery>) em.createNamedQuery("CrfElmQuery.findAll").getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CrfElmQuery> findByGroupId(Integer project_id, Integer event_id, String instrument) {
		try {
			if(event_id == null){//not required
				return (List<CrfElmQuery>) em.createNamedQuery("CrfElmQuery.findByProjectInstrument")
					.setParameter("project_id", project_id)
					.setParameter("instrument", instrument)
					.getResultList();												
			}else{
				return (List<CrfElmQuery>) em.createNamedQuery("CrfElmQuery.findByProjectEventInstrument")
				.setParameter("project_id", project_id)
				.setParameter("event_id", event_id)
				.setParameter("instrument", instrument)
				.getResultList();
			}
		} catch (NoResultException e) {
			return null;
		}
	}
}
