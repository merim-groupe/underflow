package com.merimdigitalmedia.underflow.tests.sample;

import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.DefaultValue;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.annotation.routing.Query;
import com.merimdigitalmedia.underflow.annotation.routing.QueryListProperty;
import com.merimdigitalmedia.underflow.annotation.security.Secured;
import com.merimdigitalmedia.underflow.forms.WebForm;
import com.merimdigitalmedia.underflow.handlers.flows.FlowTemplateHandler;
import com.merimdigitalmedia.underflow.results.Result;
import com.merimdigitalmedia.underflow.tests.sample.security.MyCookieSecurity;
import com.merimdigitalmedia.underflow.tests.sample.security.MySecurityScope;
import com.merimdigitalmedia.underflow.tests.sample.security.MyUserRepresentation;
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
     * Example : http://localhost:8080/login?name=Pierre&scope=web&scope=abc
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
     * @throws Exception the exception
     */
//    @ALL
//    @Path("/app")
//    public void foo(final HttpServerExchange exchange) throws Exception {
//        new SubTestHandler().handleRequest(exchange);
//    }

    /**
     * Simple GET example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
//    @ALL
//    @Path("/app1")
//    @Path("/app2")
//    public void bar(final HttpServerExchange exchange) throws Exception {
//        new SubTestHandler().handleRequest(exchange);
//    }

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

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
//    @POST
//    @Path("/webform")
//    public void webForm(final HttpServerExchange exchange) throws Exception {
//        this.dispatchAndBlock(exchange, () -> {
//            this.getForm(exchange, TestForm.class, form -> {
//                this.logger.error("GOT : {}", form.getName());
//            }, exception -> {
//                this.logger.error("OH NO ! ... Anyway ...", exception);
//            });
//        });
//    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @param state    the state
     * @throws Exception the exception
     */
//    @GET
//    @Path("/enum")
//    public void webForm(final HttpServerExchange exchange,
//                        @Query(value = "state", required = true) final StateEnum state) throws Exception {
//        this.dispatchAndBlock(exchange, () -> {
//            this.ok(exchange, sender -> {
//                sender.send("State is : " + state.name());
//            });
//        });
//    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @param states   the states
     * @throws Exception the exception
     */
//    @GET
//    @Path("/enums")
//    public void enums(final HttpServerExchange exchange,
//                      @Query(value = "state",
//                              listProperty = @QueryListProperty(backedType = StateEnum.class)) final List<StateEnum> states) throws Exception {
//        this.dispatchAndBlock(exchange, () -> {
//            this.ok(exchange, sender -> {
//                final StringBuilder s = new StringBuilder("State are :");
//                for (final StateEnum state : states) {
//                    s.append("\n").append(state.name());
//                }
//                sender.send(s.toString());
//            });
//        });
//    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @param states   the states
     * @throws Exception the exception
     */
//    @GET
//    @Path("/enums-default")
//    public void enumsDefault(final HttpServerExchange exchange,
//                             @Query(value = "state",
//                                     listProperty = @QueryListProperty(backedType = StateEnum.class),
//                                     defaultValue = @DefaultValue({"DONE", "PENDING"})
//                             ) final List<StateEnum> states) throws Exception {
//        this.enums(exchange, states);
//    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
//    @GET
//    @Dispatch
//    @Fallback
//    public Result fallback(final HttpServerExchange exchange) throws Exception {
//        return this.ok("Fallback controller !");
//    }
}
