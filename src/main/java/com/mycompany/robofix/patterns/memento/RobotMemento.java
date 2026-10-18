package com.mycompany.robofix.patterns.memento;

public class RobotMemento {
    private final int energia;
    private final String modo;

    public RobotMemento(int energia, String modo) {
        this.energia = energia;
        this.modo = modo;
    }

    public int getEnergia() {
        return energia;
    }

    public String getModo() {
        return modo;
    }
}
