package ru.kamuzta.somafactorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kamuzta.somafactorymanager.enums.*;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class Order implements Serializable, Comparable {
    public static final Logger LOGGER = LoggerFactory.getLogger(Order.class);
    private int number;
    private String clientName;
    private LocalDateTime date;
    private List<Roll> rollList;
    private Status status;
    private Manager manager;

    public Order(String clientName, Roll... rolls) {
        this.setRollList(new ArrayList<Roll>());
        this.setNumber();
        this.setClientName(clientName);
        this.setDate();
        this.loadRollsToRollList(rolls);
        this.setStatus(Status.NEW);
        LOGGER.info("ЗАКАЗ СОЗДАН: " + this);
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber() {
        this.number = (int) (Math.random() * 10000);
    } //set random Number

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate() {
        this.date = LocalDateTime.now();
    }

    public List<Roll> getRollList() {
        return rollList;
    }

    public void setRollList(List<Roll> rollList) {
        this.rollList = rollList;
    }

    public void loadRollsToRollList(Roll... rolls) {
        for (Roll roll : rolls) {
            roll.setOrder(this);
            this.rollList.add(roll);
        }
        Collections.sort(this.rollList);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void updateStatus() {
        Map<Status, Integer> statusMap = new TreeMap<>();
        for (Roll roll : this.getRollList()) {
            if (statusMap.containsKey(roll.getStatus())) {
                statusMap.put(roll.getStatus(), statusMap.get(roll.getStatus()) + 1);
            } else {
                statusMap.put(roll.getStatus(), 1);
            }
        }

        if (statusMap.size() == 1 && statusMap.containsKey(Status.NEW)) {
            this.changeStatus(Status.NEW);
        } else if (statusMap.size() == 1 && statusMap.containsKey(Status.COMPLETED)) {
            this.changeStatus(Status.COMPLETED);
        } else {
            this.changeStatus(Status.INPROGRESS);
        }
    }

    public void changeStatus(Status newStatus) {
        Status oldStatus = this.getStatus();
        if (oldStatus != newStatus) {
            try {
                this.setStatus(newStatus);
                Thread.sleep(100); //обновление статуса заказа занимает 100мс
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException во время смены статуса заказа #" + this.getNumber() + " из " + oldStatus.getStatusName() + " в " + newStatus.getStatusName());
            }
            LOGGER.info("Состояние заказа #" + this.getNumber() + " переведено из " + oldStatus.getStatusName() + " в " + newStatus.getStatusName());
        }
    }


    @Override
    public String toString() {
        int totalRollsCount = 0;
        float totalRollsWeight = 0.0f;
        for (Roll roll : this.getRollList()) {
            totalRollsCount += roll.getCount();
            totalRollsWeight += roll.getWeight() * roll.getCount();
        }
        return String.format("Order #%d %s %s Client: %s Rolls:%dpcs Weight:%.1fkg",
                this.getNumber(),
                this.getStatus().getStatusName(),
                this.getDate().format(DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss")),
                this.getClientName(),
                totalRollsCount,
                totalRollsWeight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getNumber() == order.getNumber() &&
                getClientName().equals(order.getClientName()) &&
                getDate().equals(order.getDate()) &&
                getRollList().equals(order.getRollList()) &&
                getStatus() == order.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getClientName(), getDate(), getRollList(), getStatus());
    }

    @Override
    public int compareTo(Object o) {
        return this.date.compareTo(((Order) o).date);
    }

}
