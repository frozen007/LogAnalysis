<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/common/taglibs.jsp"%>
<table border="2px">
    <tr>
        <th>server</th>
        <th>record count</th>
    </tr>
    
	<c:forEach items="${logcollections}" var="collection">
	    <tr>
	        <td><a href="/serverstat?logcollection=${collection.collectionName}">${collection.logEntity.memo}</a></td>
	        <td>${collection.collectionCnt}</td>
	    </tr>	
	</c:forEach>
</table>