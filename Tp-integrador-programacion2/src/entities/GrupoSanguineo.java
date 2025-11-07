/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

/**
 *
 * @author PC
 */
public enum GrupoSanguineo {
    A_POSITIVO("A+"),
    A_NEGATIVO("A-"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB-"),
    O_POSITIVO("O+"),
    O_NEGATIVO("O-");
    
    private final String valor;

    private GrupoSanguineo(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
    
    public static GrupoSanguineo fromValor(String valor) {
    for (GrupoSanguineo tipo : values()) {
        if (tipo.valor.equals(valor)) {
            return tipo;
        }
    }
    return null;
}
}
