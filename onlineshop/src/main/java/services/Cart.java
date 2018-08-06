package services;

import com.google.appengine.api.search.Document;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("cart")
@PermitAll
@Singleton
public class Cart {

    @GET
    @Path("hasNew")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hasNew(@DefaultValue("-1") @CookieParam("cart-last-update") long lastUpdate) {
        long currLastUpdate = backend.Cart.GetLastUpdate();
        if(lastUpdate == -1) {
            if(backend.Cart.GetIsSearched()) return Response.ok(false).cookie(new NewCookie("cart-last-update", "" + currLastUpdate)).build();
            else Response.ok(true).build();
        }
        else if(lastUpdate == currLastUpdate) return Response.ok(false).build();
        return Response.ok(true).cookie(new NewCookie("cart-last-update", "" + currLastUpdate)).build();
    }

    @GET
    @Path("countPayment")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public long countPayment() {
        return backend.Cart.CountPayment();
    }

    @GET
    @Path("countUnconfirmed")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public long countUnconfirmed() {
        return backend.Cart.CountUnconfirmed();
    }

    @GET
    @Path("countConfirmed")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public long countConfirmed() {
        return backend.Cart.CountConfirmed();
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("countPaymentFromUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countPaymentFromUser(
            @DefaultValue("-1") @CookieParam("cart-last-payment") long lastPayment,
            @DefaultValue("-1") @CookieParam("cart-last-payment-count") long lastPaymentCount) {
        long currPayment = backend.Cart.GetLastPayment();
        if(lastPayment == currPayment) return Response.ok(lastPaymentCount).build();
        lastPaymentCount = backend.Cart.CountPaymentFromUser(backend.Auth.GetID());
        return Response.ok(lastPaymentCount).cookie(
                new NewCookie("cart-last-payment", "" + currPayment),
                new NewCookie("cart-last-payment-count", "" + lastPaymentCount)).build();
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("countUnconfirmedFromUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countUnconfirmedFromUser(
            @DefaultValue("-1") @CookieParam("cart-last-unconfirmed") long lastUnconfirmed,
            @DefaultValue("-1") @CookieParam("cart-last-unconfirmed-count") long lastUnconfirmedCount) {
        long currUnconfirmed = backend.Cart.GetLastUnconfirmed();
        if(lastUnconfirmed == currUnconfirmed) return Response.ok(lastUnconfirmedCount).build();
        lastUnconfirmedCount = backend.Cart.CountUnconfirmedFromUser(backend.Auth.GetID());
        return Response.ok(lastUnconfirmedCount).cookie(
                new NewCookie("cart-last-unconfirmed", "" + currUnconfirmed),
                new NewCookie("cart-last-unconfirmed-count", "" + lastUnconfirmedCount)).build();
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("countConfirmedFromUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countConfirmedFromUser(
            @DefaultValue("-1") @CookieParam("cart-last-confirmed") long lastConfirmed,
            @DefaultValue("-1") @CookieParam("cart-last-confirmed-count") long lastConfirmedCount) {
        long currConfirmed = backend.Cart.GetLastConfirmed();
        if(lastConfirmed == currConfirmed) return Response.ok(lastConfirmedCount).build();
        lastConfirmedCount = backend.Cart.CountConfirmedFromUser(backend.Auth.GetID());
        return Response.ok(lastConfirmedCount).cookie(
                new NewCookie("cart-last-confirmed", "" + currConfirmed),
                new NewCookie("cart-last-confirmed-count", "" + lastConfirmedCount)).build();
    }

    @GET
    @Path("get/{id : .+}")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {
        model.CartDetail cartDetail = backend.Cart.Get(id);
        if(cartDetail == null) return Response.status(Response.Status.BAD_REQUEST).build();
        else return Response.ok(cartDetail).build();
    }

    @GET
    @Path("getFromUser/{id : .+}")
    @RolesAllowed("logged-in")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFromUser(@PathParam("id") String id) {
        model.CartDetail cartDetail = backend.Cart.Get(id);
        if(cartDetail.getAccountId().equals(backend.Auth.GetID())) {
            if(cartDetail == null) return Response.status(Response.Status.BAD_REQUEST).build();
            else return Response.ok(cartDetail).build();
        }
        else return Response.status(Response.Status.FORBIDDEN).build();
    }

    @GET
    @Path("searchPayment/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.CartOut> searchNoBillImg(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        if(limit > offset) return backend.Cart.Search("confirm: 0 AND billImgName: \"\"", offset, limit);
        else return null;
    }

    @GET
    @Path("searchUnconfirmed/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.CartOut> searchUnconfirmed(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Cart.Search("confirm: 0 AND NOT billImgName: \"\"", offset, limit);
    }

    @GET
    @Path("searchConfirmed/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.CartOut> searchConfirm(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Cart.Search("confirm: 1", offset, limit);
    }

    @GET
    @Path("searchPaymentFromUser/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("logged-in")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.CartOut> searchNoBillImgFromId(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Cart.Search("accountId: " + backend.Auth.GetID() + " AND confirm: 0 AND billImgName: \"\"", offset, limit);
    }

    @GET
    @Path("searchUnconfirmedFromUser/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("logged-in")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.CartOut> searchUnconfirmedFromId(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Cart.Search("accountId: " + backend.Auth.GetID() + " AND confirm: 0 AND NOT billImgName: \"\"", offset, limit);
    }

    @GET
    @Path("searchConfirmedFromUser/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("logged-in")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.CartOut> searchConfirmFromId(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.Cart.Search("accountId: " + backend.Auth.GetID() + " AND confirm: 1", offset, limit);
    }

    @POST
    @Path("add")
    @RolesAllowed("logged-in")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean add(model.CartIn cartIn) {
        return backend.Cart.Add(cartIn, backend.Auth.GetID(), backend.Auth.GetUser().getEmail());
    }

    @DELETE
    @Path("delete")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String id) {
        backend.Cart.Delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("updateBillImg/{id: .+}")
    @RolesAllowed("logged-in")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateImg(
            @PathParam("id") String id,
            @FormDataParam("imageType") String imgType,
            @FormDataParam("image") byte[] img
    ) {
        if(img.length == 0) return Response.status(Response.Status.BAD_REQUEST).build();
        String accountId = backend.Auth.GetID();
        Document doc = backend.Cart.GetDocument(id);
        if(doc == null) return Response.status(Response.Status.BAD_REQUEST).build();
        if (!accountId.equals(doc.getOnlyField("accountId").getAtom()))
            return Response.status(Response.Status.FORBIDDEN).build();
        if (backend.Cart.UpdateBillImgFromId(id, accountId, doc, img, imgType) != null)
            return Response.noContent().build();
        else return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("confirm")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirm(String id) {
        Document doc = backend.Cart.GetDocument(id);
        if(doc == null) return Response.status(Response.Status.BAD_REQUEST).build();
        backend.Cart.SetComfirm(id, doc, true);
        return Response.noContent().build();
    }

    @POST
    @Path("unconfirmed")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unconfirmed(String id) {
        Document doc = backend.Cart.GetDocument(id);
        if(doc == null) return Response.status(Response.Status.BAD_REQUEST).build();
        backend.Cart.SetComfirm(id, doc, false);
        return Response.noContent().build();
    }

    @POST
    @Path("cancel")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancle(String id) {
        Document doc = backend.Cart.GetDocument(id);
        if(doc == null) return Response.status(Response.Status.BAD_REQUEST).build();
        backend.Cart.Cancel(id, doc,  Math.abs(doc.getOnlyField("confirm").getNumber()) > 0.5);
        return Response.noContent().build();
    }

    @POST
    @Path("cancelFromUser")
    @RolesAllowed("logged-in")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancleFromUser(String id) {
        Document doc = backend.Cart.GetDocument(id);
        if(doc == null) return Response.status(Response.Status.BAD_REQUEST).build();
        boolean isConfirm = Math.abs(doc.getOnlyField("confirm").getNumber()) > 0.5;
        if(isConfirm) return Response.status(Response.Status.BAD_REQUEST).build();
        if(doc.getOnlyField("accountId").getAtom().equals(backend.Auth.GetID())) {
            backend.Cart.Cancel(id, doc, isConfirm);
            return Response.noContent().build();
        }
        else return Response.status(Response.Status.FORBIDDEN).build();
    }
}
