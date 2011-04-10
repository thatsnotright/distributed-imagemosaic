
<%@ page import="edu.cu.csci5673.caete.ImageHeader" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'imageHeader.label', default: 'ImageHeader')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="url" title="${message(code: 'imageHeader.url.label', default: 'Url')}" />
                        
                            <g:sortableColumn property="blue" title="${message(code: 'imageHeader.blue.label', default: 'Blue')}" />
                        
                            <g:sortableColumn property="green" title="${message(code: 'imageHeader.green.label', default: 'Green')}" />
                        
                            <g:sortableColumn property="latitude" title="${message(code: 'imageHeader.latitude.label', default: 'Latitude')}" />
                        
                            <g:sortableColumn property="longitude" title="${message(code: 'imageHeader.longitude.label', default: 'Longitude')}" />
                        
                            <g:sortableColumn property="red" title="${message(code: 'imageHeader.red.label', default: 'Red')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${imageHeaderInstanceList}" status="i" var="imageHeaderInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${imageHeaderInstance.url}">${fieldValue(bean: imageHeaderInstance, field: "url")}</g:link></td>
                        
                            <td>${fieldValue(bean: imageHeaderInstance, field: "blue")}</td>
                        
                            <td>${fieldValue(bean: imageHeaderInstance, field: "green")}</td>
                        
                            <td>${fieldValue(bean: imageHeaderInstance, field: "latitude")}</td>
                        
                            <td>${fieldValue(bean: imageHeaderInstance, field: "longitude")}</td>
                        
                            <td>${fieldValue(bean: imageHeaderInstance, field: "red")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${imageHeaderInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
