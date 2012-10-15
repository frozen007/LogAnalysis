<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/common/taglibs.jsp"%>
<link rel="stylesheet" type="text/css" href="/style/t.css" />

<div class="align-center">
    <b><fmt:message key="serverlist.title"/>&nbsp;${analysisdate}</b>
    <br/>
    (<fmt:message key="serverlist.msg1"/>)
    <br/>
    <center>
	<table border="2">
	    <tr>
	        <th><fmt:message key="serverlist.table.servercolumn"/></th>
	        <th><fmt:message key="serverlist.table.recordcolumn"/></th>
	    </tr>
	    
		<c:forEach items="${logcollectionList}" var="collection">
		    <tr>
		        <td><a href="/serverstat?logcollection=${collection.collectionName}&loguniqueid=${collection.logEntity.uniqueID}">${collection.logEntity.memo}</a></td>
		        <td class="number">${collection.collectionCnt}</td>
		    </tr>
		</c:forEach>
	</table>
	</center>
</div>