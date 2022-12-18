package com.shopMe.quangcao.user;


import com.shopMe.quangcao.orderDetail.OrderDetailRepository;
import com.shopMe.quangcao.product.ProductStatus;
import com.shopMe.quangcao.settings.EmailSettingBag;
import com.shopMe.quangcao.settings.SettingService;
import com.shopMe.quangcao.common.Utility;
import com.shopMe.quangcao.common.Helper;
import com.shopMe.quangcao.common.MessageStrings;
import com.shopMe.quangcao.exceptions.AuthenticationFailException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

  public static final int USERS_PER_PAGE = 5;
  @Autowired
  UserRepository userRepository;


  @Autowired
  PasswordEncoder passwordEncoder;


  @Autowired
  SettingService settingService;

  @Autowired
  OrderDetailRepository orderDetailRepository;


  @Value("${site.url}")
  private String SITE_URL;

  public User save2(User user) {
    String rawPassword = user.getPassword();
    String encodedPassword = passwordEncoder.encode(rawPassword);
    user.setPassword(encodedPassword);
    return userRepository.save(user);
  }

  public User save(User user) {
    boolean isUpdatingUser = (user.getId() != null);
    if (isUpdatingUser) {
      User existingUser = userRepository.findById(user.getId()).get();

      if (user.getPassword().isEmpty() || user.getPassword() == null) {
        user.setPassword(existingUser.getPassword());
      } else {
        encodePassword(user);
      }
    }
    return userRepository.save(user);
  }

  public void updateUser(User user) {

    userRepository.save(user);
  }


  public void updateAvatar(User user) {
    userRepository.save(user);
  }

  private void encodePassword(User user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
  }

  public void sendEmail(User user, HttpServletRequest request)
      throws MessagingException, UnsupportedEncodingException {
    sendVerificationEmail(request, user);
  }

  private void sendVerificationEmail(HttpServletRequest request, User user)
      throws UnsupportedEncodingException, MessagingException {
    EmailSettingBag emailSettings = settingService.getEmailSettings();
    JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

    String toAddress = user.getEmail();
    String subject = emailSettings.getCustomerVerifySubject();
    String content = emailSettings.getCustomerVerifyContent();

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
    helper.setTo(toAddress);
    helper.setSubject(subject);

    content = content.replace("[[name]]", user.getFullName());

    String verifyURL = SITE_URL + "verify?code=" + user.getEmailVerifyCode();

    content = content.replace("[[URL]]", verifyURL);

    helper.setText(content, true);

    mailSender.send(message);

  }


  public User getById(Integer id) throws UserNotFoundException {
    try {
      return userRepository.findById(id).get();
    } catch (NoSuchElementException ex) {
      throw new UserNotFoundException(MessageStrings.USER_NOT_FOUND + id);
    }
  }

  public boolean Verify(String code) throws UserNotFoundException {
    User user = userRepository.findByEmailVerifyCode(code);
    if (user == null) {
      throw new UserNotFoundException(MessageStrings.USER_NOT_FOUND);
    }
    if (!user.isEnabled()) {
      return false;
    } else {
      userRepository.verify(user.getId());

      return true;
    }

  }

  public boolean resetPassword(String code, String newPassword) throws UserNotFoundException {
    User user = userRepository.findByEmailVerifyCode(code);
    if (user == null) {
      throw new UserNotFoundException(MessageStrings.USER_NOT_FOUND);
    }
    if (!user.isEnabled()) {
      return false;
    } else {
      userRepository.verify(user.getId());
      user.setPassword(newPassword);
      save2(user);

      return true;
    }

  }

  public User findByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);

    return user.orElse(null);

  }

  public User findByPhoneNumber(String phoneNumber) {
    Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
    return user.orElse(null);
  }

  public boolean isLogin(String phoneNumber, String password)
      throws AuthenticationFailException, UserNotFoundException {
    User user = findByPhoneNumber(phoneNumber);
    if (!Objects.nonNull(user)) {
      throw new AuthenticationFailException(MessageStrings.USER_NOT_FOUND);
    }
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new AuthenticationFailException(MessageStrings.USER_PASSWORD_WRONG);
    }
    return true;
  }

  public String updateResetPasswordToken(String email) throws UserNotFoundException {
    Optional<User> user = userRepository.findByEmail(email);

    if ((user.isPresent())) {
      String token = RandomString.make(64);
      user.get().setEmailVerifyCode(token);
      userRepository.save(user.get());

      return token;
    } else {
      throw new UserNotFoundException(MessageStrings.USER_NOT_FOUND + email);
    }
  }

//    public User getByResetPasswordCode(String token){
//        return userRepository.findByResetPasswordCode(token);
//    }

  public void updatePassword(User user, String newPassword) throws UserNotFoundException {
    if (user == null) {
      throw new UserNotFoundException(MessageStrings.USER_NOT_FOUND + "invalid token");
    }
    user.setPassword(newPassword);
    user.setEmailVerifyCode(null);
    encodePassword(user);
    userRepository.save(user);
  }

  public Page<User> listByPage(int pageNum, String sortField, String sort, String keyword,
      int usersPerPage) {

    Sort sort2 = Sort.by(sortField);
    sort2 = sort.equals("asc") ? sort2.ascending() : sort2.descending();
    Pageable pageable = PageRequest.of(pageNum - 1, usersPerPage, sort2);
    if (keyword != null) {
      return userRepository.findAll(keyword, pageable);
    }
    return userRepository.findAll(pageable);
  }

  public User findById(Integer id) throws UserNotFoundException {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("user not exist"));
  }

  public void updateUserEnabledStatus(Integer userId, boolean status) {
    userRepository.updateEnabledStatus(userId, status);
  }

  public List<User> getUserHiring() {
    return orderDetailRepository.findAll()
        .stream()
        .filter(Helper.distinctByKey(o -> o.getOrders().getUser().getId()))
        .filter(o -> o.getProduct().getStatus() == ProductStatus.HIRING)
        .toList()
        .stream()
        .map(p -> p.getOrders().getUser()).toList();

  }

  public void delete(User user) {
    userRepository.delete(user);
  }


  public void changePassword(Integer id, String password, String newPassword)
      throws UserNotFoundException {
    User user = findById(id);
    if(newPassword.length() < 8){
      throw new UserNotFoundException("Mật khẩu phải có ít nhất 8 ký tự");
    }
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new UserNotFoundException("Mật khẩu cũ không chính xác, vui lòng nhập lại");
    } else {
      user.setPassword(newPassword);
      encodePassword(user);
      userRepository.save(user);
    }
  }
}
