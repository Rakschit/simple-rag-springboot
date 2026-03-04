<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login</title>
  <link rel="stylesheet" href="<c:url value='/css/auth.css'/>">
</head>
<body>

  <div class="auth-container">
    <h2>Login</h2>
    
    <c:if test="${param.error != null}">
        <div class="error-message">
            Invalid username or password.
        </div>
    </c:if>

    <form method="post" action="/perform_login">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" placeholder="Enter your username" required>
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Enter your password" required>
      </div>
      <button type="submit">Login</button>
    </form>
    <div class="auth-footer">
      Don't have an account? <a href="/register">Register</a>
    </div>
  </div>

</body>
</html>