package org.sample.swaggen;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.swagger.annotations.Api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/base")
@Api(tags = "base")
public class Base {
    @GET
    @Produces(APPLICATION_JSON)
    public boolean getBase() {
        return true;
    }
}
