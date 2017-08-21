package org.partners.usciitg_prep.data.controllers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by maxidroms83 on 1/12/17.
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {


    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext cres) throws IOException {
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        cres.getHeaders().add("Access-Control-Allow-Headers", "*");
    }
}