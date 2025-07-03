package com.souunit.minhasdividas.entities;

import java.time.LocalDate;
import java.util.Map;

public class DebtManager {

    // ArrayList final ainda permite alteração nos dados da lista
    private final Map<Long, FixedDebt> fixedDebts;
    private static Long id = 0L;

    public DebtManager(Map<Long, FixedDebt> fixedDebts) {
        this.fixedDebts = fixedDebts;
        //TODO: carregar dados do banco de dados
    }

    // methods
    public boolean putFixedDebt(String name, double amount, LocalDate dueDate) throws IllegalArgumentException {
        id++;
        FixedDebt fd = new FixedDebt(id, name, amount, dueDate);
        fixedDebts.put(fd.getId(), fd);
        return true;
    }

    public boolean deleteFixedDebt(Long id) {
        fixedDebts.remove(id);
        return true;
    }

    public boolean updateFixedDebt(Long id, String name, double amount, LocalDate dueDate) throws IndexOutOfBoundsException {
        FixedDebt fd = new FixedDebt(id, name, amount, dueDate);
        fixedDebts.put(fd.getId(), fd);
        return true;
    }

    // getters and setters
    public Map<Long, FixedDebt> getFixedDebts() {
        return fixedDebts;
    }
}
