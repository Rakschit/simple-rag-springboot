document.addEventListener("DOMContentLoaded", () => {

    const textarea = document.getElementById("userQuery");
    const form = document.getElementById("messageForm"); 

    // --- AUTO-RESIZE TEXTAREA---
    const adjustHeight = () => {
        textarea.style.height = 'auto'; 
        textarea.style.height = textarea.scrollHeight + 'px';
    };

    textarea.addEventListener('input', adjustHeight);

    // --- SUBMIT ON 'ENTER' ---
    textarea.addEventListener('keydown', (event) => {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault(); 
            form.querySelector('#submitBtn').click();
        }
    });

    // Call the resize function once on load in case there's pre-filled text
    adjustHeight(); 
});