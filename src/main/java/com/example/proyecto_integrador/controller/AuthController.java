package com.example.proyecto_integrador.controller;

import com.example.proyecto_integrador.dto.UserDTO;
import com.example.proyecto_integrador.model.Carrito;
import com.example.proyecto_integrador.service.AuthService;
import com.example.proyecto_integrador.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private HttpSession httpSession;
    @Autowired
    private CarritoService carritoService;

    // Método para mostrar la vista de login (GET)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Nombre de la vista de login
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        UserDTO user = authService.login(username, password);

        if (user != null && user.getToken() != null && !user.getToken().isEmpty()) {
            // Guarda el usuario y el token en la sesión
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("token", user.getToken());

            // Obtener el carrito del usuario
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());



            // Guarda el carrito en la sesión
            httpSession.setAttribute("carrito", carrito);

            return "redirect:/"; // Redirige a la página de inicio
        }
        return "redirect:/login?error=true"; // Redirige al login con mensaje de error
    }




    // Método para mostrar la vista de registro (GET)
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";  // Nombre de la vista de registro
    }

    // Método para manejar el registro (POST)
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email,
                           @RequestParam String first_name, @RequestParam String last_name, @RequestParam String role) {
        UserDTO user = authService.register(username, password, email, first_name, last_name, role);

        if (user != null) {
            // Guarda el usuario y el token en la sesión
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("token", user.getToken());
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(user.getUsername());
            // Guarda el carrito en la sesión
            httpSession.setAttribute("carrito", carrito);

            return "redirect:/";  // Redirige a la página de inicio
        }
        return "redirect:/register?error=true";  // Redirige al registro con mensaje de error
    }

    // Método para manejar el logout (GET)
    @GetMapping("/logout")
    public String logout() {
        httpSession.invalidate();
        return "redirect:/";  // Redirige a la página de login después de cerrar sesión
    }

    // Método para mostrar la vista de perfil (GET)
    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        UserDTO sessionUser = (UserDTO) httpSession.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        UserDTO user = authService.getUserProfile(sessionUser.getToken());
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "No se pudo obtener el perfil del usuario.");
        }
        return "profile";
    }


    // Método para mostrar la vista de edición de perfil (GET)
    @GetMapping("/editProfile")
    public String showEditProfilePage(Model model) {
        UserDTO user = (UserDTO) httpSession.getAttribute("user");
        if (user == null) {
            return "redirect:/login";  // Redirige al login si el usuario no está autenticado
        }
        model.addAttribute("user", user); // Pre-popula el formulario con los datos actuales
        return "editProfile";  // Nombre de la vista de edición
    }

    // Método para manejar la actualización de los datos del perfil (POST)
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String firstName, @RequestParam String lastName,
                                @RequestParam String email, @RequestParam String username) {
        UserDTO user = (UserDTO) httpSession.getAttribute("user");
        String token = (String) httpSession.getAttribute("token");

        if (user == null || token == null) {
            return "redirect:/login";  // Redirige al login si el usuario no está autenticado
        }

        // Actualiza los datos del usuario
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);

        // Guarda los cambios en el servicio o base de datos
        UserDTO updatedUser = authService.updateUser(user, token);

        if (updatedUser != null) {
            // Actualiza los datos en la sesión
            httpSession.setAttribute("user", updatedUser);
            return "redirect:/profile";  // Redirige al perfil actualizado
        }
        return "redirect:/editProfile?error=true";  // Redirige con mensaje de error
    }
}
