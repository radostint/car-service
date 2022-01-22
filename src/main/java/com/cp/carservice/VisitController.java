package com.cp.carservice;

/**
 *
 * @author rado1
 */
import java.util.Comparator;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class VisitController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    VisitRepository visitRepository;

    @GetMapping("/vehicle/{id}/visits")
    public String showVehicleVisits(@PathVariable("id") long id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            Vehicle vehicle = vehicleRepository.findById(id);
            if (vehicle == null) {
                if (user.getRole().equals("ROLE_ADMIN")) {
                    throw new IllegalArgumentException("Invalid vehicle id: " + id);
                } else {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "entity not found"
                    );
                }
            }
            if (!user.getRole().equals("ROLE_ADMIN") && user.getId() != vehicle.getUser().getId()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "entity not found"
                );
            }
            model.addAttribute("vehicle", vehicle);
            List<Visit> visits = visitRepository.findByVehicleVehId(id);
            visits.sort(Comparator.comparing(Visit::getDate));
            model.addAttribute("visits", visits);
            return "vehicle-visits";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such vehicle does not exist.");
            return "vehicles-list";
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/vehicle/{id}/visits/add")
    public String showAddVisitForm(@PathVariable("id") long id, Visit visit, Model model) {
        try {
            if (vehicleRepository.findById(id) == null) {
                throw new IllegalArgumentException("problem");
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such visit does not exist.");
            return "visit-list";
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setVehId(id);
        visit.setVehicle(vehicle);
        model.addAttribute("vehicleId", id);
        return "add-visit";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/vehicle/{id}/visits/add")
    public String addVisit(@PathVariable("id") long id, @Valid Visit visit, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vehicleId", id);
            return "add-vehicle";
        }
        try {
            System.out.println(visit.getVehicle().getVehId());
            visitRepository.save(visit);
            model.addAttribute("vehicles", vehicleRepository.findAll());
            return "redirect:/vehicle/" + String.valueOf(id) + "/visits";
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
    @GetMapping("visit/edit/{id}")
    public String showEditVisitForm(@PathVariable("id") long id, Model model) {
        try {
            Visit visit = visitRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
            model.addAttribute("visit", visit);
            Long a = visit.getVisId();
            if (a != 0 && a != null) {
                System.out.println("UPDATE //GET " + a);
            } else {
                System.out.println("Id is null!");
            }

            model.addAttribute("vehicleId", visit.getVehicle().getVehId());
            return "update-visit";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Such visit does not exist.");
            model.addAttribute("vehicles", vehicleRepository.findAll());
            return "vehicles-list";
        }
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/visit/update/{id}")
    public String updateVisit(@PathVariable("id") long id, @Valid Visit visit,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            visit.setVisId(id);
            return "update-visit";
        }
        try {
            visit.setVisId(id);
            visitRepository.save(visit);
            return "redirect:/vehicle/" + String.valueOf(visit.getVehicle().getVehId()) + "/visits";
        } catch (Exception e) {
            visit.setVisId(id);
            model.addAttribute("error", "Something went wrong. Please try again!");
            return "update-visit";
        }

    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/visit/delete/{id}")
    public String deleteVisit(@PathVariable("id") long id, Model model) {
        try {

            Visit visit = visitRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid visit Id:" + id));
            long vehicleId = visit.getVehicle().getVehId();
            visitRepository.delete(visit);
            return "redirect:/vehicle/" + String.valueOf(vehicleId) + "/visits";
        } catch (Exception e) {
            model.addAttribute("error", "Such visit does not exist.");
            return "vehicles-list";
        }
    }
}
