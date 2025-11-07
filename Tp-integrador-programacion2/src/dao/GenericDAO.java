/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;

/**
 *
 * @author PC
 */
public interface GenericDAO<T> {
    // Esta es una interfaz genérica que define métodos comunes para trabajar con cualquier entidad.

    void insertar(T entidad) throws Exception;
    void actualizar(T entidad)throws Exception;
    void eliminar(int id)throws Exception;
    T getById(int id)throws Exception;
    List<T> getAll()throws Exception;

}
