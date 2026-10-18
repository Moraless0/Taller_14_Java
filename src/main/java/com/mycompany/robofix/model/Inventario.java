package com.mycompany.robofix.model;

import java.util.HashMap;
import java.util.Map;

public class Inventario {
    private static Inventario instancia;
    private Map<String, Integer> repuestos;

    private Inventario() {
        repuestos = new HashMap<>();
        repuestos.put("BATERIA", 2);
        repuestos.put("MOTOR", 3);
        repuestos.put("SENSOR", 4);
    }

    public static Inventario getInstance() {
        if (instancia == null) {
            instancia = new Inventario();
        }
        return instancia;
    }

    public void mostrar() {
        for (String repuesto : repuestos.keySet()) {
            System.out.println(
                repuesto + ": " + repuestos.get(repuesto)
            );
        }
    }

    public boolean descontar(String repuesto, int cantidad) {
        if (!repuestos.containsKey(repuesto)) {
            return false;
        }
        if (cantidad <= 0) {
            return false;
        }
        int actual = repuestos.get(repuesto);
        if (actual < cantidad) {
            return false;
        }
        repuestos.put(repuesto, actual - cantidad);
        return true;
    }

    public int consultar(String repuesto) {
        return repuestos.getOrDefault(repuesto, 0);
    }
}
