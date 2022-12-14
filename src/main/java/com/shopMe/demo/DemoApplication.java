package com.shopMe.demo;

import com.shopMe.demo.Role.RoleService;
import com.shopMe.demo.ScheduleTask.UpdateStatus;
import com.shopMe.demo.Role.Role;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserNotFoundException;
import com.shopMe.demo.user.UserService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

  SimpMessagingTemplate simpMessagingTemplate;
  UpdateStatus update;
  RoleService roleService;

  UserService userService;

  PasswordEncoder passwordEncoder;

  @Value("${admin.email}")
  private String adminEmail;

  @Value("${admin.phone}")
  private String adminPhone;


  @Autowired
  public DemoApplication(RoleService roleService, UpdateStatus update, UserService userService,
      PasswordEncoder passwordEncoder, SimpMessagingTemplate simpMessagingTemplate) {
    this.roleService = roleService;
    this.update = update;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.simpMessagingTemplate = simpMessagingTemplate;

  }

  public static void main(String[] args) {
    //change SystemTime to UTC
//    String instantExpected = "2014-12-22T10:15:30Z";
//    Clock clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"));
//    Instant instant = Instant.now(clock);
//    System.out.println(instant.toString());
    //main
    SpringApplication.run(DemoApplication.class, args);
  }


  @Bean
  public void addDefaultRoles() {
    Role userRole = roleService.getRoleByName("ROLE_USER");
    Role adminRole = roleService.getRoleByName("ROLE_ADMIN");

    if (userRole == null) {
      userRole = roleService.save(new Role("ROLE_USER"));
    }
    if (adminRole == null) {
      adminRole = roleService.save(new Role("ROLE_ADMIN"));
    }
    System.out.println(adminEmail);
    User user = userService.findByPhoneNumber(adminPhone);
    if (user == null) {
      User admin = new User("admin", "quanlitru", adminEmail, adminPhone, null,
          "123456789",
          new Date(), true);

      admin.addRole(adminRole);
      userService.save2(admin);
    }
  }

  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Ho_Chi_Minh")
  public void autoSendEmail() throws UserNotFoundException {
    update.SendEmailToUserThatHasProductExpiring();
    update.SendEmailToUserThatHasProductInWishlist();
    update.cancelOrder();
  }


  //add schedule for wishlist
}


