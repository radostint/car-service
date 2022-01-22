package com.cp.carservice;
/**
 *
 * @author rado1
 */
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VehicleController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    VehicleTypeRepository vehicleTypeRepository;

    @Secured("ROLE_ADMIN")
    @GetMapping("/vehicles/all")
    public String showAllCars(Model model) {
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "vehicles-list";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/{id}/vehicles")
    public String showUserCars(@PathVariable("id") long id, Model model) {
        try {
            userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such user does not exist.");
            return "vehicles-list";
        }
        model.addAttribute("vehicles", vehicleRepository.findByUserId(id));
        model.addAttribute("userId", id);
        return "user-vehicles";
    }

    @GetMapping("/profile/vehicles")
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("vehicles", vehicleRepository.findByUserId(user.getId()));
        model.addAttribute("userId", user.getId());
        return "user-vehicles";

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user/{id}/vehicles/add")
    public String showAddVehicleForm(@PathVariable("id") long id, Vehicle vehicle, Model model) {
        try {
            userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such user does not exist.");
            return "vehicles-list";
        }
        model.addAttribute("types", vehicleTypeRepository.findAll());
        User user = new User();
        user.setId(id);
        vehicle.setUser(user);
        model.addAttribute("userId", id);
        return "add-vehicle";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/user/{id}/vehicles/add")
    public String addVehicle(@PathVariable("id") long id, @Valid Vehicle vehicle, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("types", vehicleTypeRepository.findAll());
            model.addAttribute("userId", id);
            return "add-vehicle";
        }
        try {
            vehicleRepository.save(vehicle);
            model.addAttribute("vehicles", vehicleRepository.findAll());
            return "redirect:/vehicles/all";
        } catch (Exception e) {
            if (e.getMessage().contains("regplate")) {
                model.addAttribute("duplicateRegplate", "This registration plate is already registered");
            } else {
                model.addAttribute("error", e.getMessage());
            }
            model.addAttribute("types", vehicleTypeRepository.findAll());
            model.addAttribute("userId", id);
            return ("add-vehicle");
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/vehicle/edit/{id}")
    public String showEditVehicleForm(@PathVariable("id") long id, Model model) {
        try {
            Vehicle vehicle = vehicleRepository.findById(id);
            if (vehicle == null) {
                throw new IllegalArgumentException("vehicle");
            }
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("types", vehicleTypeRepository.findAll());
            return "update-vehicle";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such vehicle does not exist.");
            return "vehicles-list";
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/vehicle/update/{id}")
    public String updateVehicle(@PathVariable("id") long id, @Valid Vehicle vehicle,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            vehicle.setVehId(id);
            return "update-vehicle";
        }
        try {
            vehicle.setVehId(id);
            vehicleRepository.save(vehicle);
            model.addAttribute("vehicles", vehicleRepository.findAll());
            return "vehicles-list";
        } catch (Exception e) {
            if (e.getMessage().contains("regplate")) {
                model.addAttribute("duplicateRegplate", "This registration plate is already registered");
            }
            vehicle.setVehId(id);
            model.addAttribute("types", vehicleTypeRepository.findAll());
            return "update-vehicle";
        }

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/vehicle/delete/{id}")
    public String deleteVehicle(@PathVariable("id") long id, Model model) {
        try {
            Vehicle vehicle = vehicleRepository.findById(id);
            if (vehicle == null) {
                throw new IllegalArgumentException("Invalid user Id:" + id);
            }
            vehicleRepository.delete(vehicle);
            model.addAttribute("vehicles", vehicleRepository.findAll());
            return "vehicles-list";
        } catch (Exception e) {
            model.addAttribute("error", "Such vehicle does not exist.");
            return "vehicles-list";
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/search-vehicle")
    public String showVehicleByRegplate(@RequestParam(name = "regplate", required = false) String regplate, Model model) {
        Vehicle vehicle = vehicleRepository.findByRegplate(regplate);
        if (vehicle == null) {
            model.addAttribute("error", "Nothing found.");
            return "vehicles-list";
        }
        return "redirect:/vehicle/" + String.valueOf(vehicle.getVehId()) + "/visits";
    }

}
