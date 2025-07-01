package com.souunit.minhasdividas.entities;

import java.time.LocalDate;

public class FixedDebt {

    private Long id;
    private String name;
    private double amount; // valor da conta
    private LocalDate dueDate; // data de vencimento

    public FixedDebt(Long id, String name, double amount, LocalDate dueDate) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.dueDate = dueDate;
    }

    public boolean isLimitDay(LocalDate currentDate) {
        return currentDate.equals(dueDate);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
