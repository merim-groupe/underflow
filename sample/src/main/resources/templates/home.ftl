<#import "/common/base-page.ftl" as base/>

<@base.page title="Underflow" lang="en">
    <h1>Underflow !</h1>
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
        <p>
            <b>Routing test page</b> <a href="/routes">Here !</a>
        </p>
        <p>
            <b>Long page with multiple chunk (HTTP)</b> <a href="/long-content">Here !</a>
        </p>
        <p>
            <b>Assets loading from Java Resources</b> <a href="/image-asset-resources">Here !</a>
        </p>
        <p>
            <b>DEV exception</b> <a href="/exception">Here !</a>
        </p>
    </div>
    <br/>
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

            <p>GET Login form</p>
            <form method="GET" action="/login">
                <label for="login-name">Name:</label><br>
                <input id="login-name" type="text" name="name"/><br><br>
                <label for="login-scope">Scope (web required to access secured page):</label><br>
                <input id="login-scope" type="text" name="scope[]" value="web"/><br><br>
                <input type="submit" value="Login">
            </form>

            <br/>

            <p>POST Login form</p>
            <form method="POST" action="/login">
                <label for="login-name">Name:</label><br>
                <input id="login-name" type="text" name="name"/><br><br>
                <label for="login-scope">Scope (web required to access secured page):</label><br>
                <input id="login-scope" type="text" name="scope[]" value="web"/><br><br>
                <input id="login-scope" type="text" name="scope[]" value=""/><br><br>
                <input id="login-scope" type="text" name="scope[]" value=""/><br><br>
                <input type="submit" value="Login">
            </form>
        </#if>
    </div>
</@base.page>
