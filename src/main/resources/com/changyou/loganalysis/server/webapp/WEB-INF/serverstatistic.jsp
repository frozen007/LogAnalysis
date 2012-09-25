<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<table>
    <tr>
        <td>request url</td>
        <td>total count</td>
        <td>average cost</td>
    </tr>

	<c:forEach items="${statistic_results}" var="result">
	   <tr>
	       <td>${result.requestUrl}</td>
	       <td>${result.count}</td>
	       <td>${result.avgCost}</td>
	   </tr>
	</c:forEach>
</table>