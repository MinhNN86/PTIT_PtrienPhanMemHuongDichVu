package com.example.student.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/finance")
@Produces(MediaType.APPLICATION_JSON)
public class FinanceResource {

    @GET
    @Path("/tuition")
    public Response calculateTuition(@QueryParam("credits") int credits,
            @QueryParam("majorType") String majorType) {

        long pricePerCredit = 400000; // Default
        if ("IT".equalsIgnoreCase(majorType)) {
            pricePerCredit = 500000;
        }

        long totalAmount = credits * pricePerCredit;

        Map<String, Object> response = new HashMap<>();
        response.put("totalAmount", totalAmount);
        response.put("currency", "VND");

        return Response.ok(response).build();
    }
}
