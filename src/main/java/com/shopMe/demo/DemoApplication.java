package com.shopMe.demo;

import com.shopMe.demo.Role.RoleService;
import com.shopMe.demo.ScheduleTask.UpdateStatus;
import com.shopMe.demo.model.Role;
import com.shopMe.demo.user.User;
import com.shopMe.demo.user.UserNotFoundException;
import com.shopMe.demo.user.UserService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
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

    User user = userService.findByPhoneNumber("+84123456789");
    if (user == null) {
      User admin = new User("admin", "quanlitru", "admin@gmail.com", "+84123456789", null,
          "123456789",
          new Date(), true);

      admin.addRole(adminRole);
      System.out.println("admin: " + admin.getId() + admin.getPassword());
      userService.save2(admin);
    }
  }

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
  public void autoSendEmail() throws UserNotFoundException {
    update.SendEmailToUserThatHasProductExpiring();
    System.out.println("sent email for products");
  }

  //add schedule for place_order
  //add schedule for wishlist
}


