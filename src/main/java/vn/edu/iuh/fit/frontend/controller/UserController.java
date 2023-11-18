package vn.edu.iuh.fit.frontend.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.iuh.fit.backend.models.User;
import vn.edu.iuh.fit.backend.repositories.UserRepository;
import vn.edu.iuh.fit.frontend.utils.SecUtils;

import java.time.Instant;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String show_login(Model model, HttpSession session) {
        User user = new User();
        if (session.getAttribute("user") != null)
            return "redirect:/index";
        model.addAttribute("user", user);
        return "users/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model, HttpSession session) {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if (optionalUser.isEmpty() || !BCrypt.verifyer().verify(user.getPasswordHash().getBytes(), optionalUser.get().getPasswordHash().getBytes()).verified) {
            model.addAttribute("user", user);
            model.addAttribute("error", true);
            return "users/login";
        }
        session.setAttribute("user", optionalUser.get());
        return "redirect:/index";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/index";
    }
    @GetMapping("/register")
    public ModelAndView openSignUpPage(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("users/register");
        return modelAndView;
    }
    @PostMapping("/register")
    public String handleSignUp(@ModelAttribute("user")User user, Model model){
        if (user.getPasswordHash().trim().length() < 6){
            model.addAttribute("errSignUp", "Password at least 6 character");
            return "users/register";
        }
        String password = user.getPasswordHash();
        String newPassword= SecUtils.hash(password);
        user.setPasswordHash(newPassword);
        user.setRegisteredAt(Instant.now());
        try {
            userRepository.save(user);
        } catch (Exception e){
            model.addAttribute("errSignUp", "Email already exist!");
            return "users/register";
        }
        return "redirect:/index";
    }

}

