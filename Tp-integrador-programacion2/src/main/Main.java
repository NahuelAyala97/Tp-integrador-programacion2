/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import dao.HistoriaClinicaDAO;
import dao.PacienteDAO;
import entities.GrupoSanguineo;
import entities.HistoriaClinica;
import entities.Paciente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author PC
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        HistoriaClinica hc1 = new HistoriaClinica(1, 1, GrupoSanguineo.O_POSITIVO, "Alergia a la penicilina.", "Loratadina 10mg", "Paciente refiere buena salud general.");

        HistoriaClinica hc2 = new HistoriaClinica(2, 2, GrupoSanguineo.A_NEGATIVO, "Asma infantil, controlado.", "Salbutamol", "Control anual.");

        HistoriaClinica hc3 = new HistoriaClinica(3, 3, GrupoSanguineo.B_POSITIVO, "Hipertensión leve.", "Enalapril 5mg", "Requiere seguimiento de presión arterial.");

        //Creación de los Pacientes
        Paciente p1 = new Paciente(1, "Carlos", "Gomez", 30123456, LocalDate.of(1983, 5, 15), hc1);

        Paciente p2 = new Paciente(2, "Ana", "Martinez", 35987654, LocalDate.of(1991, 11, 20), hc2);

        Paciente p3 = new Paciente(3, "Luis", "Fernandez", 28456789, LocalDate.of(1980, 2, 10), hc3);

        HistoriaClinicaDAO historiaDAO = new HistoriaClinicaDAO();

//        //prueba de insert
//        historiaDAO.insertar(hc3);
        PacienteDAO pacienteDAO = new PacienteDAO();
//    
//        pacienteDAO.insertar(p3);

        //prueba update
//          p1.setNombre("Nicolas");
//          
//          pacienteDAO.actualizar(p1);
//          
//          hc1.setObservaciones("Ninguna.");
//          
//          historiaDAO.actualizar(hc1);   
// prueba delete
        // pacienteDAO.eliminar(1);
        // historiaDAO.eliminar(1);
        // prueba getById
//        System.out.println("Historia Encontrada");
//        System.out.println(historiaDAO.getById(1).toString());
//
//        System.out.println("Paciente Encontrado");
//        System.out.println(pacienteDAO.getById(1).toString());

    //prueba getAll
    
       List<HistoriaClinica> listaHistorias = historiaDAO.getAll();
       List<Paciente> listapaciente = pacienteDAO.getAll();
       
        for (HistoriaClinica historia : listaHistorias) {
            System.out.println(historia);
        }
        
        for (Paciente paciente : listapaciente) {
            System.out.println(paciente);
        }
    }
}
