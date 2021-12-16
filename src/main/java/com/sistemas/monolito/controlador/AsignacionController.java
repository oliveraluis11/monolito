package com.sistemas.monolito.controlador;

import com.sistemas.monolito.dominio.Asignacion;
import com.sistemas.monolito.servicio.AsignacionService;
import com.sistemas.monolito.servicio.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/asignacion")
public class AsignacionController {
    @Autowired private AsignacionService asignacionService;
    @Autowired private EmpleadoService empleadoService;

    @GetMapping({"{ordenid}/", "{ordenid}/index"})
    public String getIndex(
            @PathVariable("ordenid") Long ordenid,
            Model model){
        List<Asignacion> listaAsignaciones = new ArrayList<Asignacion>();

        //Solo se deben mostrar las tareas de una Orden específica
        for (Asignacion asignacion:asignacionService.listarTodos()){
            if(asignacion.getOrden().getId() == ordenid){
                listaAsignaciones.add(asignacion);
            }
        }
        // agrega la lista al modelo para pasarlo a la vista
        model.addAttribute("listaAsignaciones", listaAsignaciones);

        return "asignacion/asignacionIndex";
    }

    @GetMapping("/editar/{id}")
    public String getAsignacionFormEdit(
            @PathVariable("id") Long id,
            Model model){
        Optional<Asignacion> buscado = asignacionService.buscar(id);
        model.addAttribute("asignacion",
                buscado.isPresent()? buscado.get():new Asignacion());
        model.addAttribute("listaEmpleados", empleadoService.listarTodos());

        return "asignacion/asignacionForm";
    }

    @PostMapping("/editar")
    public String postAsignacionFormEdit(
            @Valid @ModelAttribute("asignacion") Asignacion asignacion,
            BindingResult bindingResult,
            Model model){

        if(bindingResult.hasErrors()){
            //Si hay errores muestra el formulario de edición
            model.addAttribute("listaEmpleados", empleadoService.listarTodos());
            return "asignacion/asignacionForm";
        }

        asignacionService.agregar(asignacion);

        return "redirect:/asignacion/" + asignacion.getOrden().getId() + "/index";
    }

    @GetMapping("iniciar/{id}")
    public String getTareaIniciar(
            @PathVariable("id") Long id,
            Model model){

        Long ordenId = 0L;
        Optional<Asignacion> buscado = asignacionService.buscar(id);

        if(buscado.isPresent()){
            Asignacion asignacion = buscado.get();
            asignacion.setFechaInicio(new Date());
            ordenId = asignacion.getOrden().getId();
            asignacionService.actualizar(asignacion);
        }

        return "redirect:/asignacion/" + ordenId + "/index";
    }

    @GetMapping("terminar/{id}")
    public String getTareaTerminar(
            @PathVariable("id") Long id,
            Model model){

        Long ordenId = 0L;
        Optional<Asignacion> buscado = asignacionService.buscar(id);

        if(buscado.isPresent()){
            Asignacion asignacion = buscado.get();
            asignacion.setFechaFin(new Date());
            ordenId = asignacion.getOrden().getId();
            asignacionService.actualizar(asignacion);
        }

        return "redirect:/asignacion/" + ordenId + "/index";
    }
}
