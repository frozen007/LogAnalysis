<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/common/taglibs.jsp"%>
<link rel="stylesheet" type="text/css" href="/style/t.css" />
<link href="/style/smoothness/jquery-ui-1.9.0.css" rel="stylesheet">
<script src="/js/jquery-1.8.2.js"></script>
<script src="/js/jquery-ui-1.9.0.js"></script>
<script>
var COST_RANGE_MIN = ${QUERY_COST_RANGE[0]};
var COST_RANGE_MAX = ${QUERY_COST_RANGE[1]}+1;

$(function() {
    $("#slider").slider({
        range : true,
        min : COST_RANGE_MIN,
        max : COST_RANGE_MAX,
        values : [ ${mincost}, ${maxcost} ],
        slide : function(event, ui) {
            var minvalue = ui.values[0];
            var maxvalue = ui.values[1];
            if (maxvalue == COST_RANGE_MAX) {
                maxvalue = '<fmt:message key="serverstat.query.condition.maxvalue"/>';
            } else {
                maxvalue = maxvalue + "s";
            }

            $("#amount").val(minvalue + "s ~ " + maxvalue);
        }
    });

    var currentmin = $("#slider").slider("values", 0);
    var currentmax = $("#slider").slider("values", 1);
    if (currentmax == COST_RANGE_MAX) {
    	currentmax = '<fmt:message key="serverstat.query.condition.maxvalue"/>';
    } else {
    	currentmax = currentmax + "s";
    }
    $("#amount").val(currentmin + "s ~ " + currentmax);

    $("#querybtn").click(function(){
    	$("input[name='mincost']").val($("#slider").slider("values", 0));
    	$("input[name='maxcost']").val($("#slider").slider("values", 1));
    	$("#queryform").submit();
    });

});

</script>
<div class="align-center">
    <b>${logentity.memo}</b> <br />

    <center>
        <table cellpadding="10px">
            <tr>
                <td colspan="2">
                    <p style="border: 0; color: #f6931f; font-weight: bold;">
                        <b><fmt:message key="serverstat.query.condition.memo"/>:</b>
                        <input type="text" id="amount" style="border: 0; color: #f6931f; font-weight: bold;" />
                    </p>
                    <div id="slider"></div>
                </td>
                <td valign="bottom" align="right">
                    <form id="queryform" action="/serverstat" method="get">
                        <input type="hidden" name="logcollection" value="${logcollection}"/>
                        <input type="hidden" name="loguniqueid" value="${loguniqueid}"/>
                        <input type="hidden" name="mincost" value="${mincost}"/>
                        <input type="hidden" name="maxcost" value="${maxcost}"/>
                        <input id="querybtn" type="button" value="query"/>
                    </form>
                </td>
            </tr>
        </table>
        <table border="2">
            <tr>
                <th><fmt:message key="serverstat.table.urlcolumn" /></th>
                <th><fmt:message key="serverstat.table.countcolumn" /></th>
                <th><fmt:message key="serverstat.table.avgcostcolumn" /></th>
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