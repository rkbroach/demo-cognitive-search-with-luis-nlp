<%@ page import="main.java.app.App" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="main.java.app.Result" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: broac
  Date: 17-02-2020
  Time: 09:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Contextual Search with NLP Capability</title>
  </head>
  <body>

    <h1>Contextual Search with NLP Capability using Azure Cognitive Services</h1>
    <p>by Anshul, Mohak, Nitya, Rohan</p>\

    <form name="searchForm" method="post" action="index.jsp">
      Search: <input type="text" name="searchQuery"/> <br/>
      <input type="submit" value="Search" />
    </form>
    <%
      String searchQuery = request.getParameter("searchQuery");
      if (searchQuery == null) {
        // myText is null when the page is first requested, so do nothing
      } else {
        if (searchQuery.length() == 0) {
          // a zero length string
    %>
    <b> Search Query is empty</b>
    <%   } else { %>
    <b>Search Query is <%= searchQuery %></b>
    <%
          App appObj = new App();
          List<String> list = appObj.getSearchQuery(searchQuery);
          out.println("<ol> ");
          for(String ans : list)
          {
            out.println("<li> " + ans + "</li> ");
          }
          out.println("</ol>");
        }
      }
    %>
<!-- ---------------------------------------- -->









  </body>
</html>
