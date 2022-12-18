package com.shopMe.quangcao;

import com.shopMe.quangcao.role.RoleService;
import com.shopMe.quangcao.scheduleTask.UpdateStatus;
import com.shopMe.quangcao.role.Role;
import com.shopMe.quangcao.user.User;
import com.shopMe.quangcao.user.UserNotFoundException;
import com.shopMe.quangcao.user.UserService;
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
public class QuangCaoApplication {

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
  public QuangCaoApplication(RoleService roleService, UpdateStatus update, UserService userService,
      PasswordEncoder passwordEncoder, SimpMessagingTemplate simpMessagingTemplate) {
    this.roleService = roleService;
    this.update = update;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.simpMessagingTemplate = simpMessagingTemplate;

  }


  public static void main(String[] args) {
    //main
    SpringApplication.run(QuangCaoApplication.class, args);
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
    User user = userService.findByPhoneNumber(adminPhone);
    if (user == null) {
      User admin = new User("admin", "quanlitru", adminEmail, adminPhone, null,
          "123456789",
          new Date(), true);

      admin.addRole(adminRole);
      userService.save2(admin);
    }
  }
//every 1AM
  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Ho_Chi_Minh")
  public void autoSendEmail() throws UserNotFoundException {

    //send mail to user có product sắp hết hạn
    update.SendEmailToUserThatHasProductExpiring();
    //send mail to user có product trong wishlist có thể đặt
    update.SendEmailToUserThatHasProductInWishlist();
    //hủy đơn hàng
    update.cancelOrder();
  }

//  //test every 1min
//  @Scheduled(cron = "* * * * *", zone = "Asia/Ho_Chi_Minh")
//  public void autoSendEmailTest() throws UserNotFoundException {
//    System.out.println("autoSendEmail");
//
//    //send mail to user có product sắp hết hạn
//    update.SendEmailToUserThatHasProductExpiring();
//    //send mail to user có product trong wishlist có thể đặt
//    update.SendEmailToUserThatHasProductInWishlist();
//    //hủy đơn hàng
//    update.cancelOrder();
//  }


}


