<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RAG</title>
    <%-- The updated stylesheet is linked here --%>
    <link rel="stylesheet" href="<c:url value='/css/home.css'/>">

    <style>
        .welcome-message {
        position: absolute; 
        top: 50px;
        left: 80vw;
        width: fit-content; 
        color: white; 
    }
    </style>

</head>
<body>
    <div class="main-container">

        <div class="welcome-message">
            <c:choose>
                <c:when test="${username != null}">
                    <div class="user-dropdown">
                        <h2 id="username-trigger">${username}</h2>
                        <div id="dropdown-content" class="dropdown-content">
                            <form method="post" action="<c:url value='/logout'/>">
                                <input type="submit" value="Logout"/>
                            </form>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <h2><a href="<c:url value='/login'/>">Login</a></h2>
                </c:otherwise>
            </c:choose>
        </div>

        <form action="/generate" id="uploadForm" class="upload-section" method="POST" enctype="multipart/form-data">
            <h1>Upload Your Document to get started.</h1>
            <p>Supported formats: PDF, Word, Text</p>
            <input type="file" id="fileInput" name="file" hidden>
            <input type="button" value="Upload" id="uploadBtn">
        </form>

        <div class="search-section">
            <c:choose>
                <c:when test="${not empty chats}">
                    <h3>Your Documents</h3>
                    <div class="filter-controls">
                        <button onclick="sortTable(1, 'string', this)">Sort by Name</button>
                        <button onclick="sortTable(2, 'date', this)">Sort by Created Date</button>
                        <button class="active-sort" onclick="sortTable(3, 'date', this)">Sort by Last Modified</button>
                    </div>

                    <table class="chat-table" id="chatTable">
                        <thead>
                            <tr>
                                <th>Filename</th>
                                <th>Created On</th>
                                <th>Last Modified</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="chat" items="${chats}">
                                <tr>
                                    <td><a href="<c:url value='/qa/${chat.chatId}'/>"><c:out value="${chat.title}"/></a></td>
                                    <td><fmt:formatDate value="${chat.createdAtTimestamp}" type="both" dateStyle="medium" timeStyle="short"/></td>
                                    <td><fmt:formatDate value="${chat.updatedAtTimestamp}" type="both" dateStyle="medium" timeStyle="short"/></td>
                                    <td>
                                        <form action="<c:url value='/chat/${chat.chatId}'/>" method="post" onsubmit="return confirm('Are you sure you want to delete this document?');">
                                            <input type="hidden" name="_method" value="delete">
                                            <button type="submit" class="delete-btn">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                
                <c:otherwise>
                    <h3>No document uploaded till now.
                    <a href="#" onclick="document.getElementById('uploadBtn').click(); return false;">Upload to get started</a>.</h3>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <div class="overlay" id="upload_overlay">
        <div>Drop the document anywhere to submit</div>
    </div>
    
    <script src="<c:url value='/js/FileHandler.js'/>"></script>
    <script src="<c:url value='/js/SortTable.js'/>"></script>

    <script>
    document.addEventListener('DOMContentLoaded', function() {
    const dropdownContainer = document.querySelector('.user-dropdown');
    if (dropdownContainer) {
        const usernameTrigger = document.getElementById('username-trigger');
        const dropdownContent = document.getElementById('dropdown-content');
        usernameTrigger.addEventListener('click', function() {
            dropdownContent.classList.toggle('show');
        });
        window.addEventListener('click', function(event) { 
            if (!dropdownContainer.contains(event.target)) { 
                if (dropdownContent.classList.contains('show')) { 
                    dropdownContent.classList.remove('show'); 
                }
            }
        });
    }
});
</script>
</body>
</html>

