package br.com.m3rcurio.livroemprestator.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bruno on 20/08/2017.
 */

public class Livros implements Serializable {
    private String tipo;
    private String id;
    private String etag;
    private String titulo;
    private String subTitulo;
    private String urlImagem;
    private String resumo;
    private int quantidade;
    private List<String> autores;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public List<String> getAutores() {
        return autores;
    }

    public void setAutores(List<String> autores) {
        this.autores = autores;
    }

    /*public String getExbicaoAutores(){
        String nomes=this.autores.get(0);

      // for(int i=0; i<this.autores.size(); i++){
    //    nomes+=this.autores.get(i);
     //   }
        if(this.autores.size()>1){
            nomes+=" , "+this.autores.get(1);
        }
        if(this.autores.size()>2){
            nomes+= " e outros";
        }

        return nomes;
    }*/

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}
