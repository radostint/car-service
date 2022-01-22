package com.cp.carservice;
/**
 *
 * @author rado1
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    public List<Visit> findByVehicleVehId(long vehicleId);
}
