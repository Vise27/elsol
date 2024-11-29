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
        return "User/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        UserDTO user = authService.login(username, password);

        if (user != null && user.getToken() != null && !user.getToken().isEmpty()) {
            // Guarda el usuario y el token en la sesión
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("token", user.getToken());
            return "redirect:/"; // Redirige a la página de inicio
        }
        return "redirect:/login?error=true"; // Redirige al login con mensaje de error
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "User/register";
    }
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email,
                           @RequestParam String first_name, @RequestParam String last_name) {
        UserDTO user = authService.register(username, password, email, first_name, last_name, "user");

        if (user != null) {
            return "redirect:/login?registered=true";
        }

        return "redirect:/register?error=true";
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
        return "User/profile";
    }
    // Método para mostrar la vista de edición del perfil (GET)
    @GetMapping("/profile/edit")
    public String editUserProfileForm(Model model) {
        UserDTO sessionUser = (UserDTO) httpSession.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Obtener los datos actuales del perfil del usuario
        UserDTO user = authService.getUserProfile(sessionUser.getToken());
        if (user != null) {
            model.addAttribute("user", user);
            return "User/editProfile";  // Vista de edición del perfil
        } else {
            model.addAttribute("error", "No se pudo obtener el perfil del usuario.");
            return "redirect:/profile";  // Redirigir a la vista de perfil si hay error
        }
    }

    // Método para guardar los cambios de edición del perfil (POST)
    @PostMapping("/profile/edit")
    public String saveUserProfile(@RequestParam("email") String email,
                                  @RequestParam("firstName") String firstName,
                                  @RequestParam("lastName") String lastName,
                                  Model model) {
        UserDTO sessionUser = (UserDTO) httpSession.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Llamar al servicio para editar el perfil
        UserDTO updatedUser = authService.editUserProfile(sessionUser.getToken(), email, firstName, lastName);

        if (updatedUser != null) {
            model.addAttribute("user", updatedUser);
            model.addAttribute("success", "Perfil actualizado exitosamente.");
            return "User/profile";  // Redirigir a la vista de perfil después de la actualización
        } else {
            model.addAttribute("error", "No se pudo actualizar el perfil.");
            return "User/editProfile";  // Volver a la vista de edición si hubo error
        }
    }


}
