package com.cp.carservice;
/**
 *
 * @author rado1
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MechanicController {

    @Autowired
    MechanicRepository mechanicRepository;

    @GetMapping("/mechanics/all")
    public String showAllMechanics(Model model) {
        model.addAttribute("mechanics", mechanicRepository.findAll());
        return "mechanics-list";
    }

}
