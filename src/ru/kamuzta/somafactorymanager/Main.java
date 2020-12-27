package ru.kamuzta.somafactorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        LOGGER.info("Начало работы программы!!!");
        LOGGER.info("----------------------------\n");
        Manager manager = Utils.unmarshallManager();
        LOGGER.info("----------------------------\n");
        for (Machine machine : manager.getMachineList()) {
            LOGGER.info("Восстановлен станок: " + machine);
        }
        LOGGER.info("----------------------------\n");
        manager.loadOrdersToOrderSet(Utils.getRandomOrdersArray(5));
        LOGGER.info("----------------------------\n");
        manager.loadRollsFromOrderSetToRollQueue();
        LOGGER.info("----------------------------\n");
        manager.distributeRollsToMachines();
        LOGGER.info("----------------------------\n");
        for (Machine machine : manager.getMachineList()) {
            machine.produceLoop();
        }
        LOGGER.info("----------------------------\n");
        for (Machine machine : manager.getMachineList()) {
            LOGGER.info("" + machine);
        }
        LOGGER.info("----------------------------\n");
        float totalWeight = 0.0f;
        int totalRolls = 0;
        for (Order order : manager.getOrderSet()) {
            LOGGER.info("" + order);
            for (Roll roll : order.getRollList()) {
                totalRolls += roll.getCount();
                totalWeight += roll.getWeight()*roll.getCount();
            }
        }
        LOGGER.info("----------------------------\n");
        LOGGER.info("Total Count of Rolls produced: " + totalRolls + "\n");
        LOGGER.info("Total Weight of Rolls produced: " + totalWeight + "\n");
        LOGGER.info("----------------------------\n");
    }
}
