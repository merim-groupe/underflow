package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.annotation.method.*;
import com.merim.digitalpayment.underflow.annotation.routing.DefaultValue;
import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.annotation.routing.Query;
import com.merim.digitalpayment.underflow.annotation.routing.QueryListProperty;
import com.merim.digitalpayment.underflow.annotation.security.Secured;
import com.merim.digitalpayment.underflow.handlers.flows.FlowTemplateHandler;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.tests.sample.security.MyCookieSecurity;
import com.merim.digitalpayment.underflow.tests.sample.security.MySecurityScope;
import com.merim.digitalpayment.underflow.tests.sample.security.MyUserRepresentation;
import com.merim.digitalpayment.underflow.web.forms.WebForm;
import freemarker.template.Template;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class HomeHandler extends FlowTemplateHandler implements WebForm {

    /**
     * Instantiates a new Test handler.
     */
    public HomeHandler() {
        super("/templates", new MyCookieSecurity());
    }

    /**
     * Simple GET example.
     *
     * @param user     the optional user
     * @param security the security
     * @return the result
     */
    @GET
    @Path("")
    public Result home(final MyUserRepresentation user, final MyCookieSecurity security) {
        final Map<String, Object> dataModel = new HashMap<>();
        final Template template = this.getTemplate("home.ftl");

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        final LocalDateTime now = LocalDateTime.now();
        dataModel.put("currentDate", dtf.format(now));
        dataModel.put("user", user);

        return this.ok(template, dataModel);
    }

    /**
     * Login result.
     * <p>
     * Example : http://localhost:8080/login?name=Pierre&amp;scope=web&amp;scope=abc
     * </p>
     *
     * @param user     the user
     * @param security the security
     * @param name     the name
     * @param scopes   the scopes
     * @return the result
     */
    @GET
    @Path("/login")
    public Result login(final MyUserRepresentation user,
                        final MyCookieSecurity security,
                        @Query(value = "name", defaultValue = @DefaultValue(value = "John Dummer")) final String name,
                        @Query(value = "scope",
                                listProperty = @QueryListProperty(backedType = String.class),
                                defaultValue = @DefaultValue(value = "web")) final List<String> scopes) {
        if (user == null) {
            final MyUserRepresentation newUser = new MyUserRepresentation(name, scopes);
            return this.redirect("/")
                    .withCookie(security.newCookie(newUser));
        } else {
            return this.ok("ALREADY LOGGED IN !");
        }
    }

    /**
     * Logout result.
     * <p>
     * Example : http://localhost:8080/logout
     *
     * @return the result
     */
    @GET
    @Path("/logout")
    public Result logout() {
        return this.redirect("/").dropCookies();
    }

    /**
     * Secured page result.
     *
     * @param user the user
     * @return the result
     */
    @GET
    @Secured
    @MySecurityScope("web")
    @Path("/secured")
    public Result securedPage(final MyUserRepresentation user) {
        final Map<String, Object> dataModel = new HashMap<>();
        final Template template = this.getTemplate("secured-page.ftl");

        dataModel.put("user", user);

        return this.ok(template, dataModel);
    }

    /**
     * Secured page result.
     *
     * @param user the user
     * @return the result
     */
    @GET
    @Path("/long-content")
    public Result longContent(final MyUserRepresentation user) {
        return this.ok(this.getTemplate("long-content.ftl"), null);
    }

    /**
     * Secured page result.
     *
     * @param user the user
     * @return the result
     */
    @GET
    @Path("/image-asset-resources")
    public Result imageAssetResources(final MyUserRepresentation user) {
        return this.ok(this.getTemplate("image-asset-resources.ftl"), null);
    }

    /**
     * Exception result.
     *
     * @param user the user
     * @return the result
     */
    @GET
    @Path("/exception")
    public Result exception(final MyUserRepresentation user) {
        if (true) {
            throw new RuntimeException("Sample Exception");
        }
        return this.ok("");
    }

    /**
     * Has query param result.
     *
     * @param download the download
     * @return the result
     */
    @GET
    @Path(value = "/hasQueryParam")
    public Result hasQueryParam(@Query(value = "download", required = false) final String download) {
        if (download == null) {
            return this.ok("download was not on the query parameters");
        } else {
            return this.ok("download was on the query parameters with the value \"" + download + "\"");
        }
    }

    /**
     * Post home.
     *
     * @return the result
     */
    @POST
    @Path("")
    public Result postHome() {
        return this.ok("POST from Underflow");
    }

    /**
     * Put home.
     *
     * @return the result
     */
    @PUT
    @Path("")
    public Result putHome() {
        return this.ok("PUT from Underflow");
    }

    /**
     * Patch home.
     *
     * @return the result
     */
    @PATCH
    @Path("")
    public Result patchHome() {
        return this.ok("PATCH from Underflow");
    }

    /**
     * Option home.
     *
     * @return the result
     */
    @OPTIONS
    @Path("")
    public Result optionHome() {
        return this.ok("OPTIONS from Underflow");
    }

    /**
     * Delete home.
     *
     * @return the result
     */
    @DELETE
    @Path("")
    public Result deleteHome() {
        return this.ok("DELETE from Underflow");
    }

    /**
     * Delete home.
     *
     * @return the result
     */
    @HEAD
    @Path("")
    public Result headHome() {
        return this.ok("HEAD from Underflow");
    }

    /**
     * Simple GET example.
     *
     * @param exchange the exchange
     * @return the result
     * @throws Exception the exception
     */
    @GET
    @Path("/status")
    @Path("/statusBis")
    public Result status(final HttpServerExchange exchange) throws

            Exception {
        return this.ok("Status : ", new IoCallback() {
            @Override
            public void onComplete(final HttpServerExchange exchange, final Sender sender) {
                sender.send("OK !", IoCallback.END_EXCHANGE);
            }

            @Override
            public void onException(final HttpServerExchange exchange, final Sender sender, final IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
}
