# Underflow

An undertow based framework making your life easier than it should be.

## Modules

| Module | Description |
|---|---|
| `underflow-core` | Core framework module providing the handler model, routing, result types, and all base abstractions the other modules build upon. |
| `underflow-server` | Wraps Undertow with a fluent `UnderflowServer` builder API for configuring host, port, CORS, logging, and graceful shutdown. |
| `underflow-context` | Application context management: `Application` singleton, MDC support, and DEV/PROD operating mode configuration. |
| `underflow-converters` | Standard type converters for path and query parameters (String, Integer, Long, Double, Boolean, UUID, BigDecimal, etc.). |
| `underflow-security` | Abstract security framework for authentication and authorization via headers, cookies, and flows; provides the `@Secured` annotation. |
| `underflow-security-jwt` | Extends `underflow-security` with JWT cookie-based authentication using the JJWT library. |
| `underflow-openapi` | Integrates SmallRye OpenAPI to auto-generate API documentation and expose it at `/docs`. |
| `underflow-openapi-api` | Lightweight module that provides MicroProfile OpenAPI annotations without pulling in the full OpenAPI implementation. |
| `underflow-template` | FreeMarker template engine integration for server-side HTML rendering. |
| `underflow-i18n` | Internationalization support: properties-file or map-based message sources, locale detection via cookies, and message formatting. |
| `underflow-api-form` | Standardized request/response abstractions (`ApiForm`, `ApiFormWithPayload`, `ApiServerError`) for form-based endpoints. |
| `underflow-utils` | Utility helpers including compression (`SmartGZipBodyInput`), map utilities, and version management. |
| `underflow-test` | JUnit 5 extension and REST-assured utilities for integration-testing Underflow applications. |
| `underflow-bom` | Bill of Materials — import this in your project to align dependency versions across all Underflow modules. |

## Getting started

> **Jackson compatibility:** Underflow requires **Jackson 3.0 or later**. It has been tested with Jackson 3.2.

### 1. Import the BOMs

Add `underflow-bom` and `jackson-bom` to your `<dependencyManagement>` so all module versions are aligned automatically:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.merim.digitalpayment.underflow</groupId>
            <artifactId>underflow-bom</artifactId>
            <version>${underflow.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>tools.jackson</groupId>
            <artifactId>jackson-bom</artifactId>
            <version>${jackson.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. Add the mandatory dependencies

`underflow-core` and `underflow-server` are required for any project. `undertow-core` is the underlying HTTP server — its version is managed by `underflow-bom` so you can omit it or pin a specific version yourself.

```xml
<dependencies>
    <!-- Underflow -->
    <dependency>
        <groupId>com.merim.digitalpayment.underflow</groupId>
        <artifactId>underflow-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.merim.digitalpayment.underflow</groupId>
        <artifactId>underflow-server</artifactId>
    </dependency>

    <!-- Undertow (version managed by underflow-bom) -->
    <dependency>
        <groupId>io.undertow</groupId>
        <artifactId>undertow-core</artifactId>
    </dependency>

    <!-- Jackson -->
    <dependency>
        <groupId>tools.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
    </dependency>
    <dependency>
        <groupId>tools.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### 3. OpenAPI support (optional)

If you want to expose an OpenAPI/Swagger endpoint, add `underflow-openapi` along with the YAML serializer for Jackson:

```xml
<dependencies>
    <dependency>
        <groupId>com.merim.digitalpayment.underflow</groupId>
        <artifactId>underflow-openapi</artifactId>
    </dependency>

    <!-- Required by SmallRye OpenAPI to serialize the spec as YAML -->
    <dependency>
        <groupId>tools.jackson.dataformat</groupId>
        <artifactId>jackson-dataformat-yaml</artifactId>
    </dependency>
</dependencies>
```

SmallRye OpenAPI also requires a Jandex index to scan your classes for annotations at build time. Add the Jandex Maven plugin to your `<build>`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.smallrye</groupId>
            <artifactId>jandex-maven-plugin</artifactId>
            <version>${jandex.version}</version>
            <executions>
                <execution>
                    <id>make-index</id>
                    <goals>
                        <goal>jandex</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

The `jandex.version` property is managed by `underflow-bom`. If you import `underflow-bom` as a **parent** (not just a BOM import), the plugin version is also declared in `<pluginManagement>` and you can omit the `<version>` tag entirely. Underflow is tested with Jandex **3.6.0**. External projects importing the BOM via `<scope>import</scope>` must pin the version themselves.

## Quickstart

An Underflow application is a single class that implements `UnderflowApplication`. It declares the server (host, port, modules, handlers) and is launched through `UnderflowApplication.run`.

### The application class

```java
public class MyApp implements UnderflowApplication {

    public static void main(String[] args) {
        UnderflowApplication.run(MyApp.class, args);
    }

    @Override
    public void initialize(String[] args) {
        // Called once before the server is built.
        // Register global singletons here (I18n, database pools, etc.).
    }

    @Override
    public UnderflowServerBuilder createServerBuilder() {
        return UnderflowServer.builder("0.0.0.0", 8080)
                .addHandler(new HelloHandler());
    }

    @Override
    public void onServerCreated(UnderflowServer server) {
        // Optional — the server exists but is not started yet.
        Application.register(UnderflowServer.class, server);
    }

    @Override
    public void onServerStart(UnderflowServer server) {
        // Optional — called just before start.
        System.out.println("Listening on http://localhost:" + server.getPort());
    }
}
```

`UnderflowApplication.run` drives the full lifecycle: it detects the run mode, calls `initialize`, builds the server from your builder, fires `onServerCreated`/`onServerStart`, starts the server, and blocks until exit — then runs your shutdown hooks.

### A handler

A handler is a class annotated with `@Path`. Each public method becomes a route via the standard Jakarta REST annotations (`@GET`, `@POST`, `@PathParam`, `@QueryParam`, …). Extend the base class that matches what you return:

| Base class | Use for | Provides |
|---|---|---|
| `FlowHandler` | Plain text / low-level responses | `ok(String)`, `redirect(...)`, status helpers |
| `FlowApiHandler` | JSON REST APIs | `ok(JsonNode)`, `toJsonNode(...)`, JSON form helpers |
| `FlowTemplateHandler` | Server-rendered HTML | `ok(Template, dataModel)` + error pages |
| `FlowDTOWrapperTemplateHandler` | HTML with auto-injected common data | as above + DTOWrapper |

```java
@Path("/")
public class HelloHandler extends FlowApiHandler {

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Result hello(@QueryParam("name") @DefaultValue("World") String name) {
        return this.ok(this.toJsonNode(Map.of("message", "Hello " + name)));
    }
}
```

Handler methods may return a `Result` directly or a `CompletableFuture<Result>` for asynchronous processing. The framework resolves the future before writing the response.

### Routing and parameter injection

Routing uses the standard `jakarta.ws.rs` annotations:

- `@Path` — on the class (prefix) and on each method (route). Path variables: `@Path("/users/{id}")`
- `@GET` `@POST` `@PUT` `@DELETE` `@PATCH` — HTTP method
- `@PathParam("id")`, `@QueryParam("q")`, `@CookieParam("session")` — parameter sources
- `@DefaultValue("...")` — value used when the parameter is absent
- `@Produces` / `@Consumes` — media types
- `@Context` — inject framework objects (see below)

Underflow adds a few routing annotations of its own in `com.merim.digitalpayment.underflow.annotation.routing`:

- `@QueryParamList(String.class)` — bind a repeated/array query parameter to a `List` (`?scope=a&scope=b`, `?scope[]=a`, `?scope[0]=a`)
- `@Converter(MyConverter.class)` — apply a custom converter to one parameter (see [Converters](#converters))
- `@PathIgnoreCase` — case-insensitive path matching
- `@QueryParamRequired` — reject the request if the parameter is missing

Parameter values are converted from their string form to the declared Java type automatically (see [Converters](#converters)).

### What @Context can inject

Beyond your own registered types and security user representations, `@Context` resolves:

- `HttpServerExchange` — the raw Undertow exchange (request body, headers, etc.)
- `UnderflowServer` — the running server instance (e.g. to call `stop()`)
- your security class and user representation (see [Security](#security))

### Server builder options

`UnderflowServer.builder(host, port)` returns an `UnderflowServerBuilder`. Common calls:

```java
UnderflowServer.builder("0.0.0.0", 8080)
    .addHandler(new MyHandler())                              // register a handler
    .addHandler(new MyHandler(),
        UnderflowCORSOption.enable("https://example.com"),    // per-handler CORS
        UnderflowLoggerOption.LOG_ALL_QUERY)                  // per-handler request logging
    .addModule(new OpenApiServerModule(...))                  // pluggable modules (see OpenAPI)
    .addPreShutdownHook(() -> { /* before stop */ })
    .addShutdownHook(() -> { /* after stop */ })
    .setServerOption(UndertowOptions.ENABLE_HTTP2, true)      // raw Undertow options
    .setServerOption(UndertowOptions.MAX_ENTITY_SIZE, 10L * 1024 * 1024);
```

Handler options are passed as varargs **per handler**, so different handlers can have different CORS and logging policies. `UnderflowCORSOption` offers `enable()`, `enable(origin)`, and `enable(origin, methods, headers, credentials)`; `UnderflowCORSOption.enableEasyCORS()` is a permissive shortcut for development.

## Application context

The `underflow-context` module provides the `Application` class — a process-wide registry and a few global services used throughout the framework.

### Singleton registry

`Application` holds a typed map of singletons keyed by class. Register instances during `initialize`, then retrieve them anywhere (handlers, DTO wrapper builders, filters) without passing references around.

```java
// Register (usually in UnderflowApplication.initialize)
Application.register(I18n.class, myI18nInstance);

// Retrieve
I18n i18n = Application.getInstance(I18n.class);            // null if absent
Optional<I18n> opt = Application.getInstanceOptional(I18n.class);
```

### Shared ObjectMapper

`Application.getMapper()` returns the framework's shared Jackson `ObjectMapper` (a Jackson 3 `JsonMapper` built with auto-discovered modules via `findAndAddModules()`). The JSON form/body helpers use it by default, and you should reuse it rather than constructing your own:

```java
ObjectMapper mapper = Application.getMapper();
```

### Run mode

`Application` tracks a `Mode` — `DEV`, `PROD`, or `TEST`. `UnderflowApplication.run` initializes it automatically: `PROD` when running from a packaged JAR, `DEV` otherwise. The test extension forces `TEST`.

```java
if (Application.getMode() == Mode.DEV) {
    // verbose diagnostics, hot-reload, full error pages, ...
}
```

The framework already uses this internally — for example, `FlowTemplateHandler` renders a detailed stack-trace error page in `DEV` and a blank one in `PROD`. You can read it with `Application.getMode()` and override it with `Application.setMode(...)` if needed.

### MDC logging context

The module integrates with SLF4J's MDC (Mapped Diagnostic Context). On every request the framework populates MDC with request metadata so it can appear in your log pattern. Standardized keys live in `MDCKeys`:

- `MDCKeys.Request.UID` — a generated unique id per request
- `MDCKeys.Request.METHOD`, `.URL`, `.QUERY_STRING`, `.HOST_NAME`, `.HOST_PORT`, `.BODY`
- `MDCKeys.Connection.IO_THREAD`, `.PEER_ADDRESS`

Anything implementing `MDCContext` (handlers do) gets safe helpers:

```java
this.putMDC(MDCKeys.Request.UID, requestId);
Optional<String> uid = this.getMDC(MDCKeys.Request.UID);
```

Reference these keys in your logging configuration (e.g. Logback's `%X{request.uid}`) to correlate log lines per request.

## Security

Underflow's security system is built around three generic types you provide:

- **`T`** — your user representation (what a logged-in user looks like)
- **`U`** — your scope annotation (the custom annotation you place on protected methods)

The framework calls your code at two points: to resolve a user from an incoming request (`isLogged`), and to decide whether that user may access a given endpoint (`isAccessible`). Everything else — 401/403 responses, user injection into handler methods — is handled automatically.

### Step 1 — Define your user representation

Extend `JwtUserRepresentation<D>` where `D` is your custom data class. The `data` field is serialized as a JSON claim inside the JWT, so you can put anything you need in it.

```java
@NoArgsConstructor
@Getter
public class MyUserRepresentation extends JwtUserRepresentation<MyUserRepresentation.UserData> {

    public MyUserRepresentation(String name, List<String> roles) {
        this.setData(new UserData(name, roles));
        this.setSubject(name);
        this.setAudience(Set.of(MySecurity.ISSUER));
        this.setJwtId(UUID.randomUUID().toString());
        this.setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))));
    }

    @Getter
    @Setter
    public static class UserData {
        private String name;
        private List<String> roles;

        @JsonCreator
        public UserData(
                @JsonProperty(value = "name", required = true) String name,
                @JsonProperty(value = "roles", required = true) List<String> roles) {
            this.name = name;
            this.roles = roles;
        }
    }
}
```

### Step 2 — Define your scope annotation

Create an annotation meta-annotated with `@Secured`. This is what you will place on handler methods that require authentication. You can add as many attributes as you like — they are passed directly to your `isAccessible` method, so you can encode any authorization data you need (role name, permission level, feature flag, etc.).

```java
@Secured                          // tells the framework this annotation requires authentication
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MySecurityScope {
    String value();               // the required role, e.g. "admin" or "read:orders"
}
```

The `@Secured` meta-annotation is the only contract the framework requires. Everything else on the annotation is yours to define.

### Step 3 — Implement your security class

Extend `JwtCookieSecurity<T, U>` and pass both type tokens to the constructor. You must implement one method: `isAccessible`, which receives the resolved user and the annotation instance from the method being called — giving you direct access to all annotation attributes.

```java
public class MySecurity extends JwtCookieSecurity<MyUserRepresentation, MySecurityScope> {

    public static final String ISSUER = "my-app";
    public static final String SECRET_KEY = "your-256-bit-secret-here!!!!!!!!!";

    public MySecurity() {
        super(
            MyUserRepresentation.class,
            MySecurityScope.class,
            "MySession",            // cookie name
            ISSUER,
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)),
            Application.getMapper()
        );
    }

    @Override
    public boolean isAccessible(MyUserRepresentation user, MySecurityScope scope) {
        if (scope == null) {
            return true;            // method has @Secured but not @MySecurityScope — allow all logged-in users
        }
        return user.getData().getRoles().contains(scope.value());
    }
}
```

When `isAccessible` is called, `scope` is the actual annotation instance from the handler method, so `scope.value()` returns exactly what was written at the call site (e.g. `"admin"`). If the method only has `@Secured` and not `@MySecurityScope`, `scope` will be `null`.

You can optionally override `updateUser` to refresh the JWT on active sessions:

```java
@Override
protected Optional<MyUserRepresentation> updateUser(MyUserRepresentation user) {
    // Called on every request with a valid session.
    // Return Optional.of(user) with a new expiration to refresh the cookie,
    // or Optional.empty() to leave it unchanged.
    if (Duration.between(user.getIssuedAt().toInstant(), Instant.now()).toMinutes() >= 2) {
        user.setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))));
        return Optional.of(user);
    }
    return Optional.empty();
}
```

### Step 4 — Protect your endpoints

Annotate handler methods with your scope annotation. The framework resolves the logged-in user and injects it as a `@Context` parameter — it will be `null` if the endpoint is not secured and no session exists.

```java
@GET
@Path("/admin")
@MySecurityScope("admin")          // requires a valid session with the "admin" role
public Result adminPage(@Context MyUserRepresentation user) {
    // user is guaranteed non-null here
    return ok("Hello " + user.getData().getName());
}

@GET
@Path("/profile")
@Secured                           // requires any valid session, no specific role check
public Result profile(@Context MyUserRepresentation user) {
    return ok(user.getData());
}

@GET
@Path("/public")
public Result publicPage(@Context MyUserRepresentation user) {
    // user may be null if not logged in
    return ok("Welcome " + (user != null ? user.getData().getName() : "guest"));
}
```

### Step 5 — Issue a session on login

Call `security.newCookie(userRepresentation)` to create the signed JWT cookie and attach it to your response:

```java
@GET
@Path("/login")
public Result login(@Context MySecurity security, @QueryParam("name") String name) {
    MyUserRepresentation user = new MyUserRepresentation(name, List.of("admin"));
    return redirect("/").withCookie(security.newCookie(user));
}
```

### How the framework uses your annotation

When a request arrives the framework:

1. Calls `isLogged(exchange)` — your security class extracts the cookie, parses and validates the JWT, and returns an `Optional<T>`
2. Checks whether the target method has any annotation meta-annotated with `@Secured`
3. If secured and no user → **401 Unauthorized**
4. If secured and user present → calls `isAccessible(user, scopeAnnotation)` with the annotation instance
5. If `isAccessible` returns `false` → **403 Forbidden**
6. Otherwise → executes the method with the user injected via `@Context`

Because your `isAccessible` implementation receives the full annotation object, you can model any authorization scheme — simple role strings, bitmask permissions, multi-attribute rules — just by adding more attributes to your annotation.

## Internationalization (i18n)

The `underflow-i18n` module provides locale-aware message resolution. There are no special annotations or base classes required — you configure an `I18n` instance at startup, register it globally, and retrieve messages wherever you need them.

### Step 1 — Configure message sources

Messages are loaded from `.properties` files or plain `Map<String, String>` objects, one per locale. Use the builder for each source type and add all the locales you support:

```java
// From classpath resources
I18n i18n = new I18n()
    .addI18nSource(PropertiesSource.builder()
        .addLocale(Locale.ENGLISH,
            PropertiesSource.loadPropertiesFromResource(Main.class, "messages.en.properties").orElseThrow())
        .addLocale(Locale.FRENCH,
            PropertiesSource.loadPropertiesFromResource(Main.class, "messages.fr.properties").orElseThrow())
        .build()
    );
```

```java
// From in-memory maps
I18n i18n = new I18n()
    .addI18nSource(MapSource.builder()
        .addLocale(Locale.ENGLISH, Map.of(
            "greeting", "Hello {0}!",
            "farewell", "Goodbye {name}!"
        ))
        .addLocale(Locale.FRENCH, Map.of(
            "greeting", "Bonjour {0} !",
            "farewell", "Au revoir {name} !"
        ))
        .build()
    );
```

Multiple sources can be added to the same `I18n` instance. They are checked in the order they were added — the first source that contains a key wins.

Register the instance so it is accessible anywhere in your application:

```java
Application.register(I18n.class, i18n);
```

### Step 2 — Configure locale detection

`I18nCookie` handles locale resolution from HTTP requests. Configure it once at startup alongside your message sources:

```java
I18nCookie.setDefaultLocale(Locale.ENGLISH); // fallback when no locale can be determined
I18nCookie.setCookieName("lang");             // cookie name to read/write (default: "lang")
```

Locale resolution priority on each request:
1. The locale cookie (e.g. `lang=fr`)
2. The `Accept-Language` request header (parsed with quality weights)
3. The configured default locale

`addLocale()` on the source builders automatically registers each locale as allowed. Only allowed locales are matched — unknown locales fall back to the closest match (same language, different country) or the default.

### Step 3 — Resolve the locale per request

Call `I18nCookie.resolveAndSetCookie(exchange)` at the start of a request to get the locale for that request. It also writes the locale cookie back to the response if it was not already set.

```java
Locale locale = I18nCookie.resolveAndSetCookie(exchange);
```

If you only need the locale without touching the response cookie:

```java
Locale locale = I18nCookie.resolveLocale(exchange);
```

### Step 4 — Retrieve messages

Retrieve the `I18n` instance from the registry and call `get`:

```java
I18n i18n = Application.get(I18n.class);

// Plain key lookup
String msg = i18n.get(locale, "greeting");

// Positional arguments — uses java.text.MessageFormat syntax
String msg = i18n.get(locale, "greeting", "Alice");  // "Hello Alice!"

// Named arguments
String msg = i18n.get(locale, "farewell", Map.of("name", "Alice"));  // "Goodbye Alice!"

// Safe fallbacks
Optional<String> msg = i18n.getOptional(locale, "unknown.key");
String msg = i18n.getOrDefault(locale, "unknown.key", "default text");
```

For handlers that need to look up many messages for the same request, bind the locale once with `getLocalizedMessage` to get a `LocalizedMessage` wrapper that omits the locale parameter on every call:

```java
LocalizedMessage messages = i18n.getLocalizedMessage(locale);

String title   = messages.get("page.title");
String heading = messages.get("page.heading", "Alice");
```

### Step 5 — Let users switch language

A typical language-switch endpoint reads a query parameter, validates the locale against allowed values, and sets the cookie:

```java
@GET
@Path("/lang")
public Result setLanguage(@QueryParam("lang") String lang) {
    if (lang == null) {
        return redirect("/").deleteCookie(I18nCookie.getCookieName());
    }
    Locale locale = I18nCookie.resolveFromAllowedLocalesOrDefault(Locale.forLanguageTag(lang));
    return redirect("/").withCookie(I18nCookie.createCookie(locale));
}
```

### Hot-reloading (development)

Wrap any source in `ReloadableSource` to have it reloaded from disk at a given interval without restarting the server:

```java
I18n i18n = new I18n()
    .addI18nSource(ReloadableSource.wrap(
        () -> PropertiesSource.builder()
            .addLocale(Locale.ENGLISH, PropertiesSource.fromFile("messages.en.properties"))
            .build(),
        3000  // reload interval in milliseconds
    ));
```

### Message format syntax

Both positional and named placeholders are supported in the same message file:

```properties
# Positional — java.text.MessageFormat
welcome=Welcome, {0}! You have {1} messages.

# Named
order.summary=Order {orderId} placed on {date,date,short} for {amount,number,currency}.

# Escaping — single quotes prevent expansion
literal=The placeholder '{0}' will not be replaced.
```

## Templates

The `underflow-template` module integrates FreeMarker for server-side HTML rendering. It adds a handler base class that manages a `TemplateEngine` and provides `ok(template, dataModel)` and similar helper methods for every HTTP status code.

> **Note:** FreeMarker does not work correctly with Java Records. Use regular classes with getters (or Lombok `@Getter`) for data models and DTOWrappers.

### Step 1 — Initialize a TemplateEngine

`TemplateEngine` wraps a FreeMarker `Configuration`. You rarely instantiate it directly — the handler constructors accept the same arguments and create it for you. There are two loading strategies:

**From classpath resources** (recommended for packaged JARs):

```java
// Templates are loaded relative to the given class, inside the given resource folder.
// A path of "/templates" means src/main/resources/templates/ in your module.
new TemplateEngine(MyHandler.class, "/templates");
```

**From a filesystem directory** (useful for hot-reload during development):

```java
new TemplateEngine(new File("src/main/resources/templates"));
```

Once created, retrieve a FreeMarker `Template` object by name:

```java
Template home = templateEngine.getTemplate("home.ftl");
```

If you need access to the underlying FreeMarker `Configuration` to tune caching, locale, or other settings, call `templateEngine.getConfiguration()`.

### Step 2 — Extend FlowTemplateHandler

Instead of using `TemplateEngine` directly, extend `FlowTemplateHandler`. It wires up the engine, provides `getTemplate(path)`, and automatically handles error pages (404, 403, 500) with built-in FreeMarker templates.

```java
@Path("/")
public class HomeHandler extends FlowTemplateHandler {

    public HomeHandler() {
        super("/templates");          // classpath resource path
        // or: super("/templates", mySecurity);  // with security
        // or: super(new File("..."));            // from filesystem
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Result home() {
        Template template = this.getTemplate("home.ftl");
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("title", "Welcome");
        return this.ok(template, dataModel);
    }
}
```

The `dataModel` object is passed directly to FreeMarker. Any POJO with public getters works. In `home.ftl` you would access it as `${title}`.

### Step 3 — Override error pages (optional)

`FlowTemplateHandler` renders its own built-in templates for 404, 403, and 500 errors. In DEV mode the 500 page shows the full stack trace; in PROD mode it shows a blank error page.

To use your own error templates, override `TemplateSystem.getStandardErrorsTemplates()` with your own `TemplateEngine`:

```java
TemplateSystem.getInstance().setStandardErrorsTemplates(
    new TemplateEngine(MyApp.class, "/my-error-templates")
);
```

Your error template directory must contain `error404.ftl`, `error403.ftl`, and `error500.ftl`.

### Step 4 — Automatic data injection with DTOWrapper

Repeating the same boilerplate in every handler method (resolving locale, injecting the current URL, adding common model data) gets tedious. The `DTOWrapper` pattern solves this: you define a wrapper class that holds your per-request common data, and a builder that constructs it automatically before every template render.

**Define your wrapper** — a plain POJO that holds whatever every template needs:

```java
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyDTOWrapper<T> {
    private Object data;          // the per-handler data model
    private String currentUrl;
    private LocalizedMessage messages;
    // add anything else every template should have: user, app version, feature flags...
}
```

**Define your builder** — implement `DTOWrapperBuilder`. The `build` method is called automatically before every template render. It receives the raw exchange, the result being built, and the `dataModel` that was passed to `ok(template, dataModel)`. It must return the object that FreeMarker will actually receive.

```java
public class MyDTOWrapperBuilder implements DTOWrapperBuilder {

    private final I18n i18n;

    public MyDTOWrapperBuilder() {
        this.i18n = Application.getInstance(I18n.class);
    }

    @Override
    public Object build(HttpServerExchange exchange, HttpResult result, Object dataModel) {
        // Resolve locale and set the cookie on the response in one call
        Locale locale = I18nCookie.resolveAndSetCookie(exchange, result::withCookie);

        return new MyDTOWrapper<>(
            dataModel,
            exchange.getRequestURL(),
            this.i18n.getLocalizedMessage(locale)
        );
    }
}
```

Note that `result::withCookie` is passed to `resolveAndSetCookie` so the locale cookie is attached to the response automatically, without the handler needing to do anything.

**Use FlowDTOWrapperTemplateHandler** — extend this instead of `FlowTemplateHandler`. Pass your builder to the constructor. Every call to `ok(template, dataModel)` will transparently run your builder and give FreeMarker the wrapper instead of the raw data model:

```java
@Path("/")
public class HomeHandler extends FlowDTOWrapperTemplateHandler {

    public HomeHandler() {
        super("/templates", new MySecurity(), new MyDTOWrapperBuilder());
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public Result home(@Context MyUserRepresentation user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("currentDate", LocalDateTime.now().toString());

        // The builder wraps `data` into MyDTOWrapper before FreeMarker sees it.
        // In the template: ${messages.get("home.title")}, ${currentUrl}, ${data.user}
        return this.ok(this.getTemplate("home.ftl"), data);
    }
}
```

In your FreeMarker templates the root object is `MyDTOWrapper`, so you access data as:

```ftl
<title>${messages.get("page.title")}</title>
<p>You are at: ${currentUrl}</p>
<#if data.user??>
    <p>Hello ${data.user.name}!</p>
</#if>
```

The handler passes per-request data as usual; the builder adds the cross-cutting concerns. Neither has to know about the other.

## Converters

Converters translate the raw string value of a `@QueryParam`, `@PathParam`, or `@CookieParam` into a typed Java object. The framework ships with converters for the common types (`String`, `Integer`, `Long`, `Double`, `Float`, `Boolean`, `BigDecimal`, `BigInteger`, `UUID`). Enums are handled automatically by name matching without a converter.

When you need to accept a custom type as a parameter you implement `IConverter<T>`.

### Implementing a converter

```java
public class MyComplexObject {
    public String left;
    public String right;

    public static class Converter implements IConverter<MyComplexObject> {

        @Override
        public MyComplexObject bind(String representation) {
            // Called when parsing the raw string from the request
            MyComplexObject obj = new MyComplexObject();
            String[] parts = representation.split(":");
            obj.left  = parts[0];
            obj.right = parts.length > 1 ? parts[1] : "";
            return obj;
        }

        @Override
        public String unbind(MyComplexObject object) {
            // Called when serializing back to a string (e.g. for URL building)
            return object.left + ":" + object.right;
        }

        @Override
        public Class<MyComplexObject> getBackedType() {
            return MyComplexObject.class;
        }

        @Override
        public String getSyntax() {
            // Optional — regex used for path matching. Default is [^/]+
            return "[^/]+";
        }
    }
}
```

`bind` is the only method that must contain real logic. `unbind` and `getSyntax` can be left as defaults unless you need URL serialization or a tighter path-segment regex.

### Registering globally

Call `Converters.addConverter()` once at startup. Every parameter of that type across all handlers will use it automatically — no annotation required at the call site:

```java
// In your application startup
Converters.addConverter(new MyComplexObject.Converter());
```

After this, any handler method with a parameter of type `MyComplexObject` will be converted transparently:

```java
@GET
@Path("/items")
public Result getItem(@QueryParam("data") MyComplexObject obj) {
    // obj is already parsed — no annotation needed beyond @QueryParam
}
```

### Using locally with @Converter

When a converter is only relevant for one or a few parameters — or when the same type needs different parsing in different contexts — annotate the parameter directly with `@Converter`. No global registration is needed; the framework instantiates the converter on demand and caches it:

```java
@GET
@Path("/query-converter")
public Result queryExample(
        @QueryParam("data")
        @DefaultValue("123:456")
        @Converter(MyComplexObject.Converter.class)
        MyComplexObject obj) {
    return this.ok(obj.left + " / " + obj.right);
}

@GET
@Path("/path-converter/{data}")
public Result pathExample(
        @PathParam("data")
        @Converter(MyComplexObject.Converter.class)
        MyComplexObject obj) {
    return this.ok(obj.left + " / " + obj.right);
}
```

`@Converter` works on both `@QueryParam` and `@PathParam`. The converter class must have a no-argument constructor, or it must have been pre-registered via `Converters.registerRuntimeConverter(instance)` before the first request arrives.

### Global vs local — when to use which

| | Global (`Converters.addConverter`) | Local (`@Converter`) |
|---|---|---|
| Registration | Once at startup | Per parameter, no startup call needed |
| Syntax | Used for path-segment matching | Used for path-segment matching |
| Best for | Types used across many handlers | One-off or context-specific parsing |

## Forms

Underflow provides two distinct form handling mechanisms that can coexist in the same handler:

- **Multipart / URL-encoded forms** — submitted by HTML `<form>` elements, handled via `WebForm` + `Form`
- **JSON forms** — submitted as a JSON body, handled via `FlowApiHandler.getJsonForm()`

Both paths share the same validation contract through `ApiForm`.

### Defining a form class

A form class is a plain POJO with getters/setters. It implements one or both of the following interfaces depending on how it will be submitted:

- `Form` — for multipart/URL-encoded parsing (implements `accept(exchange, formData)`)
- `ApiForm` — for validation (implements `isValid()`, used by both multipart and JSON paths)

A class that implements both can be used for either submission format:

```java
public class LoginForm implements Form, ApiForm {

    private String name;
    private List<String> scopes;

    public LoginForm() {
        this.scopes = new ArrayList<>();
    }

    // Getters and setters ...

    @Override
    public void accept(HttpServerExchange exchange, FormData formData) throws Exception {
        if (formData.contains("name")) {
            this.name = formData.getFirst("name").getValue();
        }
        // multi-value field — browsers may send it as "scope" or "scope[]"
        for (String key : List.of("scope", "scope[]")) {
            if (formData.contains(key)) {
                formData.get(key).stream()
                    .map(FormData.FormValue::getValue)
                    .filter(v -> v != null && !v.isEmpty())
                    .forEach(this.scopes::add);
            }
        }
    }

    @Override
    public List<FormError> isValid() {
        List<FormError> errors = new ArrayList<>();
        if (this.name == null || this.name.trim().isEmpty()) {
            errors.add(new FormError("name", "name is required"));
        }
        return errors.isEmpty() ? null : errors;
    }
}
```

`isValid()` must return `null` (or an empty list) when the form is valid, and a list of `FormError` objects otherwise. Each `FormError` carries a `field` name and a `message`.

### Handling a JSON form

Extend `FlowApiHandler` and call `getJsonForm`. The framework deserializes the body with Jackson, calls `isValid()`, and automatically returns a `400 Bad Request` with the list of errors if validation fails. Your logic lambda is only called when the form is valid.

```java
@POST
@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
public Result login(@Context HttpServerExchange exchange) {
    return this.getJsonForm(exchange, LoginForm.class, form -> {
        // Only reached when isValid() returned null
        MyUserRepresentation user = new MyUserRepresentation(form.getName(), form.getScopes());
        return this.ok(user);
    });
}
```

When validation fails the response body is a JSON `ServerFormError`:

```json
{
  "type": "Bad Request",
  "formErrors": [
    { "field": "name", "message": "name is required" }
  ]
}
```

### Handling a multipart / URL-encoded form

Add `implements WebForm` to your handler and call `getForm`. The framework instantiates your form class, calls `accept(exchange, formData)` to populate it, and then passes it to your success lambda. Parse and validate inside `accept`, throwing an exception to trigger the error lambda:

```java
@POST
@Path("/login")
@Consumes(MediaType.MULTIPART_FORM_DATA)
public Result loginMultipart(@Context HttpServerExchange exchange) {
    return this.getForm(exchange, LoginForm.class,
        form -> {
            // Only reached when accept() did not throw
            MyUserRepresentation user = new MyUserRepresentation(form.getName(), form.getScopes());
            return this.redirect("/");
        },
        error -> this.badRequest("Invalid form data.")
    );
}
```

### Accepting both formats in the same endpoint

A common pattern is to check whether the request is a multipart form or a JSON body and dispatch accordingly:

```java
@POST
@Path("/login")
public Result login(@Context HttpServerExchange exchange) {
    if (this.hasFormData(exchange)) {
        return this.getForm(exchange, LoginForm.class,
            form -> this.redirect("/").withCookie(security.newCookie(new MyUserRepresentation(form.getName(), form.getScopes()))),
            error -> this.badRequest("Invalid form.")
        );
    } else {
        return this.getJsonForm(exchange, LoginForm.class,
            form -> this.redirect("/").withCookie(security.newCookie(new MyUserRepresentation(form.getName(), form.getScopes())))
        );
    }
}
```

`hasFormData(exchange)` returns `true` when the `Content-Type` is `multipart/form-data` or `application/x-www-form-urlencoded`.

### Validation with external context (ApiFormWithPayload)

Sometimes validation depends on data that is not in the request body — for example, checking that an ID in the form actually exists in the database. Use `ApiFormWithPayload<T>` instead of `ApiForm`: `isValid` receives an extra payload object that you supply at call time.

```java
public class UpdateOrderForm implements ApiFormWithPayload<Order> {

    private String status;

    // Getters/setters ...

    @Override
    public List<FormError> isValid(Order existingOrder) {
        List<FormError> errors = new ArrayList<>();
        if (this.status == null) {
            errors.add(new FormError("status", "status is required"));
        }
        if (existingOrder == null) {
            errors.add(new FormError("id", "order not found"));
        }
        return errors.isEmpty() ? null : errors;
    }
}
```

```java
@POST
@Path("/orders/{id}")
public Result updateOrder(@PathParam("id") UUID id, @Context HttpServerExchange exchange) {
    Order existing = orderRepository.findById(id);
    return this.getJsonFormWithPayload(exchange, UpdateOrderForm.class, existing, form -> {
        // form is valid and existing order was found
        return this.ok(orderRepository.update(id, form.getStatus()));
    });
}
```

### Nested form validation

When a form contains a sub-object that is itself a form, use `withValidSubForm` from `ApiBodyBindable` (already inherited by `ApiForm` and `ApiFormWithPayload`). It runs the sub-form's `isValid()` and prefixes every returned field path with the given key:

```java
public class OrderForm implements ApiForm {

    private AddressForm shippingAddress;

    @Override
    public List<FormError> isValid() {
        List<FormError> errors = new ArrayList<>();
        errors.addAll(this.withValidSubForm("shippingAddress", this.shippingAddress));
        // errors will have fields like "shippingAddress.street", "shippingAddress.city"
        return errors.isEmpty() ? null : errors;
    }
}
```

## Utilities

`underflow-utils` contains three small utility classes used internally by the framework and available for application code.

### SmartGZipBodyInput

Reads a request body and transparently decompresses it if it is GZip-encoded, returning a plain `InputStream` either way. Useful when you need to read the raw body yourself rather than going through the form or JSON helpers.

```java
SmartGZipBodyInput body = new SmartGZipBodyInput(exchange);

// As a stream (can be read once)
try (InputStream in = body.getInputStream()) {
    String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
}

// As a byte array (reusable)
byte[] bytes = body.getBytes();
```

Detection is based on the GZip magic bytes (`0x1f 0x8b`) at the start of the stream — no `Content-Encoding` header is required. If the body is shorter than the 10-byte GZip header, it is returned as-is.

### UnderflowMapUtils

Provides a factory for a bounded LRU (Least Recently Used) cache backed by `LinkedHashMap`. Entries are evicted automatically once the map exceeds `maxEntries`.

```java
Map<String, MyValue> cache = UnderflowMapUtils.createLRUCacheMap(500);
```

The returned map is not thread-safe. Wrap it in `Collections.synchronizedMap` if accessed from multiple threads.

### UnderflowVersionUtils

Reads the `Implementation-Version` from a JAR manifest and exposes it as a static string. Call `loadVersion` once at startup with any class from your packaged JAR:

```java
// At startup
UnderflowVersionUtils.loadVersion(Main.class);

// Anywhere afterwards
String version = UnderflowVersionUtils.getVersion(); // e.g. "1.2.3"
```

If the version cannot be read from the manifest (e.g. when running from an IDE without a built JAR), `getVersion()` returns the configured default. The default is `"0.0"` unless overridden:

```java
UnderflowVersionUtils.setDefault("dev");
```

## OpenAPI documentation

The `underflow-openapi` module generates an OpenAPI specification from your handlers and serves an interactive documentation UI. (See [Getting started](#3--openapi-support-optional) for the required Maven dependencies and Jandex plugin.)

### Enabling the /docs endpoint

Add an `OpenApiServerModule` to your server builder. It registers the documentation handler under `/docs` automatically:

```java
@Override
public UnderflowServerBuilder createServerBuilder() {
    return UnderflowServer.builder("0.0.0.0", 8080)
            .addModule(new OpenApiServerModule(OpenApiUiFlavor.STOPLIGHT))
            .addHandler(new MyApiHandler());
}
```

The `OpenApiUiFlavor` enum selects which UI renders the spec: `SWAGGER_UI`, `REDOC`, `RAPIDOC`, `STOPLIGHT`, or `NONE` (spec only, no UI). Once running, the module exposes:

| Endpoint | Returns |
|---|---|
| `/docs/` | The selected UI |
| `/docs/openapi.yaml` | The spec as YAML |
| `/docs/openapi.json` | The spec as JSON |

In `DEV` mode the spec is rebuilt on each request; in `PROD` it is generated once and cached.

### Describing your API

The spec is built from **MicroProfile OpenAPI** annotations (provided by `underflow-openapi-api`). Annotate your application class and handlers:

```java
@OpenAPIDefinition(info = @Info(title = "My API", version = "1.0", description = "..."))
public class MyApp implements UnderflowApplication { /* ... */ }
```

```java
@Tag(name = "Orders")
@Path("/api/orders")
public class OrderHandler extends FlowApiHandler {

    @Operation(summary = "Get an order", description = "Returns the order for the given id.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = Order.class))),
        @APIResponse(responseCode = "404", description = "Not found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ServerError.class)))
    })
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result get(@PathParam("id") Integer id) { /* ... */ }
}
```

Useful annotations:

- `@Tag` — group operations in the UI
- `@Operation(summary, description)` — describe a route; `@Operation(hidden = true)` excludes it from the spec
- `@APIResponse` / `@APIResponses` — document response codes and schemas
- `@Schema` — describe a model field (`required`, `example`, `nullable`, `description`)
- `@RequestBodySchema(MyForm.class)` — declare the request body type. **Use this rather than `@RequestBody`.**

### Customizing the spec with filters

`OpenApiServerModule` accepts a varargs list of MicroProfile `OASFilter` instances that post-process the generated spec. Underflow provides extended filter interfaces:

- `ServerAwareOASFilter` — receives the `UnderflowServerImpl` via `register(...)`, e.g. to inject the runtime version
- `JandexAwareOASFilter` — receives the Jandex `IndexView`, allowing a filter to inspect method annotations (e.g. read a custom `@MySecurityScope` and document the required access level)

```java
.addModule(new OpenApiServerModule(
    OpenApiUiFlavor.STOPLIGHT,
    new StandardApiSecurityFilter(),          // injects 401/403 responses on secured operations
    new OpenApiAutoResolveVersionFilter()))   // sets the spec version from the JAR manifest
```

`SecurityResponseInjectorFilter` is a ready-made base class: extend it to automatically add standard `401`/`403` responses to every operation that carries a given security scheme.

## Testing

The `underflow-test` module starts your real application in a JUnit 5 test and lets you exercise it over HTTP with [REST-assured](https://rest-assured.io/).

### Setup

Create a thin test application class that points at your real `UnderflowApplication`:

```java
public class TestAppImpl extends UnderflowTestApplicationImpl<MyApp> {
    public TestAppImpl() {
        super(MyApp.class);
    }
}
```

Then annotate your test class with `@UnderflowTest`, passing that class. The extension starts the server on an automatically chosen free port, sets the mode to `TEST`, and configures REST-assured to point at it.

```java
@UnderflowTest(TestAppImpl.class)
public class MyApiTest {

    @Test
    public void helloReturnsOk() {
        given()
            .when()
            .get("/hello")
            .then()
            .statusCode(200)
            .body(containsString("Hello"));
    }
}
```

`@UnderflowTest` is meta-annotated with `@ExtendWith(UnderflowTestExtension.class)` — no separate `@ExtendWith` is needed. The server is started once before all tests in the class and stopped afterwards.

### Passing startup arguments

Use `@StartupArgs` to pass command-line arguments to your application's `initialize(args)`:

```java
@UnderflowTest(value = TestAppImpl.class, args = @StartupArgs({"-foo", "-bar"}))
public class MyApiTest { /* ... */ }
```

### Making requests

REST-assured is pre-configured with the dynamic port and `http://localhost` base URI, so tests just call paths relative to the server root:

```java
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

given()
    .contentType("application/json")
    .body("{\"name\":\"Alice\"}")
    .when()
    .post("/login")
    .then()
    .statusCode(200);
```

Because the application runs in `Mode.TEST`, `Application.getMode()` returns `Mode.TEST` inside both the app and the test — handy for test-only branches.

## Running underflow-sample

For testing purposes you may want to run the tests classes from a jar file.
To do so, you will need to package the project using the `copy-dependencies` profile,
then you will be able to run the sample from a jar.

```shell
mvn -P copy-dependencies package
cd underflow-sample
java -cp 'target/*' com.merim.digitalpayment.underflow.tests.sample.MainSample
```

## Rules on versioning

Underflow is versioned using X.Y.Z or X.Y.Z-ExTag followed by a potential extra tag.

- X represents the major version. This number indicate an API breaking change.
- Y represents major additions to the version but keeps the existing API functional or
  Deprecate a functionality by offering a new way of working ideally with backward compatibility.
- Z represents minor additions or fixes to a version no API should change here
  except in the case of something that was forgotten during the creation of the major version.

Example

```text
1.0.0 would be the first major version of the framework.
1.1.0 would add to 1.0.0 a new major functionality
1.1.1 would either fix or improve something on 1.1.0
```

An extra tag can be added for some extra information.
This extra tag is usually use for temporary version or specific version
that should not be considered as a final release.

Example

```text
2.0.0-beta1 would indicate a beta test version of the future 2.0.0 version.
2.0.0-rc1 would indicate a release candidate of the 2.0.0 version which should in theory be stable but still in testing
```

