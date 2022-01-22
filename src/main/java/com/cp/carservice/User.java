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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;       

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username", unique = true)
    @NotBlank(message = "Username is mandatory.")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Password is mandatory.")
    private String password;

    @Column(name = "first_name")
    @NotBlank(message = "First name is mandatory.")
    @Size(min = 2, message = "First name must be at least 2 characters.")
    @Pattern(regexp = "^[а-яА-Яa-zA-Z]*$", message = "Last name must not contain any special characters.")
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Last name is mandatory.")
    @Size(min = 2, message = "Last name must be at least 2 characters.")
    @Pattern(regexp = "^[а-яА-Яa-zA-Z]*$", message = "Last name must not contain any special characters.")
    private String lastName;

    @Column(name = "phone_number", unique = true)
    @NotBlank(message = "Phone number is mandatory.")
    @Size(min = 10, max = 10, message = "Phone number length must be exactly 10 digits.")
    @Pattern(regexp = "^(?=\\d{10}$)08\\d+", message = "Phone number is not properly formatted.")
    private String phoneNumber;

    @Column(name = "role")
    @NotBlank(message = "Role must be defined.")
    private String role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Vehicle> vehicles;

    public User() {
    }

    public User(long id, String username, String password, String first_name, String last_name, String phone_number, String role, List<Vehicle> vehicles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = first_name;
        this.lastName = last_name;
        this.phoneNumber = phone_number;
        this.role = role;
        this.vehicles = vehicles;
    }

    // standard constructors / setters / getters / toString
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone_number) {
        this.phoneNumber = phone_number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

}
