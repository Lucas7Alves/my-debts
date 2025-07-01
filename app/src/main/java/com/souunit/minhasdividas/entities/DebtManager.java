package com.souunit.minhasdividas.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class DebtManager {

    // ArrayList final ainda permite alteração nos dados da lista
    private final ArrayList<FixedDebt> fixedDebts;

    public DebtManager(ArrayList<FixedDebt> fixedDebts) {
        this.fixedDebts = fixedDebts;
        //TODO: carregar dados do banco de dados
    }

    // methods
    public boolean createFixedDebt(Long id, String name, double amount, LocalDate dueDate) {
        FixedDebt fd = new FixedDebt(id, name, amount, dueDate);
        return fixedDebts.add(fd);
    }

    public boolean deleteFixedDebt(Long id) {
        return fixedDebts.removeIf(x -> Objects.equals(x.getId(), id));
    }

    public boolean updateFixedDebt(Long id, String name, double amount, LocalDate dueDate) throws IndexOutOfBoundsException {
        FixedDebt fd = new FixedDebt(id, name, amount, dueDate);
        fixedDebts.set(fixedDebts.indexOf(fd), fd);
        return true;
    }

    // getters and setters
    public ArrayList<FixedDebt> getFixedDebts() {
        return fixedDebts;
    }
}
