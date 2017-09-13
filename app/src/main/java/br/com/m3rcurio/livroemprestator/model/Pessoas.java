package br.com.m3rcurio.livroemprestator.model;

import java.util.List;

/**
 * Created by bruno on 20/08/2017.
 */

public class Pessoas {
    private String id;
    private String nome;
    private String apelido;
    private String email;
    private List<String> listaLivros;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getListaLivros() {
        return listaLivros;
    }

    public void setListaLivros(List<String> listaLivros) {
        this.listaLivros = listaLivros;
    }
}

