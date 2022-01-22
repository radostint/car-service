package com.cp.carservice;
/**
 *
 * @author rado1
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByPhoneNumber(String phoneNumber);
    public User findByUsername(String username);
}
