<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<c:forEach items="${logcollections}" var="collection">
<a href="/loganalysis/serverstat?logcollection=${collection.collectionName}">${collection.logEntity.memo}</a>&nbsp;&nbsp;Record count:${collection.collectionCnt}
<br/>
</c:forEach>