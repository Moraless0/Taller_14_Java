package com.mycompany.robofix.patterns.adapter;

import com.mycompany.robofix.legacy.EscanerAntiguo;

public class EscanerAntiguoAdapter implements Diagnostico {
    private EscanerAntiguo escanerAntiguo;

    public EscanerAntiguoAdapter(EscanerAntiguo escanerAntiguo) {
        this.escanerAntiguo = escanerAntiguo;
    }

    @Override
    public ResultadoDiagnostico obtenerResultado(String codigoRobot) {
        String resultado = escanerAntiguo.ejecutarEscaneo(codigoRobot);
        String[] datos = resultado.split("\\|");

        if (datos.length != 4) {
            return null;
        }

        try {
            String robotCode = datos[0];
            String component = datos[1];
            String status = datos[2];
            int reportedValue = Integer.parseInt(datos[3]);

            return new ResultadoDiagnostico(robotCode, component, status, reportedValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
