package ru.kamuzta.somafactorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kamuzta.somafactorymanager.enums.*;
import java.util.*;

public class Manager {
    public static final Logger LOGGER = LoggerFactory.getLogger(Manager.class);
    private String name;
    private State state;
    private List<Machine> machineList; // Хранит станки в порядке убывания FreeCapacity (обновление порядка вручную)
    private SortedSet<Order> orderSet; //Хранит заказы в порядке от старого к новому
    private Queue<Roll> rollQueue; //Хранит ролики в очереди в порядке добавления

    public Manager(String name) {
        this.setMachineList(new ArrayList<>());
        this.setOrderSet(new TreeSet<>());
        this.setRollQueue(new LinkedList<>());
        this.setName(name);
        this.setState(State.OFF);
        LOGGER.info("МЕНЕДЖЕР СОЗДАН: " + this.getName());
    }

    public String getName() {
        return name;
    }
    private void setName(String name) {
        this.name = name;
    }

    public State getState() {
        return state;
    }
    private void setState(State state) {
        this.state = state;
    }
    private void changeState(State newState) {
        State oldState = this.getState();
        try {
            this.setState(newState);
            Thread.sleep(500); //включение и выключение цеха занимает 500мс
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException во время смены состояния цеха из " + oldState.getStateName() + " в " + newState.getStateName());
        }
        LOGGER.info("Состояние цеха менеджера " + this.getName() +" переведено из " + oldState.getStateName() + " в " + newState.getStateName());
    }
    public void updateState() {
        int countOfWorkingMachines = 0;
        for (Machine machine : machineList) {
            countOfWorkingMachines += (machine.getState() == State.ON) ? 1 : 0;
        }
        if (this.getState() == State.ON && countOfWorkingMachines == 0) {
            this.changeState(State.OFF);
        } else if (this.getState() == State.OFF && countOfWorkingMachines > 0) {
            this.changeState(State.ON);
        }
    }

    public List<Machine> getMachineList() {
        return machineList;
    }
    private void setMachineList(List<Machine> machineList) {
        this.machineList = machineList;
    }
    public void loadMachinesToMachineList(Machine...machines) {
        for (Machine machine : machines) {
            machine.setManager(this);
            this.machineList.add(machine);
        }
        Collections.sort(this.machineList);
    }

    public SortedSet<Order> getOrderSet() {
        return orderSet;
    }
    public void setOrderSet(SortedSet<Order> orderSet) {
        this.orderSet = orderSet;
    }
    public void loadOrdersToOrderSet(Order...orders) {
        Arrays.sort(orders);
        for (Order order : orders) {
            order.setManager(this);
            this.orderSet.add(order);
        }
    }

    public Queue<Roll> getRollQueue() {
        return rollQueue;
    }
    public void setRollQueue(Queue<Roll> rollQueue) {
        this.rollQueue = rollQueue;
    }
    public void loadRollsFromOrderSetToRollQueue() {
        LOGGER.info("ВЫГРУЖАЕМ РОЛИКИ ИЗ ЗАКАЗОВ В СТАТУСЕ NEW В ОБЩУЮ ОЧЕРЕДЬ МЕНЕДЖЕРА " + this.getName());
        for (Order order : this.getOrderSet()) {
            if (order.getStatus() == Status.NEW) {
                this.rollQueue.addAll(order.getRollList());
                order.changeStatus(Status.QUEUED);
            }
        }
    }

    //логика распределения роликов по машинам. берем первый ролик, перебираем все машины с такой же шириной,
    // отдаем ролик машине, куда ролик в станет с наименьшим оставшимся фриспейсом
    public void distributeRollsToMachines() { //TODO продумать что делать с роликами если нет подходящего по ширине станка
        LOGGER.warn("МЕНЕДЖЕР " + this.getName() + " РАСПРЕДЕЛЯЕТ РОЛИКИ ИЗ СВОЕЙ ОЧЕРЕДИ ПО СВОБОДНЫМ СТАНКАМ");
        while (!this.getRollQueue().isEmpty()) {
            Roll plannedRoll = this.getRollQueue().poll();
            for (Machine machine : this.getMachineList()) {
                if (machine.getWidth() == plannedRoll.getWidth()) {
                    plannedRoll.changeStatus(Status.QUEUED);
                    machine.loadRollsToRollQueue(plannedRoll);
                    break;
                }
            }
            Collections.sort(this.machineList);
        }
        LOGGER.warn("НЕВОЗМОЖНО РАСПРЕДЕЛИТЬ РОЛИКИ. БОЛЬШЕ НЕТ РОЛИКОВ В ОЧЕРЕДИ У МЕНЕДЖЕРА " + this.getName());
    }


}
