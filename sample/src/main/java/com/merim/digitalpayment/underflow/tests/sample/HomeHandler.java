package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.annotation.routing.PathIgnoreCase;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamList;
import com.merim.digitalpayment.underflow.handlers.flows.FlowTemplateHandler;
import com.merim.digitalpayment.underflow.i18n.I18n;
import com.merim.digitalpayment.underflow.i18n.cookie.I18nCookie;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.tests.sample.form.LoginForm;
import com.merim.digitalpayment.underflow.tests.sample.security.MyCookieSecurity;
import com.merim.digitalpayment.underflow.tests.sample.security.MySecurityScope;
import com.merim.digitalpayment.underflow.tests.sample.security.MyUserRepresentation;
import com.merim.digitalpayment.underflow.web.forms.WebForm;
import freemarker.template.Template;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * The Test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Path("/")
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
     * @param exchange   the exchange
     * @param langCookie the lang cookie
     * @param user       the optional user
     * @param security   the security
     * @param i18n       the 18 n
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("")
    // AppLanguage.Converter.class can be set at the application level using: Converters.addConverter(new AppLanguage.Converter());
    public Result home(@Context final HttpServerExchange exchange,
                       @CookieParam("UnderflowLang") final String langCookie, // Only for display purpose
                       @Context final MyUserRepresentation user,
                       @Context final MyCookieSecurity security,
                       @Context final I18n i18n) {
        final Map<String, Object> dataModel = new HashMap<>();
        final Template template = this.getTemplate("home.ftl");

        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        final LocalDateTime now = LocalDateTime.now();

        dataModel.put("messages", i18n.getLocalizedMessage(I18nCookie.resolveLocale(exchange)));
        dataModel.put("langCookie", langCookie);
        dataModel.put("currentDate", dtf.format(now));
        dataModel.put("user", user);
        dataModel.put("foo", "foo");

        return this.ok(template, dataModel);
    }

    /**
     * Sets language.
     *
     * @param language the language
     * @return the language
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_PLAIN)
    @PathIgnoreCase
    @GET
    @Path("/lang")
    public Result setLanguage(@QueryParam("lang") final String language) {
        if (language == null) {
            return this.redirect("/")
                    .deleteCookie(I18nCookie.getCookieName());
        } else {
            final Locale locale = I18nCookie.resolveFromAllowedLocalesOrDefault(Locale.forLanguageTag(language));

            return this.redirect("/")
                    .withCookie(I18nCookie.createCookie(locale));
        }
    }

    /**
     * String answer result.
     *
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_PLAIN)
    @PathIgnoreCase
    @GET
    @Path("/test-text")
    public Result stringAnswer() {
        return this.ok("OK");
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
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    @Path("/login")
    public Result login(@Context final MyUserRepresentation user,
                        @Context final MyCookieSecurity security,
                        @QueryParam("name") @DefaultValue("John Dummer") final String name,
                        @QueryParam("scope") @QueryParamList(String.class) @DefaultValue("web") final List<String> scopes) {
        if (user == null) {
            final MyUserRepresentation newUser = new MyUserRepresentation(name, scopes);
            return this.redirect("/")
                    .withCookie(security.newCookie(newUser));
        } else {
            return this.ok("ALREADY LOGGED IN !");
        }
    }

    /**
     * Login result.
     *
     * @param exchange the exchange
     * @param user     the user
     * @param security the security
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/login")
    public Result login(@Context final HttpServerExchange exchange,
                        @Context final MyUserRepresentation user,
                        @Context final MyCookieSecurity security) {
        final Function<LoginForm, Result> successLogic = loginForm -> {
            final MyUserRepresentation newUser = new MyUserRepresentation(loginForm.getName(), loginForm.getScopes());
            return this.redirect("/")
                    .withCookie(security.newCookie(newUser));
        };

        if (user == null) {
            if (this.hasFormData(exchange)) {
                return this.getForm(exchange, LoginForm.class, successLogic, e -> this.badRequest("Invalid form."));
            } else {
                return this.getJsonForm(exchange, LoginForm.class, successLogic);
            }
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
    @Operation(hidden = true)
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
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @MySecurityScope("web")
    @Path("/secured")
    public Result securedPage(@Context final MyUserRepresentation user) {
        final Map<String, Object> dataModel = new HashMap<>();
        final Template template = this.getTemplate("secured-page.ftl");

        dataModel.put("user", user);

        return this.ok(template, dataModel);
    }

    /**
     * Secured page result.
     *
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/long-content")
    public Result longContent() {
        return this.ok(this.getTemplate("long-content.ftl"), null);
    }

    /**
     * Secured page result.
     *
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/image-asset-resources")
    public Result imageAssetResources() {
        return this.ok(this.getTemplate("image-asset-resources.ftl"), null);
    }

    /**
     * Exception result.
     *
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/exception")
    public Result exception() {
        if (true) { // Java trickery ignore this.
            throw new RuntimeException("Sample Exception");
        }

        return this.ok("");
    }

    /**
     * Exception result.
     *
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/ftl-exception-1")
    public Result ftlException1() {
        return this.ok(this.getTemplate("bad-template.ftl"), new HashMap<String, Object>() {{
            this.put("key", null);
        }});
    }

    /**
     * Exception result.
     *
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/ftl-exception-2")
    public Result ftlException2() {
        return this.ok(this.getTemplate("throw-in-data-model.ftl"), new BadDataModel());
    }

    /**
     * Simple GET example.
     *
     * @return the result
     * @throws Exception the exception
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    @Path("/status")
    public Result status() throws Exception {
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

    /**
     * Stop result.
     *
     * @param underflowServer the underflow server
     * @return the result
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_PLAIN)
    @GET
    @Path("/stop")
    public Result stop(@Context final UnderflowServer underflowServer) {
        underflowServer.stop();

        return this.ok("Server is shutting down !");
    }

    /**
     * The type Bad data model.
     */
    public static class BadDataModel {

        /**
         * Gets name.
         *
         * @return the name
         */
        public String throwAnException() {
            throw new RuntimeException("Exception from DataModel");
        }
    }
}
