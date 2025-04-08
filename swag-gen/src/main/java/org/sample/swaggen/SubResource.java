package org.sample.swaggen;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import io.swagger.annotations.Api;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/base/{id}/sub")
@Api(tags = "sub")
public class SubResource {
    @GET
    @Produces(APPLICATION_JSON)
    public boolean getSub(@PathParam("id")  int id) {
        return true;
    }
}
