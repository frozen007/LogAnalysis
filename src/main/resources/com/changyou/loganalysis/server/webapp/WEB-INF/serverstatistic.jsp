<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/common/taglibs.jsp"%>
<link rel="stylesheet" type="text/css" href="/style/t.css" />
<div class="align-center">
	<b>${logentity.memo}</b>
	<br/>
	<center>
	<table border="2">
	    <tr>
	        <th><fmt:message key="serverstat.table.urlcolumn"/></th>
	        <th><fmt:message key="serverstat.table.countcolumn"/></th>
	        <th><fmt:message key="serverstat.table.avgcostcolumn"/></th>
	    </tr>
	
		<c:forEach items="${statisticresultList}" var="result">
		   <tr>
		       <td class="text">${result.requestUrl}</td>
		       <td class="number">${result.count}</td>
		       <td class="number">${result.avgCost}</td>
		   </tr>
		</c:forEach>
	</table>
	</center>
</div>