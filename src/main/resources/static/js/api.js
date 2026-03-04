const submitBtn = document.getElementById("submitBtn");
const input = document.getElementById("userQuery");
const messagesDiv = document.getElementById("messages");
const id = window.location.pathname.split("/qa/")[1];
console.log("id:", id);

marked.setOptions({
  breaks: true
});

const messageForm = document.getElementById("messageForm");



messageForm.addEventListener("submit", function(event) {
    event.preventDefault();
    const text = input.value.trim();
    if (!text) return;

    submitBtn.disabled = true;
    appendMessage(text, "user"); 
    const thinkingMsg = appendMessage("AI is thinking...", "thinking");

    fetch("/api/ask", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            userQuery: text,
            id: id
        })
    })
    .then(res => {
        if (!res.ok) {
            throw new Error("Network response was not ok");
        }
        return res.json();
    })
    .then(qaData => {
        thinkingMsg.remove();
        // Use 'assistant' as the class name for consistency
        appendMessage(qaData.answer, "assistant"); 
    })
    .catch(err => {
        console.error("Error:", err);
        thinkingMsg.remove();
        // Use 'assistant' for error messages too
        appendMessage("Error: Could not get an answer.", "assistant"); 
    })
    .finally(() => {
        submitBtn.disabled = false;
        input.value = "";
        input.focus();
    });
});

function appendMessage(text, className) {
    const p = document.createElement("p");
    p.className = className;
    // This correctly handles markdown for dynamically added messages
    p.innerHTML = marked.parse(text); 
    messagesDiv.appendChild(p);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
    return p;
}

// --- Add this block to render markdown for existing messages on page load ---
document.addEventListener("DOMContentLoaded", () => {
    const messages = document.querySelectorAll("#messages p");
    messages.forEach(message => {
        // Take the plain text, parse it as markdown, and replace the content
        const rawText = message.textContent;
        message.innerHTML = marked.parse(rawText);
    });
    // Scroll to the bottom to show the latest messages
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
});