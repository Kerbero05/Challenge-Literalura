package com.aluracursos.challenge.principal;

import com.aluracursos.challenge.model.Datos;
import com.aluracursos.challenge.model.DatosLibros;
import com.aluracursos.challenge.service.ConsumoAPI;
import com.aluracursos.challenge.service.ConvierteDatos;
import jakarta.persistence.Entity;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);
    public void muestraElMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json,Datos.class);
        System.out.println(datos);

        //Top 10 libros más descargados
        System.out.println("----------------------------------------------");
        System.out.println("Libreria Top 10: ");
        System.out.println("----------------------------------------------");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> l.titulo().toUpperCase())
                .forEach(System.out::println);

        //Busqueda de libros por nombre
        System.out.println("----------------------------------------------");
        System.out.println("Ingrese el titulo del libro a buscar: ");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search=" + tituloLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println("=>Libro Encontrado: ");
            System.out.println(libroBuscado.get());
        }else {
            System.out.println("Libro no encontrado!");
        }

        //Trabajando con estadisticas
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d -> d.numeroDeDescargas() >0 )
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("Total de Descargas: "+ est.getMax());
        System.out.println("Cantidad de registros evaluados para calcular las estadisticas: " + est.getCount());

        //Busqueda de libros por fecha
    }
}
