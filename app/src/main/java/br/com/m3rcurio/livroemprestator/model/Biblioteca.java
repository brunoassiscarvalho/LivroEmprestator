package br.com.m3rcurio.livroemprestator.model;


public class Biblioteca {
    private Integer id;
    private String name;
    private LocalBiblioteca location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalBiblioteca getLocation() {
        return location;
    }

    public void setLocation(LocalBiblioteca location) {
        this.location = location;
    }
}
