function mostrarNotificacion(message, type = 'success') {
    const notificationContainer = document.getElementById('notification-container');
    const notification = document.createElement('div');
    notification.classList.add('notification', type);
    notification.innerHTML = `
        ${message}
        <span class="close-btn" onclick="this.parentElement.remove()">×</span>
    `;

    notificationContainer.appendChild(notification);

    // Eliminar la notificación después de 5 segundos
    setTimeout(() => {
        notification.remove();
    }, 5000);
}

function agregarProducto(event, form) {
    event.preventDefault(); // Evita que el formulario se envíe de manera tradicional

    const formData = new FormData(form); // Extrae los datos del formulario

    fetch('/carrito/agregar', {
        method: 'POST',
        body: new URLSearchParams(formData),
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
    })
        .then(response => {
            // Si el estado es 401 (usuario no autenticado), muestra el modal de inicio de sesión
            if (response.status === 401) {
                console.log('Usuario no autenticado, mostrando modal.');
                mostrarModalSesion();
                throw new Error('Usuario no autenticado');
            }
            // Si el estado es un redireccionamiento (3xx), muestra el modal de inicio de sesión
            else if (response.status >= 300 && response.status < 400) {
                console.log('Redirección detectada, mostrando modal.');
                mostrarModalSesion();
                throw new Error('Redirección al login');
            } else if (response.ok) {
                return response.json(); // Procesa el JSON de la respuesta
            } else {
                // Manejo de otros errores
                return response.text().then(text => {
                    throw new Error(text || 'Error desconocido.');
                });
            }
        })
        .then(data => {
            mostrarNotificacion(data.message, 'success'); // Mostrar mensaje de éxito
        })
        .catch(error => {
            console.error('Error al agregar producto:', error.message);

            if (error.message.includes("Cantidad inválida") || error.message.includes("suficiente stock")) {
                mostrarNotificacion(error.message, 'warning'); // Advertencia para stock
            } else if (error.message.includes("El producto no existe")) {
                mostrarNotificacion("No se encontró el producto seleccionado.", 'error');
            } else if (error.message.includes("No se pudo obtener el carrito")) {
                mostrarNotificacion("Hubo un problema al cargar tu carrito. Inténtalo más tarde.", 'error');
            } else {
                mostrarNotificacion(error.message || 'Ocurrió un error al agregar el producto al carrito.', 'error');
            }
        });
}

function mostrarModalSesion() {
    // Verifica si ya existe un modal antes de agregarlo al DOM
    if (!document.getElementById('loginModal')) {
        const modalHtml = `
      <div class="modal fade" id="loginModal" tabindex="-1" aria-labelledby="loginModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg rounded-4">
            <div class="modal-header bg-warning text-white rounded-top-4">
              <h5 class="modal-title fw-bold" id="loginModalLabel">¡Atención!</h5>
              <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body text-center p-4">
              <i class="fas fa-user-lock fa-3x text-warning mb-3"></i>
              <p class="fs-5">Para agregar productos al carrito, es necesario iniciar sesión.</p>
              <p class="text-muted small">Si aún no tienes una cuenta, puedes registrarte fácilmente.</p>
            </div>
            <div class="modal-footer justify-content-center bg-light rounded-bottom-4">
              <a href="/login" class="btn btn-primary px-4">Iniciar Sesión</a>
              <a href="/register" class="btn btn-outline-secondary px-4">Registrarse</a>
            </div>
          </div>
        </div>
      </div>
    `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
    }

    // Inicializa y muestra el modal usando Bootstrap
    const modal = new bootstrap.Modal(document.getElementById('loginModal'));
    modal.show();

    // Limpia el modal del DOM al cerrarse
    modal._element.addEventListener('hidden.bs.modal', () => {
        modal.dispose();
        document.getElementById('loginModal').remove();
    });
}
