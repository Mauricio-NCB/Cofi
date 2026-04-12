package com.website.main.dto.ActividadMadrid;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActividadMadridDTO {
    private String id;
    private String titulo;
    private String descripcion;
    private String fechaInicio;
    private String fechaFin;
    private String hora;
    private String lugar;
    private String direccion;
    private String distrito;
    private String barrio;
    private String urlInfo;
    private boolean gratuita;
    private String precio;
    private String tipo;
}