package com.example.entrevistasapp.config;

public class entrevista {
    private Integer idOrden;

    private String descripcion;
    private String periodista;
    private String fechaEntrevista;
    private String foto;
    private String audioEntrevista;

    public entrevista() {
    }

    public entrevista(Integer idOrden, String descripcion, String periodista, String fechaEntrevista, String foto, String audioEntrevista) {
        this.idOrden = idOrden;
        this.descripcion = descripcion;
        this.periodista = periodista;
        this.fechaEntrevista = fechaEntrevista;
        this.foto = foto;
        this.audioEntrevista = audioEntrevista;
    }

    public Integer getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Integer idOrden) {
        this.idOrden = idOrden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPeriodista() {
        return periodista;
    }

    public void setPeriodista(String periodista) {
        this.periodista = periodista;
    }

    public String getFechaEntrevista() {
        return fechaEntrevista;
    }

    public void setFechaEntrevista(String fechaEntrevista) {
        this.fechaEntrevista = fechaEntrevista;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getAudioEntrevista() {
        return audioEntrevista;
    }

    public void setAudioEntrevista(String audioEntrevista) {
        this.audioEntrevista = audioEntrevista;
    }

}
