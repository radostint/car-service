package com.cp.carservice;

/**
 *
 * @author rado1
 */
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Secured("ROLE_ADMIN")
    @GetMapping("/users/all")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users-list";
    }

    @GetMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);

        return "profile";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("users/add")
    public String showAddUserForm(User user) {
        return "register";
    }

    @GetMapping("/register")
    public String showSignUpForm(User user) {
        user.setRole("ROLE_USER");
        return "register";
    }

    @PostMapping("/register")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        PasswordEncoder passencoder = new BCryptPasswordEncoder(12);
        user.setPassword(passencoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            model.addAttribute("users", userRepository.findAll());
            return "redirect:/";
        } catch (Exception e) {
            if (e.getMessage().contains("username")) {
                model.addAttribute("duplicateUsername", "This username is already taken");
            } else if (e.getMessage().contains("phone_number")) {
                model.addAttribute("duplicatePhoneNumber", "This phone number is already used");
            } else {
                model.addAttribute("errorUnknown", e.getMessage());
            }
            return ("register");
        }

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
            model.addAttribute("user", user);
            return "update-user";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such user does not exist.");
            return "users-list";
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid User user,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            user.setId(id);
            return "update-user";
        }

        PasswordEncoder passencoder = new BCryptPasswordEncoder(12);
        user.setPassword(passencoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            model.addAttribute("users", userRepository.findAll());
            return "redirect:/users/all";
        } catch (Exception e) {
            user.setId(id);
            return "update-user";
        }

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
            userRepository.delete(user);
            model.addAttribute("users", userRepository.findAll());
            return "redirect:/users/all";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such user does not exist.");
            return "users-list";
        }

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search-user")
    public String showUsersByPhoneNumber(@RequestParam(name = "phoneNumber", required = false) String phoneNumber, Model model) {
        User user = userRepository.findByPhoneNumber(phoneNumber);
        if (user == null) {
            model.addAttribute("error", "Nothing found.");
            return "users-list";
        }
        return "redirect:/user/" + String.valueOf(user.getId()) + "/vehicles";
    }
}
