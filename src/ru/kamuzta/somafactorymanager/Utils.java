package ru.kamuzta.somafactorymanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kamuzta.somafactorymanager.enums.*;

import java.util.Arrays;

public class Utils {
    public static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static Roll[] getRandomRollsArray(int pcs) {
        LOGGER.info("Создание массива случайных " + pcs +" роликов...");
        Roll[] rolls = new Roll[pcs];
        for (int i = 0; i < rolls.length; i++) {
            rolls[i] = getRandomRoll();
        }
        Arrays.sort(rolls);
        return rolls;
    }
    public static Roll getRandomRoll() {
        RollType rollType = Math.random() > 0.5d ? RollType.LENGTH : RollType.DIAMETER;
        Paper paper;
        switch ((int) (Math.random() * 3)) {
            case 0 :
                paper = Paper.NTC44;
                break;
            case 1 :
                paper = Paper.NTC48;
                break;
            case 2 :
                paper = Paper.NTC55;
                break;
            case 3 :
                paper = Paper.NTC58;
                break;
            default:
                LOGGER.error("Ошибка в определении сырья при создании ролика, выбрано сырье по умолчанию.");
                paper = Paper.NTC44;
        }
        float width = Math.random() > 0.5d ? 57.0f : 80.0f;
        float core = (width == 57.0f) ? 12.0f : ((Math.random() > 0.5d) ? 18.0f : 26.0f);
        int count = (int) (Math.random() * 10000);
        float value;
        switch (rollType) {
            case LENGTH:
                value = width == 57.0f ? 10.0f + (float) (Math.random() * 80) : 30.0f + (float) (Math.random() * 200);
                break;
            case DIAMETER:
                value = width == 57.0f ? core + 10.0f + (float) (Math.random() * 80) : core + 10.0f + (float) (Math.random() * 200);
                break;
            default:
                LOGGER.error("Ошибка в определении типа ролика, выбран тип ролика по умолчанию и value - длина.");
                value = width == 57.0f ? 10.0f + (float) (Math.random() * 80) : 30.0f + (float) (Math.random() * 200);
        }
        Roll newRoll = new Roll(rollType, paper, width, core, count, value);
        return newRoll;
    }

    public static Order[] getRandomOrdersArray(int count) {
        LOGGER.info("Создание массива случайных " + count +" заказов...");
        Order[] orders = new Order[count];
        for (int i = 0; i < orders.length; i++) {
            orders[i] = getRandomOrder();
        }
        Arrays.sort(orders);
        return orders;
    }
    public static Order getRandomOrder() {
        int pcs = (int) (1 + Math.random() * 9);
        String name;
        switch ((int) (Math.random() * 3)) {
            case 0 :
                name = "Komus";
                break;
            case 1 :
                name = "SamsonOpt";
                break;
            case 2 :
                name = "deVente";
                break;
            case 3 :
                name = "RelefOpt";
                break;
            default:
                LOGGER.error("Ошибка в определении клиента. Выбран клиент по умолчанию.");
                name = "Komus";
        }
        Order newOrder = new Order(name, getRandomRollsArray(pcs));
        return newOrder;
    }

    public static void randomizeStatus(Order order) {
        LOGGER.info("Меняем случайным образом статусы роликов в заказе " + order.getNumber() + "...");
        for (Roll roll : order.getRollList()) {
            if (Math.random() > 0.95d) {
                roll.setStatus(Status.COMPLETED);
            } else if (Math.random() > 0.80d) {
                roll.setStatus(Status.INPROGRESS);
            }
        }
        order.updateStatus();
    }

    public static Machine[] getRandomMachinesArray(int count) {
        LOGGER.info("Создание массива случайных " + count +" станков...");
        Machine[] machines = new Machine[count];
        for (int i = 0; i < machines.length; i++) {
            machines[i] = getRandomMachine();
        }
        Arrays.sort(machines);
        return machines;
    }

    public static Machine getRandomMachine() {
        int id = (int) (1.0d + Math.random() * 99.0d);
        float width = Math.random() > 0.5d ? 57.0f : 80.0f;
        Paper paper;
        switch ((int) (Math.random() * 3)) {
            case 0 :
                paper = Paper.NTC44;
                break;
            case 1 :
                paper = Paper.NTC48;
                break;
            case 2 :
                paper = Paper.NTC55;
                break;
            case 3 :
                paper = Paper.NTC58;
                break;
            default:
                LOGGER.error("Ошибка в определении сырья при создании станка, выбрано сырье по умолчанию.");
                paper = Paper.NTC44;
        }
        int fullCapacity = (int) (15000.0d + Math.random() * 10000.0d);
        Machine newMachine = new Machine(id, width, paper, fullCapacity);
        return newMachine;
    }


}
