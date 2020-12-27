package ru.kamuzta.somafactorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kamuzta.somafactorymanager.enums.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

@XmlType(propOrder = { "id", "state", "width", "paper", "fullCapacity" })
@XmlRootElement(name = "machine")
public class Machine implements Serializable, Comparable {
    public static final Logger LOGGER = LoggerFactory.getLogger(Machine.class);
    private Manager manager;
    private int id;
    private float width;
    private Paper paper;
    private State state;
    private int fullCapacity;
    private Queue<Roll> rollQueue;

    public Machine() {
        this.setRollQueue(new LinkedList<>());
    }

    public Machine(int id, float width, Paper paper, int fullCapacity) {
        this.setRollQueue(new LinkedList<>());
        this.setId(id);
        this.setWidth(width);
        this.setPaper(paper);
        this.setFullCapacity(fullCapacity);
        this.setState(State.OFF);
        LOGGER.info("СТАНОК СОЗДАН: " + this);
    }

    public Manager getManager() {
        return manager;
    }
    @XmlTransient
    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public int getId() {
        return id;
    }
    @XmlAttribute(name = "id")
    public void setId(int id) {
        this.id = id;
    }

    public float getWidth() {
        return width;
    }
    @XmlAttribute(name = "width")
    public void setWidth(float width) {
        this.width = width;
    }

    public Paper getPaper() {
        return paper;
    }
    @XmlAttribute(name = "paper")
    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public int getFullCapacity() {
        return fullCapacity;
    }
    @XmlAttribute(name = "fullCapacity")
    public void setFullCapacity(int fullCapacity) {
        this.fullCapacity = fullCapacity;
    }
    public int getFreeCapacity() {
        int rollsInQueue = 0;
        if (this.getRollQueue().size() > 0) {
            for (Roll roll : this.getRollQueue()) {
                rollsInQueue += roll.getCount();
            }
        }
        return this.getFullCapacity() - rollsInQueue;
    }
    public int getCountRollsInQueue() {
        int rollsInQueue = 0;
        if (this.getRollQueue().size() > 0) {
            for (Roll roll : this.getRollQueue()) {
                rollsInQueue += roll.getCount();
            }
        }
        return rollsInQueue;
    }

    public State getState() {
        return state;
    }
    @XmlAttribute(name = "state")
    private void setState(State newState) {
        this.state = newState;
    }
    public void updateState() {
        if(this.getRollQueue().size() == 0) {
            this.changeState(State.OFF);
            this.getManager().updateState();
        } else if (this.getState() == State.OFF && this.getRollQueue().size() > 0){
            this.changeState(State.ON);
            this.getManager().updateState();
        }
    }
    public void changeState(State newState) {
        State oldState = this.getState();
        if (oldState != newState) {
            try {
                this.setState(newState);
                Thread.sleep(500); //включение и выключение станка занимает 500мс
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException во время смены состояния станка #" + this.getId() + " из " + oldState.getStateName() + " в " + newState.getStateName());
            }
            LOGGER.info("Состояние станка #" + this.getId() + " переведено из " + oldState.getStateName() + " в " + newState.getStateName());
        }
    }

    public Queue<Roll> getRollQueue() {
        return rollQueue;
    }
    @XmlTransient
    private void setRollQueue(Queue<Roll> rollQueue) {
        this.rollQueue = rollQueue;
    }

    public void loadRollsToRollQueue(Roll...rolls) {
        Arrays.sort(rolls);
        for (Roll roll : rolls) {
            roll.setMachine(this);
            this.rollQueue.add(roll);
            LOGGER.info("Ролик " + roll + " распределен на станок #" + this.getId());
        }
        this.updateState();
    }

    public void produceLoop() {
        while (!this.getRollQueue().isEmpty()) {
            Roll nextRoll = this.getRollQueue().peek();
            if (nextRoll.getPaper() != this.getPaper()) {
                this.changePaper(nextRoll.getPaper());
            }
            if (nextRoll.getWidth() != this.getWidth()) {
                this.changeWidth(nextRoll.getWidth());
            }
            this.produceNextRoll();
        }

    }
    private void produceNextRoll() {
        if (this.getRollQueue().size() > 0) {
            Roll nextRoll = this.getRollQueue().poll();
            try {
                nextRoll.changeStatus(Status.INPROGRESS);
                nextRoll.getOrder().updateStatus();
                Thread.sleep(nextRoll.getCount()); //1 ролик делается 1 миллисекунду
                nextRoll.changeStatus(Status.COMPLETED);
                nextRoll.getOrder().updateStatus();
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException во время производства ролика из заказа # " + nextRoll.getOrder().getNumber() + " на станке #" + this.getId());
            }
            LOGGER.info("Ролик из заказа #" + nextRoll.getOrder().getNumber() + " произведен на станке #" + this.getId());
        }
        this.updateState();
    }

    private void changePaper(Paper newPaper) {
        Paper oldPaper = this.getPaper();
        if ( oldPaper != newPaper) {
            try {
                this.setPaper(newPaper);
                Thread.sleep(1000); //замена сырья занимает 1000мс
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException во время смены сырья с" + oldPaper.getPaperSKU() + "на " + newPaper.getPaperSKU());
            }
            LOGGER.info("Произведена смена сырья с " + oldPaper.getPaperSKU() + " на " + newPaper.getPaperSKU());
        }
    }

    private void changeWidth(float newWidth) {
        float oldWidth = this.getWidth();
        if (oldWidth != newWidth) {
            try {
                this.setWidth(newWidth);
                Thread.sleep(5000); //перенастройка станка на новую ширину занимает 5000мс
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException во время перенастройки с ширины " + oldWidth + " на " + newWidth);
            }
            LOGGER.info("Произведена перенастройки с ширины " + oldWidth + " на " + newWidth);
        }
    }

    @Override
    public String toString() {
        return String.format("Machine #%d %s Width:%.0f Paper: %s Capacity(Free/InQueue/Full): %d / %d / %d",
                this.getId(),
                this.getState().getStateName(),
                this.getWidth(),
                this.getPaper().getPaperSKU(),
                this.getFreeCapacity(),
                this.getCountRollsInQueue(),
                this.getFullCapacity());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Machine)) return false;
        Machine machine = (Machine) o;
        return getId() == machine.getId() &&
                Float.compare(machine.getWidth(), getWidth()) == 0 &&
                getFullCapacity() == machine.getFullCapacity() &&
                getPaper() == machine.getPaper() &&
                getState() == machine.getState() &&
                getRollQueue().equals(machine.getRollQueue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getWidth(), getPaper(), getState(), getFullCapacity(), getRollQueue());
    }

    @Override
    public int compareTo(Object o) {
        int x = this.getFreeCapacity();
        int y = ((Machine) o).getFreeCapacity();
        return Integer.compare(y, x); //reverseOrder from big to small
    }
}
