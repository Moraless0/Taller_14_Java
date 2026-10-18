package com.mycompany.robofix.service;

import com.mycompany.robofix.model.Inventario;
import com.mycompany.robofix.model.Robot;
import com.mycompany.robofix.patterns.adapter.Diagnostico;
import com.mycompany.robofix.patterns.adapter.ResultadoDiagnostico;

public class Taller {
    private Inventario inventario;
    private Diagnostico diagnostico;

    public Taller(Diagnostico diagnostico) {
        this.inventario = Inventario.getInstance();
        this.diagnostico = diagnostico;
    }

    public void diagnosticar(Robot robot) {
        ResultadoDiagnostico resultado = diagnostico.obtenerResultado(robot.getCodigo());

        if (resultado == null || !resultado.isValid()) {
            System.out.println("\n--- DIAGNOSTICO ---");
            System.out.println("Error: No se pudo obtener un diagnostico valido.");
            return;
        }

        System.out.println("\n--- DIAGNOSTICO ---");
        System.out.println("Robot: " + resultado.getRobotCode());
        System.out.println("Componente: " + resultado.getComponent());
        System.out.println("Estado: " + resultado.getStatus());
        System.out.println("Consumo reportado: " + resultado.getReportedValue());
    }

    public boolean reparar(Robot robot) {
        ResultadoDiagnostico resultado = diagnostico.obtenerResultado(robot.getCodigo());

        if (resultado == null || !resultado.isValid()) {
            System.out.println("Error: Diagnostico invalido. No se puede reparar.");
            return false;
        }

        String repuesto = resultado.getComponent();

        if (!inventario.descontar(repuesto, 1)) {
            System.out.println("Error: No hay suficientes repuestos de " + repuesto + " para reparar.");
            return false;
        }

        int nuevaEnergia = robot.getEnergia() - 20;
        if (nuevaEnergia < Robot.ENERGIA_MIN) {
            nuevaEnergia = Robot.ENERGIA_MIN;
        }
        robot.setEnergia(nuevaEnergia);

        System.out.println("Robot reparado correctamente.");
        return true;
    }

    public void mostrarInventario() {
        inventario.mostrar();
    }
}
