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

            $("#amount").html(minvalue + "s ~ " + maxvalue);
        }
    });

    var currentmin = $("#slider").slider("values", 0);
    var currentmax = $("#slider").slider("values", 1);
    if (currentmax == COST_RANGE_MAX) {
    	currentmax = '<fmt:message key="serverstat.query.condition.maxvalue"/>';
    } else {
    	currentmax = currentmax + "s";
    }
    $("#amount").html(currentmin + "s ~ " + currentmax);

    $( "#querybtn" ).button({
        icons: {
            primary: "ui-icon-gear"
        },
    });

    function doQuery(sortoption) {
        $("input[name='mincost']").val($("#slider").slider("values", 0));
        $("input[name='maxcost']").val($("#slider").slider("values", 1));
        $("input[name='sortoption']").val(sortoption);
        $("#queryform").submit();
    }

    $("#querybtn").click(function(){
    	doQuery("");
    });

    $("a[id='columnurl']").click(function(){
    	<c:if test="${not (sortoptionMap._id eq '2')}">
            doQuery('_id^2');
    	</c:if>
        <c:if test="${sortoptionMap._id eq '2'}">
            doQuery('_id^1');
        </c:if>

    });

    $("a[id='columncount']").click(function(){
        <c:if test="${not (sortoptionMap.cnt eq '2')}">
            doQuery('cnt^2');
        </c:if>
        <c:if test="${sortoptionMap.cnt eq '2'}">
            doQuery('cnt^1');
        </c:if>
    });

    $("a[id='columnavgcost']").click(function(){
        <c:if test="${not (sortoptionMap.avg eq '2')}">
            doQuery('avg^2');
        </c:if>
        <c:if test="${sortoptionMap.avg eq '2'}">
            doQuery('avg^1');
        </c:if>
    });

});

</script>
<div class="align-center">
    <b>${logentity.memo}</b> <br />

    <center>
        <table cellpadding="10px">
            <tr>
                <td colspan="2" valign="middle">
                    <p style="border: 0; color: #f6931f; font-weight: bold;">
                        <b><fmt:message key="serverstat.query.condition.memo"/>:</b>
                        <!-- <input type="text" id="amount" disabled="disabled" style="border: 0; color: #f6931f; font-weight: bold;" />-->
                        <label id="amount" style="border: 0; color: #f6931f; font-weight: bold;"></label>
                    </p>
                    <div id="slider"></div>
                </td>
                <td valign="bottom" align="right" valign="bottom">
                    <form id="queryform" action="/serverstat" method="get">
                        <input type="hidden" name="logcollection" value="${logcollection}"/>
                        <input type="hidden" name="loguniqueid" value="${loguniqueid}"/>
                        <input type="hidden" name="mincost" value="${mincost}"/>
                        <input type="hidden" name="maxcost" value="${maxcost}"/>
                        <input type="hidden" name="sortoption" value=""/>
                    </form>
                    <button id="querybtn">GO</button>
                </td>
            </tr>
        </table>
        <table border="2">
            <thead>
                <tr>
                    <th class="sortable order${sortoptionMap._id}"><a id="columnurl" href="javascript:void(0);"><fmt:message key="serverstat.table.urlcolumn" /></a></th>
                    <th class="sortable order${sortoptionMap.cnt}"><a id="columncount" href="javascript:void(0);"><fmt:message key="serverstat.table.countcolumn" /></a></th>
                    <th class="sortable order${sortoptionMap.avg}"><a id="columnavgcost" href="javascript:void(0);"><fmt:message key="serverstat.table.avgcostcolumn" /></a></th>
                </tr>
            </thead>

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