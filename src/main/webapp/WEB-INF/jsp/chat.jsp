<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>RAG Q&A</title>
    <link rel="stylesheet" href="<c:url value='/css/chat.css'/>">
</head>
<body>
    <div class="main-container">
        <div class="chat-header">
            <h2>${title}</h2>
        </div>

        <div id="messages">
            <c:forEach var="message" items="${messages}">
                <p class="${message.role.name().toLowerCase()}">
                    <c:out value="${message.content}" escapeXml="false" />
                </p>
            </c:forEach>
        </div>

        <form id="messageForm">
            <textarea id="userQuery" name="userQuery" placeholder="Ready for your questions" rows="1"></textarea>
            <button type="submit" id="submitBtn" class="submitBtn">
                <svg class="submit-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24">
                    <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
                </svg>
            </button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <script src="<c:url value='/js/api.js'/>"></script>
    <script src="<c:url value='/js/TextAreaHandler.js'/>"></script>
</body>
</html>