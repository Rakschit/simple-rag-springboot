<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Authentication</title>
  <link rel="stylesheet" href="<c:url value='/css/auth.css'/>">

  <style>
    #password-requirements {
      margin-top: 10px;
      font-size: 0.9em;
      color: #666;
    }

    #password-requirements ul {
      list-style-type: none;
      padding-left: 0;
    }

  .requirement {
    transition: all 0.2s ease-in-out;
  }

  .requirement.valid {
    color: #39e07f; 
    text-decoration: line-through;
  }

  .requirement.valid::before {
    content: '✓ '; 
    display: inline-block;
    margin-right: 5px;
  }
  </style>
</head>
<body>

  <div class="auth-container">
    <h2>Register</h2>
    
    <c:if test="${error != null}">
        <div class="error-message">
            ${error}
        </div>
    </c:if>

    <form method="post" action="/register-submit">
      <div class="form-group">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" value="${user.username}" placeholder="Enter your username" required minlength="3" maxlength="15">
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" name="passwordHash" placeholder="Enter your password" required pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{7,}$">
        <small>Must contain:
        <ul>
            <li class="requirement length">At least 7 characters</li>
            <li class="requirement uppercase">1 uppercase letter (A-Z)</li>
            <li class="requirement number">1 number (0-9)</li>
            <li class="requirement special">1 special character (!@#$%^&*)</li>
        </ul>
        </small>
      </div>
      <button type="submit">Register</button> 
    </form>
    <div class="auth-footer">
      Already have an account? <a href="/login">Login</a>
    </div>
  </div>

</body>
</html>

<script>
document.addEventListener('DOMContentLoaded', () => {
    const passwordInput = document.getElementById('password');
    const lengthCheck = document.querySelector('.requirement.length');
    const upperCheck = document.querySelector('.requirement.uppercase');
    const numberCheck = document.querySelector('.requirement.number');
    const specialCheck = document.querySelector('.requirement.special');

    passwordInput.addEventListener('input', () => {
        const password = passwordInput.value;

        // Check length
        lengthCheck.classList.toggle('valid', password.length >= 7);
        
        // Check uppercase
        upperCheck.classList.toggle('valid', /[A-Z]/.test(password));
        
        // Check number
        numberCheck.classList.toggle('valid', /\d/.test(password));

        // Check special character
        specialCheck.classList.toggle('valid', /[!@#$%^&*]/.test(password));
    });
});
</script>