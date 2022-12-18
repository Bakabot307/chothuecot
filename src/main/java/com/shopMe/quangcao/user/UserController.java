package com.shopMe.quangcao.user;

import com.shopMe.quangcao.amazon.AmazonS3Util;
import com.shopMe.quangcao.role.RoleService;
import com.shopMe.quangcao.security.jwt.JwtTokenUtil;
import com.shopMe.quangcao.settings.EmailSettingBag;
import com.shopMe.quangcao.settings.SettingService;
import com.shopMe.quangcao.common.Utility;
import com.shopMe.quangcao.common.ApiResponse;
import com.shopMe.quangcao.common.MessageStrings;
import com.shopMe.quangcao.exceptions.CustomException;
import com.shopMe.quangcao.role.Role;
import com.shopMe.quangcao.user.userDTO.SignInDto;
import com.shopMe.quangcao.user.userDTO.SignInResponseDto;
import com.shopMe.quangcao.user.userDTO.SignupDto;
import com.shopMe.quangcao.user.userDTO.UpdateUser;
import com.shopMe.quangcao.user.userDTO.UpdateUserDto;
import com.shopMe.quangcao.user.userDTO.UserData;
import com.shopMe.quangcao.user.userDTO.UserProfileResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.security.RolesAllowed;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UserController {

  @Autowired
  UserService userService;

  @Autowired
  AuthenticationManager authManager;
  @Autowired
  JwtTokenUtil jwtUtil;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  SettingService settingService;

  @Autowired
  RoleService roleService;

  @Autowired
  private EntityManager entityManager;
  @Value("${site.url}")
  private String SITE_URL;


  @PostMapping(value = {"/signup"})
  public ResponseEntity<?> Signup(
      @RequestBody @Valid SignupDto signupDto,
      HttpServletRequest request
  ) throws CustomException, MessagingException, UnsupportedEncodingException {

    if (Objects.nonNull(userService.findByEmail(signupDto.getEmail()))) {
      throw new CustomException("Email đã tồn tại");
    }

    if (Objects.nonNull(userService.findByPhoneNumber(signupDto.getPhoneNumber()))) {
      throw new CustomException("Số điện thoại đã tồn tại");
    }
    String encryptedPassword;
    encryptedPassword = passwordEncoder.encode(signupDto.getPassword());
    User user = new User(signupDto.getFirstName(), signupDto.getLastName(), signupDto.getEmail(),
        signupDto.getPhoneNumber(), encryptedPassword);
    user.setCreatedDate(new Date());
    user.setEnabled(true);
    user.setEmailVerified(false);
    Role role = roleService.getRoleByName("ROLE_USER");
    user.addRole(role);
    String randomCode = RandomString.make(64);
    user.setEmailVerifyCode(randomCode);
    user.setAvatar(null);
    User savedUser = userService.save(user);
    if (Objects.nonNull(savedUser)) {
      userService.sendEmail(user, request);
      return new ResponseEntity<>(new ApiResponse(true, "Đã đăng ký thành công!"),
          HttpStatus.CREATED);
    } else {
      return new ResponseEntity<>("Đã sảy ra lỗi khi tạo tài khoản mới!!", HttpStatus.BAD_REQUEST);
    }
  }


  @PostMapping("/login")
  public ResponseEntity<?> Login(@RequestBody @Valid SignInDto request) {
    try {
      Authentication authentication = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getPhoneNumber(), request.getPassword()));

      User user = (User) authentication.getPrincipal();
      String accessToken = jwtUtil.generateAccessToken(user);
      String refreshToken = jwtUtil.generateRefreshToken(user);
      SignInResponseDto response = new SignInResponseDto(accessToken, refreshToken);
      return ResponseEntity.ok().body(response);
    } catch (BadCredentialsException ex) {
      return new ResponseEntity<>(new ApiResponse(false, MessageStrings.USER_INFO_NOT_MATCH),
          HttpStatus.BAD_REQUEST);
    }


  }

  @GetMapping("/verify")
  public ResponseEntity<?> Verify(@RequestParam String code) {
    boolean verified = false;
    try {
      verified = userService.Verify(code);
    } catch (UserNotFoundException e) {
      return new ResponseEntity<>(MessageStrings.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (verified) {
      return new ResponseEntity<>(new ApiResponse(true, "Verified successfully!"), HttpStatus.OK);
    }
    return new ResponseEntity<>("failed to verify", HttpStatus.BAD_REQUEST);
  }

  @PutMapping("/reset_password")
  public ResponseEntity<?> resetPassword(@RequestParam String token,
      @RequestParam String newPassword) {
    boolean reseted = false;
    try {
      reseted = userService.resetPassword(token, newPassword);
    } catch (UserNotFoundException e) {
      return new ResponseEntity<>(MessageStrings.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (reseted) {
      return new ResponseEntity<>(new ApiResponse(true, "Đã đổi mật khẩu thành công"),
          HttpStatus.OK);
    }
    return new ResponseEntity<>("Đổi mật khẩu thất bại", HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/forget_password")
  public ResponseEntity<?> forgotPassword(@RequestParam String email) {
    try {
      String token = userService.updateResetPasswordToken(email);
      String link = SITE_URL + "/auth/reset_password?token=" + token;
      try {
        sendEmail(link, email);
      } catch (MessagingException | UnsupportedEncodingException e) {
        return new ResponseEntity<>("Failed to send request", HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity<>(new ApiResponse(true, "Sent request successfully!"),
          HttpStatus.OK);
    } catch (UserNotFoundException e) {
      return new ResponseEntity<>(MessageStrings.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
    }


  }

  private void sendEmail(String link, String email)
      throws MessagingException, UnsupportedEncodingException {
    EmailSettingBag emailSetting = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSetting);

    String toAddress = email;
    String subject = "Here's the link to reset your password";

    String content = "<p>Hello, </p>"
        + "<p>You have requested to reset your password.</p>"
        + "<p>Click the link below to change your password:</p>"
        + "<p><a href=\"" + link + "\">Change your password</a></p>"
        + "<br>"
        + "<p>Ignore this email if you remember your password, "
        + "or you have not made the request.</p ";

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(emailSetting.getFromAddress(), emailSetting.getSenderName());
    helper.setTo(toAddress);
    helper.setSubject(subject);
    helper.setText(content, true);
    mailSender.send(message);
  }

  @PostMapping("/token/refresh")
  public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        String refreshToken = authorizationHeader.substring("Bearer ".length());
        User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(user1);

        SignInResponseDto responseAuth = new SignInResponseDto(accessToken, refreshToken);
        return ResponseEntity.ok().body(responseAuth);
      } catch (BadCredentialsException ex) {
        return new ResponseEntity<>("something is wrong with refresh token",
            HttpStatus.BAD_REQUEST);
      }
    } else {
      return new ResponseEntity<>("Refresh token", HttpStatus.BAD_REQUEST);
    }

  }

  @PutMapping("/user/edit")
  @RolesAllowed("ROLE_USER")

  public ResponseEntity<ApiResponse> editUser(
      @RequestBody @Valid UpdateUserDto updateUserDto,
      HttpServletRequest request
  ) throws UserNotFoundException, MessagingException, UnsupportedEncodingException, CustomException {

    User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User updatingUser = userService.getById(user1.getId());

    if (Objects.nonNull(userService.findByEmail(updateUserDto.getEmail())) && !updatingUser.getEmail().equals(updateUserDto.getEmail())) {
      throw new CustomException("Email đã tồn tại");
    }
    updatingUser.Update(updateUserDto);
    if(!Objects.equals(updatingUser.getEmail(), updateUserDto.getEmail())) {
      String randomCode = RandomString.make(64);
      updatingUser.setEmailVerifyCode(randomCode);
      updatingUser.setEmailVerified(false);
      userService.sendEmail(updatingUser, request);

    }
    userService.updateUser(updatingUser);
    return new ResponseEntity<>(new ApiResponse(true, "Updated user successfully!"), HttpStatus.OK);
  }


  @PutMapping("/user/avatar")
  @RolesAllowed("ROLE_USER")
  public ResponseEntity<ApiResponse> editAvatar(
      @RequestParam("image") MultipartFile multipartFile)
      throws IOException, UserNotFoundException {

    User user1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User updatingUser = userService.getById(user1.getId());
    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(
          Objects.requireNonNull(multipartFile.getOriginalFilename()));
      updatingUser.setAvatar(fileName);
      String uploadDir = "user-photos/" + updatingUser.getId();
      AmazonS3Util.removeFolder(uploadDir);
      AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
    } else {
      if (updatingUser.getAvatar().isEmpty()) {
        updatingUser.setAvatar(null);
      }
    }
    userService.updateAvatar(updatingUser);
    return new ResponseEntity<>(new ApiResponse(true, "Updated avatar successfully!"),
        HttpStatus.OK);
  }

  @RolesAllowed("ROLE_USER")
  @GetMapping("user/profile")
  public ResponseEntity<UserProfileResponse> userProfile() throws UserNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User userDB = userService.getById(user.getId());
    UserProfileResponse userDto = new UserProfileResponse(userDB);

    return new ResponseEntity<>(userDto, HttpStatus.OK);
  }

  @RolesAllowed("ROLE_USER")
  @GetMapping("user/profile/change_password")
  public ResponseEntity<ApiResponse> changePassword(@RequestParam String oldPassword,
      @RequestParam String newPassword)
      throws UserNotFoundException {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    userService.changePassword(user.getId(), oldPassword, newPassword);

    return new ResponseEntity<>(new ApiResponse(true, "Updated password successfully!"),
        HttpStatus.OK);
  }

  @GetMapping("/admin/user")
  public ResponseEntity<Page<UserData>> listFirstPage() {
    Page<User> user = userService.listByPage(1, "firstName", "asc", null, 5);
    Page<UserData> dtos = user.map(this::convertToObjectDto);
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  @GetMapping("/admin/user/page/{pageNum}")
  public ResponseEntity<Page<UserData>> getUsers(
      @PathVariable(name = "pageNum") int pageNum,
      @RequestParam("sortField") String sortField,
      @RequestParam("sort") String sort,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam("usersPerPage") int usersPerPage

  ) {
    Page<User> userDB = userService.listByPage(pageNum, sortField, sort, keyword, usersPerPage);
    Page<UserData> dtos = userDB.map(this::convertToObjectDto);
    return new ResponseEntity<>(dtos, HttpStatus.OK);
  }

  private UserData convertToObjectDto(User o) {
    UserData dto = new UserData(o);
    //conversion here
    return dto;
  }

  @PutMapping("/admin/user/update")
  public ResponseEntity<ApiResponse> updateUser(@RequestBody UpdateUser updateUser)
      throws UserNotFoundException {
    User updatingUser = userService.getById(updateUser.getId());
    updatingUser.setFirstName(updateUser.getFirstName());
    updatingUser.setLastName(updateUser.getLastName());
    updatingUser.setEmail(updateUser.getEmail());
    updatingUser.setPhoneNumber(updateUser.getPhoneNumber());
    return new ResponseEntity<>(new ApiResponse(true, "updated user successfully"), HttpStatus.OK);
  }

  @PutMapping("/admin/user/{userId}/update")
  public ResponseEntity<?> updateRole(@PathVariable("userId") Integer userId,
      @RequestParam("roleName") String roleName
  ) throws UserNotFoundException {
    User user = userService.findById(userId);
    Role role = roleService.getRoleByName(roleName);
    if (user == null || role == null) {
      return new ResponseEntity<>("user id or role name is not exist. ROLE_USER or USER_ADMIN",
          HttpStatus.BAD_REQUEST);
    }
    if (user.getRoles().contains(role)) {
      return new ResponseEntity<>("Vai trò này đã có", HttpStatus.BAD_REQUEST);
    }
    user.setRoles(new HashSet<>());
    user.addRole(role);
    userService.updateUser(user);

    return new ResponseEntity<>(
        new ApiResponse(true, "updated " + roleName + " for user " + userId), HttpStatus.OK);

  }

  @PutMapping("/admin/user/{userId}/{status}")
  public ResponseEntity<ApiResponse> updateUserEnabledStatus(
      @PathVariable("userId") Integer userId,
      @PathVariable("status") boolean status
  ) {
    String message = status ? "enabled" : "disabled";
    userService.updateUserEnabledStatus(userId, status);
    return new ResponseEntity<>(new ApiResponse(true, "user has been " + message), HttpStatus.OK);
  }


  @GetMapping("/admin/user/hiring")
  public ResponseEntity<List<UserData>> getUserHiring() {
    List<User> list = userService.getUserHiring();
    List<UserData> userList = new ArrayList<>();
    for (User user : list) {
      UserData userData = new UserData(user);
      userList.add(userData);
    }
    return ResponseEntity.ok().body(userList);
  }
}
