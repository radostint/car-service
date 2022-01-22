package com.cp.carservice;
/**
 *
 * @author rado1
 */
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long vehId;

    @Column(name = "make", unique = true)
    @NotBlank(message = "Make is mandatory")
    private String make;

    @Column(name = "model")
    @NotBlank(message = "Model is mandatory")
    private String model;

    @Column(name = "regplate")
    @NotBlank(message = "Registration plate is mandatory")
    private String regplate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private VehicleType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vehicle")
    private List<Visit> visits;

    public Vehicle() {

    }

    public Vehicle(long vehId, String make, String model, VehicleType type, User user, List<Visit> visits) {
        this.vehId = vehId;
        this.make = make;
        this.model = model;
        this.type = type;
        this.user = user;
        this.visits = visits;
    }

    public long getVehId() {
        return vehId;
    }

    public void setVehId(long vehId) {
        this.vehId = vehId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRegplate() {
        return regplate;
    }

    public void setRegplate(String regplate) {
        this.regplate = regplate;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void setVisits(List<Visit> visits) {
        this.visits = visits;
    }

}
