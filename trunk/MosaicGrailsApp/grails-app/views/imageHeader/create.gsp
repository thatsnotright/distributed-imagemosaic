

<%@ page import="edu.cu.csci5673.caete.ImageHeader" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'imageHeader.label', default: 'ImageHeader')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${imageHeaderInstance}">
            <div class="errors">
                <g:renderErrors bean="${imageHeaderInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="url"><g:message code="imageHeader.url.label" default="Url" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: imageHeaderInstance, field: 'url', 'errors')}">
                                    <g:textField name="url" value="${imageHeaderInstance?.url}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="blue"><g:message code="imageHeader.blue.label" default="Blue" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: imageHeaderInstance, field: 'blue', 'errors')}">
                                    <g:textField name="blue" value="${fieldValue(bean: imageHeaderInstance, field: 'blue')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="green"><g:message code="imageHeader.green.label" default="Green" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: imageHeaderInstance, field: 'green', 'errors')}">
                                    <g:textField name="green" value="${fieldValue(bean: imageHeaderInstance, field: 'green')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="latitude"><g:message code="imageHeader.latitude.label" default="Latitude" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: imageHeaderInstance, field: 'latitude', 'errors')}">
                                    <g:textField name="latitude" value="${fieldValue(bean: imageHeaderInstance, field: 'latitude')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="longitude"><g:message code="imageHeader.longitude.label" default="Longitude" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: imageHeaderInstance, field: 'longitude', 'errors')}">
                                    <g:textField name="longitude" value="${fieldValue(bean: imageHeaderInstance, field: 'longitude')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="red"><g:message code="imageHeader.red.label" default="Red" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: imageHeaderInstance, field: 'red', 'errors')}">
                                    <g:textField name="red" value="${fieldValue(bean: imageHeaderInstance, field: 'red')}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
