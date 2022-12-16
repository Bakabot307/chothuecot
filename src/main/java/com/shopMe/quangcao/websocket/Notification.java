package com.shopMe.quangcao.websocket;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "notification")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Integer id;

  private String message;

  private Date date;
  @Enumerated
  private MessageType type;


  private boolean checked;

  private Integer targetId;
  @Nullable
  private Integer userId;

  public Integer getId() {
    return id;
  }


  public Notification() {
  }

  public Notification(String message, Date date, MessageType type, boolean checked,
      @Nullable Integer targetId,
      @Nullable Integer userId) {
    this.message = message;
    this.date = date;
    this.type = type;
    this.checked = checked;
    this.targetId = targetId;
    this.userId = userId;
  }

  public Integer getTargetId() {
    return targetId;
  }

  public void setTargetId(Integer targetId) {
    this.targetId = targetId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "Notification{" +
        "id=" + id +
        ", message='" + message + '\'' +
        ", date=" + date +
        ", type=" + type +
        ", checked=" + checked +
        ", userId=" + userId +
        '}';
  }
}
