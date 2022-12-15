package com.shopMe.quangcao.scheduleTask;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;


@Component
public class
ScheduledTasks {

  private final TaskScheduler taskScheduler;

  UpdateOrderStatusTask task1;

  @Autowired
  public ScheduledTasks(TaskScheduler taskScheduler, UpdateOrderStatusTask task1) {
    this.taskScheduler = taskScheduler;
    this.task1 = task1;
  }

  public ScheduledFuture<?> scheduleTasks(Integer id) {
    PeriodicTrigger periodicTrigger = new PeriodicTrigger(60 * 60, TimeUnit.SECONDS);
    periodicTrigger.setInitialDelay(10);
    return taskScheduler.schedule(task1.test(id), periodicTrigger);
  }
}