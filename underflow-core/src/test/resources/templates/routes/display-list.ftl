<#import "../common/base-page.ftl" as base/>

<@base.page title="Display List" lang="en">
    <h1>Display list</h1>
    <div>
        <p>
            <a href="/routes">Back to Routing Home !</a>
        </p>
    </div>
    <div>
        <ul>
            <#list listData as entry>
                <li>${entry}</li>
            </#list>
        </ul>
    </div>
</@base.page>
