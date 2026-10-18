package com.mycompany.robofix.legacy;

// Sistema antiguo de diagnostico.
// No se permite modificar esta clase: simula una biblioteca externa.
public class EscanerAntiguo {

    public String ejecutarEscaneo(String codigoRobot) {
        if (codigoRobot.equals("RX-01")) {
            return "RX-01|BATERIA|CRITICO|15";
        }

        return codigoRobot + "|SISTEMA|NORMAL|0";
    }
}
