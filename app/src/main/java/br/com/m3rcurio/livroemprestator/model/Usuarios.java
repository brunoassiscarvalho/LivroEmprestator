package br.com.m3rcurio.livroemprestator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 20/08/2017.
 */

public class Usuarios extends Pessoas implements Serializable {
   // private String senha;
    private ArrayList<String> preferencias;

    public Usuarios(){

    }

    public Usuarios(String id, String apelido, String nome, String email){

        super.setId(id);
        super.setApelido(apelido);
        super.setNome(nome);
        super.setEmail(email);

        //this.setSenha(senha);
    }

    public List<String> getListaLivros() {
        return super.getListaLivros();
    }

    public void setListaLivros(List<String> listaLivros) {
        super.setListaLivros(listaLivros);
    }



   /* public String getSenha() {
        return senha;
    }
     public void setSenha(String senha) {
        this.senha = senha;
    }*/

    public ArrayList<String> getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(ArrayList<String> preferencias) {
        this.preferencias = preferencias;
    }

    public String getEmailSemPontos(){
        return super.getEmail().replace(".","-");
    }
}
