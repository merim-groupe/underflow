<#import "/common/base-page.ftl" as base/>

<@base.page title="Home page" lang="fr">
    <h1>HELLO !</h1>
    <div>
        <p>
            <b>Date</b> ${currentDate}
        </p>
        <p>
            <b>Secured page</b> <a href="/secured">Here !</a>
        </p>
        <p>
            <b>API page</b> <a href="/api">Here !</a>
        </p>
    </div>
    <div>
        <h3>User data</h3>
        <#if user??>
            <a href="/logout">Logout</a>
            <p>
                Username : <span>${user.name}</span>
            </p>
            <p>
                Scopes :
            </p>
            <ul>
                <#list user.scopes as scope>
                    <li>${scope}</li>
                </#list>
            </ul>
        <#else>
            Not connected.

            <form method="GET" action="/login">
                <label for="login-name">Name:</label><br>
                <input id="login-name" type="text" name="name"/><br><br>
                <label for="login-scope">Scope (web required to access secured page):</label><br>
                <input id="login-scope" type="text" name="scope[]" value="web"/><br><br>
                <input type="submit" value="Login">
            </form>
        </#if>
    </div>
</@base.page>
