    package com.cp.carservice;
/**
 *
 * @author rado1
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    public List<Vehicle> findByUserId(long userId);
    public Vehicle findById(long id);
    public Vehicle findByRegplate(String regplate);
}
