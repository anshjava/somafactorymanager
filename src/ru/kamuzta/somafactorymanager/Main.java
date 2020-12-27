package ru.kamuzta.somafactorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        LOGGER.info("Начало работы программы!!!");
        Manager manager = new Manager("Andrey");
        LOGGER.info("----------------------------\n");
        manager.loadMachinesToMachineList(Utils.getRandomMachinesArray(5));
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

        for (Machine machine : manager.getMachineList()) {
            LOGGER.info("" + machine);
        }

        LOGGER.info("----------------------------\n");

        for (Order order : manager.getOrderSet()) {
            LOGGER.info("" + order);
        }

        LOGGER.info("----------------------------\n");

        LOGGER.info("Нераспределенных роликов в очереди у менеджера: " + manager.getRollQueue().size());



    }
}
