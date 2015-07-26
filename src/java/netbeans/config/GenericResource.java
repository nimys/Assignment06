/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package netbeans.config;

import entity.Product;
import entity.ProductList;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author c0646039
 */
@Path("/products")
@RequestScoped
public class GenericResource {
@Inject
    ProductList productList;
    
    @GET
    @Produces("application/json")
    public Response getAll() {
        return Response.ok(productList.toJSON()).build();
    }

    @GET
    @Path("{id}")
    @Produces("application/json")
    public Response getById(@PathParam("id") int id) {
        return Response.ok(productList.get(id).toJSON()).build();
    }

    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response response;
        try{
            productList.add(new Product(json));
            response = Response.ok(productList.get(json.getInt("productID")).toJSON()).build();
        } catch(Exception ex){
            response = Response.status(500).build();
        }
        return response;
    }

    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response set(@PathParam("id") int id, JsonObject json) {
        Response response;
        try{
            Product m = new Product(json);
            productList.set(id, m);
            response = Response.ok(productList.get(id).toJSON()).build();
        } catch(Exception ex){
            response = Response.status(500).build();
        }
        return response;
    }
    
    
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        Response response;
        try{
            productList.remove(id);
            response = Response.ok("Deleted product with id "+id).build();
        } catch(Exception ex){
            response = Response.status(500).build();
        }
        return response;
    }
}
