/* TalentBridge — App JavaScript */
document.addEventListener('DOMContentLoaded', function () {

    // Auto-dismiss alerts after 5 seconds
    document.querySelectorAll('.alert').forEach(function (alert) {
        setTimeout(function () {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(function () { alert.remove(); }, 300);
        }, 5000);
    });

    // Notification bell toggle
    var bellBtn = document.getElementById('notifBell');
    var dropdown = document.getElementById('notifDropdown');
    if (bellBtn && dropdown) {
        bellBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            dropdown.classList.toggle('show');
        });
        document.addEventListener('click', function (e) {
            if (!dropdown.contains(e.target) && e.target !== bellBtn) {
                dropdown.classList.remove('show');
            }
        });
    }

    // Mark all notifications as read
    var markAllBtn = document.getElementById('markAllRead');
    if (markAllBtn) {
        markAllBtn.addEventListener('click', function () {
            var csrfToken = document.querySelector('meta[name="_csrf"]');
            var csrfHeader = document.querySelector('meta[name="_csrf_header"]');
            var headers = { 'Content-Type': 'application/json' };
            if (csrfToken && csrfHeader) {
                headers[csrfHeader.content] = csrfToken.content;
            }
            fetch('/api/notifications/read-all', { method: 'POST', headers: headers })
                .then(function () {
                    document.querySelectorAll('.notif-item.unread').forEach(function (item) {
                        item.classList.remove('unread');
                    });
                    var badge = document.querySelector('.notif-badge');
                    if (badge) badge.style.display = 'none';
                });
        });
    }

    // Confirm delete dialogs
    document.querySelectorAll('[data-confirm]').forEach(function (el) {
        el.addEventListener('click', function (e) {
            if (!confirm(el.getAttribute('data-confirm'))) {
                e.preventDefault();
            }
        });
    });

    // Mobile sidebar toggle
    var sidebarToggle = document.getElementById('sidebarToggle');
    var sidebar = document.querySelector('.sidebar');
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('mobile-open');
        });
    }

    // Animate stat numbers
    document.querySelectorAll('.stat-value[data-count]').forEach(function (el) {
        var target = parseInt(el.getAttribute('data-count'), 10);
        var duration = 1000;
        var start = 0;
        var startTime = null;
        function animate(timestamp) {
            if (!startTime) startTime = timestamp;
            var progress = Math.min((timestamp - startTime) / duration, 1);
            el.textContent = Math.floor(progress * target);
            if (progress < 1) requestAnimationFrame(animate);
            else el.textContent = target;
        }
        requestAnimationFrame(animate);
    });

    // File input label
    var fileInput = document.getElementById('resumeInput');
    var fileLabel = document.getElementById('fileLabel');
    if (fileInput && fileLabel) {
        fileInput.addEventListener('change', function () {
            if (fileInput.files.length > 0) {
                fileLabel.textContent = fileInput.files[0].name;
            }
        });
    }
});
