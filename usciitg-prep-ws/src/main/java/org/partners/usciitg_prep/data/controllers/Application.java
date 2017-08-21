package org.partners.usciitg_prep.data.controllers;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.partners.usciitg_prep.fhir.FhirService;
import org.partners.usciitg_prep.fhir.FhirServiceConfig;
import org.partners.usciitg_prep.terminology.jpa.FhirTerminologyProviderService;
import org.springframework.beans.BeansException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by maxidroms83 on 11/28/16.
 */
@ApplicationPath("/api")
public class Application extends javax.ws.rs.core.Application{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public Application() {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext ctx = 
				   new AnnotationConfigApplicationContext();
		ctx.register(FhirServiceConfig.class);
		ctx.refresh();
        classes.add(CORSFilter.class);

		FhirService fhirService = ctx.getBean(FhirService.class);
		try {
			fhirService.initialize(ctx.getBean(FhirTerminologyProviderService.class));
		} catch (BeansException | JAXBException e) {
			throw new ServerErrorException(Response.Status.SERVICE_UNAVAILABLE, e);
		}
		singletons.add(fhirService);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
