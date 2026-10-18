package com.mycompany.robofix.model;

import com.mycompany.robofix.patterns.memento.RobotMemento;

public class Robot {
    public static final int ENERGIA_MIN = 0;
    public static final int ENERGIA_MAX = 100;
    public static final String MODO_NORMAL = "NORMAL";
    public static final String MODO_AHORRO = "AHORRO";
    public static final String MODO_MANTENIMIENTO = "MANTENIMIENTO";

    private String codigo;
    private String nombre;
    private int energia;
    private String modo;

    public Robot(String codigo, String nombre, int energia, String modo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.energia = energia;
        this.modo = modo;
    }

    public void mostrar() {
        System.out.println("Codigo: " + codigo);
        System.out.println("Nombre: " + nombre);
        System.out.println("Energia: " + energia);
        System.out.println("Modo: " + modo);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEnergia() {
        return energia;
    }

    public boolean setEnergia(int energia) {
        if (energia < ENERGIA_MIN || energia > ENERGIA_MAX) {
            return false;
        }
        this.energia = energia;
        return true;
    }

    public String getModo() {
        return modo;
    }

    public boolean setModo(String modo) {
        if (!modo.equals(MODO_NORMAL) && !modo.equals(MODO_AHORRO) && !modo.equals(MODO_MANTENIMIENTO)) {
            return false;
        }
        this.modo = modo;
        return true;
    }

    public RobotMemento guardarEstado() {
        return new RobotMemento(energia, modo);
    }

    public void restaurarEstado(RobotMemento memento) {
        this.energia = memento.getEnergia();
        this.modo = memento.getModo();
    }
}
