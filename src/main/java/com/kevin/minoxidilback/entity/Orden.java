package com.kevin.minoxidilback.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @NotNull
    private int cantidad;

    public Orden(Usuario usuario, Date fecha, int cantidad) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public Orden() {

    }
}
