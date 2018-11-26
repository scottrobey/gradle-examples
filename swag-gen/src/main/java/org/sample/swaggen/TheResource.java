package org.sample.swaggen;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path("/theresource")
@Api(tags = "theresource")
public class TheResource {

    @GET
    @ApiOperation("my test endpoint")
    public String endpoint() {
        return "YoYoMa!";
    }
}
