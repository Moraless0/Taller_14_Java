package com.mycompany.robofix;

import com.mycompany.robofix.legacy.EscanerAntiguo;
import com.mycompany.robofix.model.Robot;
import com.mycompany.robofix.patterns.adapter.EscanerAntiguoAdapter;
import com.mycompany.robofix.patterns.memento.RobotCaretaker;
import com.mycompany.robofix.service.Taller;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        Robot robot = new Robot("RX-01", "Explorador", 80, Robot.MODO_NORMAL);

        EscanerAntiguo escanerAntiguo = new EscanerAntiguo();
        EscanerAntiguoAdapter adaptador = new EscanerAntiguoAdapter(escanerAntiguo);

        Taller taller1 = new Taller(adaptador);
        Taller taller2 = new Taller(adaptador);

        RobotCaretaker caretaker = new RobotCaretaker();

        int opcion = 0;

        while (opcion != 10) {
            System.out.println("\n====== ROBOFIX ======");
            System.out.println("1. Ver robot");
            System.out.println("2. Diagnosticar robot");
            System.out.println("3. Reparar robot");
            System.out.println("4. Cambiar configuracion");
            System.out.println("5. Ver inventario");
            System.out.println("6. Comparar inventarios");
            System.out.println("7. Salir");
            System.out.println("8. Guardar configuracion");
            System.out.println("9. Restaurar configuracion");
            System.out.println("10. Salir");
            System.out.print("Opcion: ");

            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    robot.mostrar();
                    break;

                case 2:
                    taller1.diagnosticar(robot);
                    break;

                case 3:
                    boolean exitoReparacion = taller1.reparar(robot);
                    if (!exitoReparacion) {
                        System.out.println("La reparacion fallo.");
                    }
                    break;

                case 4:
                    System.out.print("Nueva energia: ");
                    int energia = teclado.nextInt();
                    teclado.nextLine();

                    System.out.print("Nuevo modo: ");
                    String modo = teclado.nextLine();

                    int energiaAnterior = robot.getEnergia();
                    String modoAnterior = robot.getModo();

                    boolean energiaValida = robot.setEnergia(energia);
                    boolean modoValido = robot.setModo(modo);

                    if (!energiaValida) {
                        System.out.println("Error: La energia debe estar entre " + Robot.ENERGIA_MIN + " y " + Robot.ENERGIA_MAX + ".");
                    }
                    if (!modoValido) {
                        System.out.println("Error: El modo debe ser " + Robot.MODO_NORMAL + ", " + Robot.MODO_AHORRO + " o " + Robot.MODO_MANTENIMIENTO + ".");
                    }

                    if (energiaValida && modoValido) {
                        System.out.println("Configuracion actualizada.");
                    } else {
                        robot.setEnergia(energiaAnterior);
                        robot.setModo(modoAnterior);
                        System.out.println("Configuracion no actualizada. Se restauraron los valores anteriores.");
                    }
                    break;

                case 5:
                    taller1.mostrarInventario();
                    break;

                case 6:
                    System.out.println("Inventario taller 1:");
                    taller1.mostrarInventario();

                    System.out.println("Inventario taller 2:");
                    taller2.mostrarInventario();
                    break;

                case 7:
                    System.out.println("Cerrando RoboFix...");
                    opcion = 10;
                    break;

                case 8:
                    caretaker.guardarConfiguracion(robot);
                    System.out.println("Configuracion guardada.");
                    break;

                case 9:
                    if (caretaker.tieneConfiguracionGuardada()) {
                        boolean exitoRestauracion = caretaker.restaurarConfiguracion(robot);
                        if (exitoRestauracion) {
                            System.out.println("Configuracion restaurada.");
                            robot.mostrar();
                        }
                    } else {
                        System.out.println("Error: No hay ninguna configuracion guardada para restaurar.");
                    }
                    break;

                case 10:
                    System.out.println("Cerrando RoboFix...");
                    break;

                default:
                    System.out.println("Opcion invalida.");
            }
        }

        teclado.close();
    }
}
