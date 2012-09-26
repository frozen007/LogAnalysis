<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/common/taglibs.jsp"%>
<table border="2px">
    <tr>
        <th>request url</th>
        <th>total count</th>
        <th>average cost(in second)</th>
    </tr>

	<c:forEach items="${statistic_results}" var="result">
	   <tr>
	       <td>${result.requestUrl}</td>
	       <td>${result.count}</td>
	       <td>${result.avgCost}</td>
	   </tr>
	</c:forEach>
</table>