package ru.kamuzta.somafactorymanager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kamuzta.somafactorymanager.enums.*;
import java.io.*;
import java.util.*;

public class Roll implements Serializable, Comparable {
    public static final Logger LOGGER = LoggerFactory.getLogger(Roll.class);
    private RollType rollType;
    private Paper paper;
    private Status status;
    private Order order;
    private Machine machine;
    private float width;            // mm
    private float length;           // m
    private float core;             // mm
    private int count;              // pcs
    private float diameter;          // mm
    private float weight;           // kg

    public Roll(RollType rollType, Paper paper, float width, float core, int count, float value) {
        this.setRollType(rollType);
        this.setPaper(paper);
        this.setWidth(width);
        this.setCore(core);
        this.setCount(count);
        this.setLength(value);
        this.setDiameter(value);
        this.setWeight();
        this.setStatus(Status.NEW);
        LOGGER.info("РОЛИК СОЗДАН: " + this);
    }

    public RollType getRollType() {
        return rollType;
    }
    public void setRollType(RollType rollType) {
        this.rollType = rollType;
    }

    public Paper getPaper() {
        return paper;
    }
    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }
    public void changeStatus(Status newStatus) {
        Status oldStatus = this.getStatus();
        if (oldStatus != newStatus) {
            try {
                this.setStatus(newStatus);
                Thread.sleep(10); //обновление статуса ролики занимает 10мс
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException во время смены статуса ролика из заказа #" + this.getOrder().getNumber() + " из " + oldStatus.getStatusName() + " в " + newStatus.getStatusName());
            }
            LOGGER.info("Статус ролика из заказа #" + this.getOrder().getNumber() + " переведен из " + oldStatus.getStatusName() + " в " + newStatus.getStatusName());
        }
    }

    public float getWidth() {
        return width;
    }
    private void setWidth(float width) {
        this.width = width;
    }

    public float getCore() {
        return core;
    }
    private void setCore(float core) {
        this.core = core;
    }

    public int getCount() {
        return count;
    }
    private void setCount(int count) {
        this.count = count;
    }

    public float getLength() {
        return length;
    }
    private void setLength(float value) {
        switch (this.rollType) {
            case LENGTH:
                this.length = value;
                break;
            case DIAMETER:
                this.length = (float) Math.PI * (value*value - this.getCore()*this.getCore()) / (4 * this.paper.getPaperthickness());
                break;
        }
    }

    public float getDiameter() {
        return diameter;
    }
    private void setDiameter(float value) {
        switch (this.rollType) {
            case LENGTH:
                this.diameter = (float) Math.sqrt((4 * this.paper.getPaperthickness() * value)/ Math.PI + this.getCore()*this.getCore());
                break;
            case DIAMETER:
                this.diameter = value;
                break;
        }
    }

    public float getWeight() {
        return weight;
    }
    private void setWeight() {
        this.weight = this.getWidth() / 1000 * this.getLength() * this.paper.getPaperweight() / 1000;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public Machine getMachine() {
        return machine;
    }
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        if (this.getStatus() != Status.NEW) {
            switch (this.rollType) {
                case LENGTH:
                    return String.format("%s Roll from Order #%d %.0f x %.1fM x %.0f %.0fg/m2 %dpcs %.1fkg",
                            this.getStatus().getStatusName(),
                            this.getOrder().getNumber(),
                            this.getWidth(),
                            this.getLength(),
                            this.getCore(),
                            this.getPaper().getPaperweight(),
                            this.getCount(),
                            this.getCount() * this.getWeight());
                case DIAMETER:
                    return String.format("%s Roll from Order #%d %.0f x %.1fmm x %.0f %.0fg/m2 %dpcs %.1fkg",
                            this.getStatus().getStatusName(),
                            this.getOrder().getNumber(),
                            this.getWidth(),
                            this.getDiameter(),
                            this.getCore(),
                            this.getPaper().getPaperweight(),
                            this.getCount(),
                            this.getCount() * this.getWeight());
            }
        } else {
            switch (this.rollType) {
                case LENGTH:
                    return String.format("%s Roll %.0f x %.1fM x %.0f %.0fg/m2 %dpcs %.1fkg",
                            this.getStatus().getStatusName(),
                            this.getWidth(),
                            this.getLength(),
                            this.getCore(),
                            this.getPaper().getPaperweight(),
                            this.getCount(),
                            this.getCount() * this.getWeight());
                case DIAMETER:
                    return String.format("%s Roll %.0f x %.1fmm x %.0f %.0fg/m2 %dpcs %.1fkg",
                            this.getStatus().getStatusName(),
                            this.getWidth(),
                            this.getDiameter(),
                            this.getCore(),
                            this.getPaper().getPaperweight(),
                            this.getCount(),
                            this.getCount() * this.getWeight());
            }
        }
        return "Error in Roll.roString()";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Roll)) return false;
        Roll roll = (Roll) o;
        return Float.compare(roll.getWidth(), getWidth()) == 0 &&
                Float.compare(roll.getLength(), getLength()) == 0 &&
                Float.compare(roll.getCore(), getCore()) == 0 &&
                getCount() == roll.getCount() &&
                Float.compare(roll.getDiameter(), getDiameter()) == 0 &&
                Float.compare(roll.getWeight(), getWeight()) == 0 &&
                getRollType() == roll.getRollType() &&
                getPaper() == roll.getPaper() &&
                getStatus() == roll.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRollType(), getPaper(), getStatus(), getWidth(), getLength(), getCore(), getCount(), getDiameter(), getWeight());
    }

    @Override
    public int compareTo(Object o) {
        int x = this.getCount();
        int y = ((Roll) o).getCount();
        return Integer.compare(y, x); //reverseOrder from big to small
    }
}