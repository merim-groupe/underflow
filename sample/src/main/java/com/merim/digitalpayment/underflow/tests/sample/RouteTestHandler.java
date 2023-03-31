package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.annotation.method.GET;
import com.merim.digitalpayment.underflow.annotation.routing.*;
import com.merim.digitalpayment.underflow.converters.IConverter;
import com.merim.digitalpayment.underflow.handlers.flows.FlowTemplateHandler;
import com.merim.digitalpayment.underflow.results.Result;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * The Sub test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class RouteTestHandler extends FlowTemplateHandler {

    /**
     * Instantiates a new Route test handler.
     */
    public RouteTestHandler() {
        super("/templates");
    }

    /**
     * GET example with parameters in the query string.
     *
     * @param exchange the exchange
     * @param bar      the bar parameter from query string
     * @return the result
     */
    @GET
    @Path("/")
    public Result pathWithQuery(final HttpServerExchange exchange,
                                @Query(value = "bar", defaultValue = @DefaultValue(value = "default value")) final String bar) {
        return this.ok(this.getTemplate("routes/home.ftl"), null);
    }

    /**
     * GET example with parameters in the path.
     * http://localhost:8080/routes/{uuid}
     * http://localhost:8080/routes/{uuid}?arg={argValue}
     * http://localhost:8080/routes/630ce4d0-58e9-4a78-aae3-4f320304cbf0
     * http://localhost:8080/routes/630ce4d0-58e9-4a78-aae3-4f320304cbf0?arg=foobar
     *
     * @param uuid the uuid parameter from path
     * @param arg  the arg
     * @return the result
     */
    @GET
    @Path("/(?<uuid>[0-9a-f]{8}-(?>[0-9a-f]{4}-){3}[0-9a-f]{12})")
    public Result uuidInPath(
            @Named("uuid") final UUID uuid,
            @Query(value = "arg", required = false) final String arg) {
        return this.ok(this.getTemplate("routes/display-text.ftl"), new HashMap<String, Object>() {{
            this.put("text", String.format("You called with the UUID=[%s] and arg=[%s]", uuid.toString(), arg == null ? "" : arg));
        }});
    }

    /**
     * GET example with parameters list in query strings
     * <p>
     * http://localhost:8080/routes/list-query
     * http://localhost:8080/routes/list-query?entry=xxx
     * http://localhost:8080/routes/list-query?entry[]=xxx
     * http://localhost:8080/routes/list-query?entry=xxx&amp;entry=yyy
     * http://localhost:8080/routes/list-query?entry[]=xxx&amp;entry[]=yyy
     * http://localhost:8080/routes/list-query?entry[0]=xxx&amp;entry[1]=yyy
     * ...
     * </p>
     *
     * @param dataList the data list
     * @return the result
     */
    @GET
    @Path("/query-list")
    public Result uuidInPath(@Query(value = "entry", required = false,
            listProperty = @QueryListProperty(backedType = String.class),
            defaultValue = @DefaultValue("default value from controller !")) final List<String> dataList) {
        return this.ok(this.getTemplate("routes/display-list.ftl"), new HashMap<String, Object>() {{
            this.put("listData", dataList);
        }});
    }

    /**
     * GET example with custom converter
     * <p>
     * http://localhost:8080/routes/query-converter
     * http://localhost:8080/routes/query-converter?data=ab:cd
     * http://localhost:8080/routes/query-converter?data=qwe:rty:ignored
     * ...
     * </p>
     *
     * @param complexObject the complex object
     * @return the result
     */
    @GET
    @Path("/query-converter")
    public Result queryConveter(@Query(value = "data", required = true,
            defaultValue = @DefaultValue("123:456"),
            converter = @Converter(MyComplexObject.Converter.class)) final MyComplexObject complexObject) {
        return this.ok(String.format("%s === %s", complexObject.left, complexObject.right));
    }

    /**
     * GET example with custom converter
     * <p>
     * http://localhost:8080/routes/path-converter/ab:cd
     * http://localhost:8080/routes/path-converter/qwe:rty:ignored
     * ...
     * </p>
     *
     * @param complexObject the complex object
     * @return the result
     */
    @GET
    @Path("/path-converter/(?<data>.+)")
    public Result pathConverter(@Named(value = "data",
            converter = @Converter(MyComplexObject.Converter.class)) final MyComplexObject complexObject) {
        return this.ok(String.format("%s === %s", complexObject.left, complexObject.right));
    }

    /**
     * The type My complex object.
     */
    public static class MyComplexObject {

        /**
         * The Left.
         */
        String left;

        /**
         * The Right.
         */
        String right;

        /**
         * The type Converter.
         */
        public static class Converter implements IConverter<MyComplexObject> {

            @Override
            public MyComplexObject bind(final String representation) {
                final MyComplexObject myComplexObject = new MyComplexObject();
                final String[] split = representation.split(":");
                myComplexObject.left = split[0];
                myComplexObject.right = split.length > 1 ? split[1] : "";
                return myComplexObject;
            }

            @Override
            public String unbind(final MyComplexObject object) {
                return object.left + ":" + object.right;
            }

            @Override
            public Class<MyComplexObject> getBackedType() {
                return MyComplexObject.class;
            }
        }
    }
}
