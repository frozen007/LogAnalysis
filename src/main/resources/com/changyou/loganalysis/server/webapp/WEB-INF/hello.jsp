<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<br></br>
<b>This is hello.jsp</b>
<br/>
javax.servlet.forward.request_uri:<%=request.getAttribute("javax.servlet.forward.request_uri")%><br/>
javax.servlet.forward.context_path:<%=request.getAttribute("javax.servlet.forward.context_path")%><br/>
javax.servlet.forward.servlet_path:<%=request.getAttribute("javax.servlet.forward.servlet_path")%><br/>
javax.servlet.forward.query_string:<%=request.getAttribute("javax.servlet.forward.query_string")%><br/>

${hellomsg}