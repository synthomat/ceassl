document.addEventListener("DOMContentLoaded", function(event) {

});

function deleteTarget(id) {
    fetch('/targets/' + id, {
        method: 'delete',
        headers: {"X-CSRF-Token": csrfToken}
    }).then(data => {
        if (data.ok) {
            document.location.href = "/"
        }
    })
}