package org.partners.usciitg_prep.fhir.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the crf_elm_queries database table.
 * 
 */
@Entity
@Table(name="crf_elm_queries")
@NamedQueries({
@NamedQuery(name="CrfElmQuery.findAll", query="SELECT c FROM CrfElmQuery c"),
@NamedQuery(
	    name="CrfElmQuery.findByProjectEventInstrument",
	    query="SELECT d FROM CrfElmQuery d WHERE d.projectId = :project_id AND d.eventId = :event_id AND d.instrument = :instrument"
	),
@NamedQuery(
	    name="CrfElmQuery.findByProjectInstrument",
	    query="SELECT d FROM CrfElmQuery d WHERE d.projectId = :project_id AND d.eventId IS NULL AND d.instrument = :instrument"
	)
})
public class CrfElmQuery implements Serializable {	
	private static final long serialVersionUID = -6313362840175603242L;
	
	private Integer id;
	private Integer eventId;
	private String identifier;
	private String instrument;
	private String path;
	private Integer projectId;
	private String system;
	private String version;

	public CrfElmQuery() {
	}


	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="event_id")
	public Integer getEventId() {
		return this.eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}


	public String getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public String getInstrument() {
		return this.instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}


	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}


	@Column(name="project_id")
	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}


	public String getSystem() {
		return this.system;
	}

	public void setSystem(String system) {
		this.system = system;
	}


	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}