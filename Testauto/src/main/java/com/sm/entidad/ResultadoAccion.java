package com.sm.entidad;

public class ResultadoAccion {
    private String accion;
    private boolean exito;
    private String mensaje;

    // Constructor, getters y setters...
    public ResultadoAccion(String accion, boolean exito, String mensaje) {
        this.accion = accion;
        this.exito = exito;
        this.mensaje = mensaje;
    }
    public String getAccion() { return accion; }
    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
}

