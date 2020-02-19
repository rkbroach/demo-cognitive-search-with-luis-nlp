<%@ page import="main.java.app.App" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="main.java.app.Result" %>
<%@ page import="java.util.List" %>
<%--
    Project Name: Contextual Search Prototype
    Created by: Anshul Tyagi, Mohak Bahl, Nitya Singh and Rohan Kevin Broach
    Date: 19-02-2020
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://bootswatch.com/4/cosmo/bootstrap.css">

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>

    <title>Contextual Search Prototype</title>

</head>
<body>

<!-- Search Bar -->
<div class="container">
    <div class="jumbotron">
        <h1 class="display-3">Contextual Search Prototype</h1>
        <p class="lead">A project by Anshul Tyagi, Mohak Bahl, Nitya Singh and Rohan Kevin Broach</p>
        <hr class="my-4">
        <p class="lead">
        <form class="form-inline" name="searchForm" method="post" action="index.jsp">
            <input class="form-control col-sm-10" type="text" placeholder="Search" name="searchQuery">
            <!--<button class="btn btn-secondary my-2 offset-sm-1 col" type="submit" value="Search">Search</button> -->
            <button class="btn btn-primary btn-lg" type="submit" value="Search">Search</button>
        </form>
        </p>
    </div>
</div>

<!-- Query Processing -->
<%
    String searchQuery = request.getParameter("searchQuery");
    if (searchQuery == null) {
        // myText is null when the page is first requested, so do nothing
    } else {
        if (searchQuery.length() == 0) {
            // a zero length string
%>
<div class="container">
    <div class="alert alert-dismissible alert-danger">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <strong>Sorry!</strong>Try searching again.
    </div>
</div>

<!-- Results -->
<% } else {
    App appObj = new App();
    List<String> list = appObj.getSearchQuery(searchQuery);
%>
<div class="container">
    <div class="card-columns">
        <% for(String ans : list) { %>
            <div class="card text-white bg-primary mb-3" style="max-width: 20rem;">
                <div class="card-body">
                    <h4 class="card-title"><%out.print(ans);%></h4>
                </div>
            </div>
        <%}
        out.println("</div></div>");
        }
      }
    %>


</body>
</html>
