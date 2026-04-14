// --- THEME / DARK MODE SCRIPT ---

// Hàm áp dụng giao diện và đổi icon
function applyTheme(theme) {
    // Đổi thuộc tính theme của toàn bộ trang HTML
    document.documentElement.setAttribute('data-bs-theme', theme);
    // Lưu lựa chọn vào bộ nhớ trình duyệt
    localStorage.setItem('theme', theme);
    
    // Cập nhật tất cả các icon mặt trăng/mặt trời
    const icons = document.querySelectorAll('.theme-icon');
    icons.forEach(icon => {
        if (theme === 'dark') {
            icon.className = 'theme-icon bi bi-sun-fill text-warning fs-5'; // Mặt trời vàng
        } else {
            icon.className = 'theme-icon bi bi-moon-stars-fill fs-5 text-light'; // Mặt trăng trắng
        }
    });
}

// Hàm được gọi khi bấm nút chuyển đổi
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-bs-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
    applyTheme(newTheme);
}

// Hàm chạy ngay lập tức khi load trang (tránh bị nháy màn hình trắng)
(function() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-bs-theme', savedTheme);
})();

// Cập nhật lại UI sau khi các thẻ HTML đã load xong
document.addEventListener('DOMContentLoaded', function() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    applyTheme(savedTheme);
});
