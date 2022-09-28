<#import "../common/base-page.ftl" as base/>

<#macro testLink link>
    <p>
        <a href="${link}">${link}</a>
    </p>
</#macro>

<#macro testGroup name>
    <div>
        <h3>${name}</h3>
        <#nested>
    </div>
</#macro>

<@base.page title="Routing testing" lang="en">
    <h1>Routing testing page !</h1>
    <p>
        <a href="/">Back Home !</a>
    </p>
    <@testGroup "UUID in URL">
        <@testLink "/routes/630ce4d0-58e9-4a78-aae3-4f320304cbf0"/>
        <@testLink "/routes/630ce4d0-58e9-4a78-aae3-4f320304cbf0?arg=foobar"/>
    </@testGroup>
    <@testGroup "Query String List">
        <@testLink "/routes/query-list"/>
        <@testLink "/routes/query-list?entry=foo"/>
        <@testLink "/routes/query-list?entry=foo&entry=bar"/>
        <@testLink "/routes/query-list?entry=foo&entry=bar&entry=foobar"/>
        <@testLink "/routes/query-list?entry[]=foo&entry[]=bar"/>
        <@testLink "/routes/query-list?entry[]=foo&entry=bar"/>
        <@testLink "/routes/query-list?entry[0]=foo&entry[1]=bar"/>
        <@testLink "/routes/query-list?entry[1]=foo&entry[0]=bar"/>
        <@testLink "/routes/query-list?entry[0]=foo&entry[0]=bar"/>
        <@testLink "/routes/query-list?entry[20]=foo&entry[12]=bar&entry=other&entry[]=gnarf"/>
    </@testGroup>
</@base.page>
