package services;

import com.fasterxml.jackson.databind.JsonNode;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("product")
@Singleton
public class Product {

    @Path("{id : .+}")
    public model.Product get(@PathParam("id") String id) {
        return backend.Product.Get(id);
    }

    @GET
    @Path("newest/{offset : \\d+}/{limit : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.ProductSearch> newest(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Product.GetNewest(offset, limit);
    }

    @GET
    @Path("newestInStock/{offset : \\d+}/{limit : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.ProductSearch> newestInStock(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Product.GetNewestInStock(offset, limit);
    }

    @GET
    @Path("recommend/weekly/{offset : \\d+}/{limit : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.Product> recommendWeekly(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Product.GetBestSeller(604800000L, offset, limit);
    }

    @GET
    @Path("recommend/monthly/{offset : \\d+}/{limit : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.Product> recommendMonthly(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Product.GetBestSeller(2592000000L, offset, limit);
    }

    @GET
    @Path("recommend/yearly/{offset : \\d+}/{limit : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.Product> recommendYearly(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Product.GetBestSeller(31536000000L, offset, limit);
    }

    @GET
    @Path("recommend/all-time/{offset : \\d+}/{limit : \\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.Product> recommendAllTime(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Product.GetBestSellerAllTime(offset, limit);
    }

    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public long count() {
        return backend.Product.Count();
    }

    @GET
    @Path("count/{queryString: .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response count(
            @PathParam("queryString") String queryString,
            @CookieParam("product-last-queryString") String lastQueryString,
            @DefaultValue("-1") @CookieParam("product-last-update") long lastUpdate,
            @DefaultValue("-1") @CookieParam("product-last-count") long lastCount) {
        long currLastUpdate = backend.Product.GetLastUpdate();
        if(queryString == lastQueryString && lastUpdate == currLastUpdate) return Response.ok(lastCount).build();
        lastCount = backend.Product.Count(queryString);
        return Response.ok(lastCount).cookie(
                new NewCookie("product-last-queryString", queryString),
                new NewCookie("product-last-update", "" + currLastUpdate),
                new NewCookie("product-last-count", "" + lastCount)).build();
    }

    @POST
    @Path("search")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.ProductSearch> search(JsonNode json) {
        return backend.Product.Search(json);
    }

    @POST
    @Path("add")
    @RolesAllowed("employee")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String add(
            @FormDataParam("name") String name,
            @FormDataParam("description") String description,
            @FormDataParam("tags") String tags,
            @FormDataParam("price") double price,
            @FormDataParam("amount") double amount,
            @FormDataParam("minimumStock") double minimumStock,
            @FormDataParam("sold") double sold,
            @FormDataParam("date") long date,
            @FormDataParam("imageType") String imgType,
            @FormDataParam("image") byte[] img

    ) {
        return backend.Product.Add(name, tags, description, img, imgType, price, amount, minimumStock, sold, date);
    }

    @POST
    @Path("update")
    @RolesAllowed("employee")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public long update(
            @FormDataParam("id") String id,
            @FormDataParam("name") String name,
            @FormDataParam("description") String description,
            @FormDataParam("tags") String tags,
            @FormDataParam("price") double price,
            @FormDataParam("amount") double amount,
            @FormDataParam("minimumStock") double minimumStock,
            @FormDataParam("sold") double sold,
            @FormDataParam("isNewImg") boolean isNewImg,
            @FormDataParam("imageType") String imgType,
            @FormDataParam("image") byte[] img,
            @FormDataParam("isNewDate") boolean isNewDate,
            @FormDataParam("date") long date,
            @FormDataParam("lastUpdatedTime") long lastUpdatedTime

    ) {
        return backend.Product.Update(id, name, tags, description, img, imgType, isNewImg, price, amount, minimumStock, sold, isNewDate, date, lastUpdatedTime);
    }

    @POST
    @Path("addAmount/{id: .+}")
    @RolesAllowed("employee")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public int addAmount(@PathParam("id") String id, double amount) {
        return backend.Product.AddAmount(id, amount);
    }

    @POST
    @Path("updateImg/{id: .+}")
    @RolesAllowed("employee")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean updateImg(
            @PathParam("id") String id,
            @FormDataParam("imageType") String imgType,
            @FormDataParam("image") byte[] img
    ) {
        if(backend.Product.UpdateImgFromId(id, img, imgType) != null) return true;
        else return false;
    }

    @DELETE
    @Path("deleteImg")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteImg(String id) {
        backend.Product.DeleteImgFromId(id);
        return Response.noContent().build();
    }

    @DELETE
    @Path("delete")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String id) {
        backend.Product.Delete(id);
        return Response.noContent().build();
    }
}