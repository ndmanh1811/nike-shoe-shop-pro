// Nike-inspired Premium Animations
(function() {
    'use strict';

    // Intersection Observer for scroll reveal
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const revealObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('active');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Initialize on DOM ready
    document.addEventListener('DOMContentLoaded', function() {
        // Trigger hero content animations immediately
        const heroContentElements = document.querySelectorAll('.hero-content .reveal');
        heroContentElements.forEach((el, index) => {
            setTimeout(() => {
                el.classList.add('active');
            }, index * 100);
        });

        // Add reveal classes to other elements
        const revealElements = document.querySelectorAll('.product-card, .feature-card, .brand-pill, .section-title');
        revealElements.forEach((el, index) => {
            el.classList.add('reveal');
            el.style.transitionDelay = `${index * 0.05}s`;
            revealObserver.observe(el);
        });

        // Hero parallax effect
        const hero = document.querySelector('.hero');
        const heroImg = document.querySelector('.hero-illustration');
        
        if (hero && heroImg) {
            window.addEventListener('scroll', () => {
                const scrolled = window.pageYOffset;
                const rate = scrolled * 0.3;
                heroImg.style.transform = `translateY(${rate}px)`;
            });
        }

        // 3D Tilt effect for hero card
        const heroCard = document.querySelector('#heroCard');
        const heroSection = document.querySelector('.hero');
        if (heroCard && heroSection) {
            heroSection.addEventListener('mousemove', (e) => {
                const rect = heroCard.getBoundingClientRect();
                const x = e.clientX - rect.left - rect.width / 2;
                const y = e.clientY - rect.top - rect.height / 2;
                const xAxis = (x / rect.width) * 15;
                const yAxis = (y / rect.height) * -15;
                heroCard.style.transform = `perspective(1000px) rotateY(${xAxis}deg) rotateX(${yAxis}deg)`;
            });

            heroSection.addEventListener('mouseleave', () => {
                heroCard.style.transform = 'perspective(1000px) rotateY(0deg) rotateX(0deg)';
            });
        }

        // Smooth scroll for anchor links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            });
        });

        // Brand pills stagger animation
        const brandPills = document.querySelectorAll('.brand-pill');
        brandPills.forEach((pill, index) => {
            pill.style.opacity = '0';
            pill.style.transform = 'translateY(20px)';
            setTimeout(() => {
                pill.style.transition = 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)';
                pill.style.opacity = '1';
                pill.style.transform = 'translateY(0)';
            }, index * 100);
        });

        // Product card hover sound effect (visual feedback)
        const productCards = document.querySelectorAll('.product-card');
        productCards.forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.zIndex = '10';
            });
            card.addEventListener('mouseleave', function() {
                this.style.zIndex = '1';
            });
        });

        // Loading animation for images
        const images = document.querySelectorAll('img');
        images.forEach(img => {
            img.addEventListener('load', function() {
                this.style.opacity = '0';
                this.style.transition = 'opacity 0.5s ease';
                setTimeout(() => {
                    this.style.opacity = '1';
                }, 50);
            });
        });

        // Counter animation for hero stats
        const stats = document.querySelectorAll('.hero-stats strong');
        stats.forEach(stat => {
            const target = parseInt(stat.textContent) || 0;
            if (target > 0) {
                let current = 0;
                const increment = target / 50;
                const timer = setInterval(() => {
                    current += increment;
                    if (current >= target) {
                        stat.textContent = target + (stat.textContent.includes('+') ? '+' : '');
                        clearInterval(timer);
                    } else {
                        stat.textContent = Math.floor(current);
                    }
                }, 30);
            }
        });
    });

    // Ripple effect for buttons
    document.addEventListener('click', function(e) {
        const btn = e.target.closest('.btn-accent');
        if (btn) {
            const ripple = document.createElement('span');
            const rect = btn.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.cssText = `
                position: absolute;
                width: ${size}px;
                height: ${size}px;
                left: ${x}px;
                top: ${y}px;
                background: rgba(255,255,255,0.3);
                border-radius: 50%;
                transform: scale(0);
                animation: ripple 0.6s ease-out;
                pointer-events: none;
            `;
            
            btn.style.position = 'relative';
            btn.style.overflow = 'hidden';
            btn.appendChild(ripple);
            
            setTimeout(() => ripple.remove(), 600);
        }
    });

    // Add ripple keyframes dynamically
    const style = document.createElement('style');
    style.textContent = `
        @keyframes ripple {
            to {
                transform: scale(2);
                opacity: 0;
            }
        }
    `;
    document.head.appendChild(style);
})();
