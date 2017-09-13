package br.com.m3rcurio.livroemprestator.model;

import java.io.Serializable;

/**
 * Created by bruno on 07/09/2017.
 */

public class Interacoes implements Serializable {
    private String id;
    private String livro;
    private String usuarioLeitor;
    private String usuarioEmprestador;
    private int status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLivro() {
        return livro;
    }

    public void setLivro(String livro) {
        this.livro = livro;
    }

    public String getUsuarioLeitor() {
        return usuarioLeitor;
    }

    public void setUsuarioLeitor(String usuarioLeitor) {
        this.usuarioLeitor = usuarioLeitor;
    }

    public String getUsuarioEmprestador() {
        return usuarioEmprestador;
    }

    public void setUsuarioEmprestador(String usuarioEmprestador) {
        this.usuarioEmprestador = usuarioEmprestador;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
