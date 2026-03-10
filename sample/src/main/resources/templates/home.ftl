<#import "/common/base-page.ftl" as base/>

<@base.page title="Underflow" lang="en">
    <h1>${messages.get("home.title")}</h1>
    <div>
        <p>
            <b>${messages.get("home.date")}</b> ${data.currentDate}
        </p>
        <p>
            <b>${messages.get("home.securedPage")}</b> <a href="/secured">${messages.get("home.action.here")}</a>
        </p>
        <p>
            <b>${messages.get("home.openAPIDocs")}</b> <a href="/docs">${messages.get("home.action.here")}</a>
        </p>
        <p>
            <b>${messages.get("home.routingTestPage")}</b> <a href="/routes">${messages.get("home.action.here")}</a>
        </p>
        <p>
            <b>${messages.get("home.chunkTestPage")}</b> <a href="/long-content">${messages.get("home.action.here")}</a>
        </p>
        <p>
            <b>${messages.get("home.assetsTestPage")}</b> <a href="/image-asset-resources">${messages.get("home.action.here")}</a>
        </p>
        <p>
            <b>${messages.get("home.exceptionTestPages")}:</b>
        </p>
        <ul>
            <li><a href="/exception">${messages.get("home.exceptionTestPages.java")}</a></li>
            <li><a href="/ftl-exception-1">${messages.get("home.exceptionTestPages.ftl")}</a></li>
            <li><a href="/ftl-exception-2">${messages.get("home.exceptionTestPages.javaInFtl")}</a></li>
            <li><a href="/api/exception">${messages.get("home.exceptionTestPages.apiException")}</a></li>
        </ul>
        <p>
            <b>${messages.get("home.languageSelection")}</b>
        </p>
        <ul>
            <li><a href="/lang?lang=fr">${messages.get("home.languageSelection.french")}</a></li>
            <li><a href="/lang?lang=en">${messages.get("home.languageSelection.english")}</a></li>
            <li><a href="/lang?lang=cz">${messages.get("home.languageSelection.czech")}</a></li>
            <li><a href="/lang">${messages.get("home.languageSelection.deleteCookie")}</a></li>
        </ul>
        <p>
            <b>${messages.get("home.stopServer")}</b> <a href="/stop">${messages.get("home.action.here")}</a>
        </p>
    </div>
    <br/>
    <div>
        <h3>${messages.get("home.userData")}</h3>
        <p>
            <b>${messages.get("home.userData.langCookie")}: </b> ${data.langCookie!"null"}
            </br>
            <b>${messages.get("home.userData.langUsed")}: </b> ${messages.getLocale()}
        </p>
        <#if data.user??>
            <a href="/logout">${messages.get("home.userData.logout")}</a>
            <p>
                <b>${messages.get("home.userData.username")}</b> : <span>${data.user.name}</span>
            </p>
            <p>
                <b>${messages.get("home.userData.scopes")}</b> :
            </p>
            <ul>
                <#list data.user.scopes as scope>
                    <li>${scope}</li>
                </#list>
            </ul>
        <#else>
            ${messages.get("home.userData.notConnected")}.

            <p>${messages.get("home.userData.getForm")}</p>
            <form method="GET" action="/login">
                <label for="login-name">${messages.get("home.userData.form.name")}:</label><br>
                <input id="login-name" type="text" name="name"/><br><br>
                <label for="login-scope">${messages.get("home.userData.form.scopes")}:</label><br>
                <input id="login-scope" type="text" name="scope[]" value="web"/><br><br>
                <input type="submit" value="Login">
            </form>

            <br/>

            <p>${messages.get("home.userData.postForm")}</p>
            <form method="POST" action="/login">
                <label for="login-name">${messages.get("home.userData.form.name")}:</label><br>
                <input id="login-name" type="text" name="name"/><br><br>
                <label for="login-scope">${messages.get("home.userData.form.scopes")}:</label><br>
                <input id="login-scope" type="text" name="scope[]" value="web"/><br><br>
                <input id="login-scope" type="text" name="scope[]" value=""/><br><br>
                <input id="login-scope" type="text" name="scope[]" value=""/><br><br>
                <input type="submit" value="Login">
            </form>
        </#if>
    </div>
</@base.page>
