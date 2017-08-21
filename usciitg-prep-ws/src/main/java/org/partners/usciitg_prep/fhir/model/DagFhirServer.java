package org.partners.usciitg_prep.fhir.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the dag_fhir_servers database table.
 * 
 */
@Entity
@Table(name="dag_fhir_servers")
@NamedQueries({
@NamedQuery(name="DagFhirServer.findAll", query="SELECT d FROM DagFhirServer d"),
@NamedQuery(
	    name="DagFhirServer.findByGroupId",
	    query="SELECT d FROM DagFhirServer d WHERE d.groupId = :group_id"
	)
})
public class DagFhirServer implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String fhirEndpointPassword;
	private String fhirEndpointUrl;
	private String fhirEndpointUsername;
	private Integer groupId;

	public DagFhirServer() {
	}


	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="fhir_Endpoint_password")
	public String getFhirEndpointPassword() {
		return this.fhirEndpointPassword;
	}

	public void setFhirEndpointPassword(String fhirEndpointPassword) {
		this.fhirEndpointPassword = fhirEndpointPassword;
	}


	@Column(name="fhir_Endpoint_url")
	public String getFhirEndpointUrl() {
		return this.fhirEndpointUrl;
	}

	public void setFhirEndpointUrl(String fhirEndpointUrl) {
		this.fhirEndpointUrl = fhirEndpointUrl;
	}


	@Column(name="fhir_Endpoint_username")
	public String getFhirEndpointUsername() {
		return this.fhirEndpointUsername;
	}

	public void setFhirEndpointUsername(String fhirEndpointUsername) {
		this.fhirEndpointUsername = fhirEndpointUsername;
	}


	@Column(name="group_id")
	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

}