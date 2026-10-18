package com.mycompany.robofix.patterns.memento;

import com.mycompany.robofix.model.Robot;

public class RobotCaretaker {
    private RobotMemento mementoGuardado;

    public void guardarConfiguracion(Robot robot) {
        if (robot != null) {
            this.mementoGuardado = robot.guardarEstado();
        }
    }

    public boolean restaurarConfiguracion(Robot robot) {
        if (mementoGuardado == null) {
            return false;
        }
        if (robot != null) {
            robot.restaurarEstado(mementoGuardado);
            return true;
        }
        return false;
    }

    public boolean tieneConfiguracionGuardada() {
        return mementoGuardado != null;
    }
}
