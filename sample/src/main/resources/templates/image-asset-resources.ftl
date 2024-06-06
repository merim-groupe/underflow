<#import "common/base-page.ftl" as base/>

<@base.page title="Long page" lang="en">
    <h1>Assets loading</h1>
    <div>
        <p>
            <a href="/">Back Home !</a>
        </p>
    </div>
    <div>
        <img src="/assets/image.jpg" style="width: min(100%, 800px)"/>
    </div>
    <div>
        <img src="/assets/subfolder/author.jpg" style="width: min(100%, 400px)"/>
    </div>
</@base.page>
