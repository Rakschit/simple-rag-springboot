const fileInput = document.getElementById('fileInput');
const uploadBtn = document.getElementById('uploadBtn');
const uploadForm = document.getElementById('uploadForm');
const overlay = document.getElementById('upload_overlay');

let dragCounter = 0;

    uploadBtn.addEventListener("click", () => {
      fileInput.click();
    });

    // After the file number increment, automatically submit the form
    fileInput.addEventListener("change", () => {
        if (fileInput.files.length > 0) {
            uploadForm.submit();
        }
    });

    // --- Window-level Drag and Drop Handlers ---
    window.addEventListener('dragenter', (e) => {
        e.preventDefault();
        e.stopPropagation();
        dragCounter++;
        overlay.classList.add('visible');
    });

    window.addEventListener('dragover', (e) => {
        e.preventDefault();
        e.stopPropagation(); // Stop the browser default file opener
    });

    window.addEventListener('dragleave', (e) => {
        e.preventDefault();
        e.stopPropagation();

        dragCounter--;
        if (dragCounter === 0) {
            overlay.classList.remove('visible');
        }
    });

    window.addEventListener('drop', (e) => {
        e.preventDefault();
        e.stopPropagation();
        dragCounter = 0;
    
        overlay.classList.remove('visible');
        handleFiles(e.dataTransfer.files);
    });

    function handleFiles(files) {
        if (files.length > 1) {
            alert("Please upload only one file at a time.");
            return;
        }

        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(files[0]);
        fileInput.files = dataTransfer.files;

        uploadForm.submit();
    }
