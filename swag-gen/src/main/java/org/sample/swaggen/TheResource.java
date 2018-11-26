package org.sample.swaggen;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/theresource")
public class TheResource {

    @GET
    public String endpoint() {
        return "YoYoMa!";
    }
}
