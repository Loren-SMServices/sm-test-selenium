package com.sm.controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sm.entidad.ResultadoAccion;
import com.sm.servicios.SeleniumService;


@Controller
public class ControladorInicial {
	
	private SeleniumService seleniumService;
	
	ControladorInicial(SeleniumService seleniumService){
		this.seleniumService = seleniumService;
	}
	
	@GetMapping("/")
	public String paginaInicio() {
		System.out.println("Entrando en /");
		return "test";
	}
	
	@PostMapping("/test")
	public String test(@RequestParam(required = false) String webUrl, Model model) {
		System.out.println("Entrando en test");
		model.addAttribute("webUrl", webUrl);
		return "test";
	}
		
	@PostMapping("/test/ajax")
	@ResponseBody
	public List<Map<String, String>> paginaTestAjax(@RequestParam String webUrl) {
		System.out.println("Entrando en test/ajax");
	    List<Map<String, String>> elementos = seleniumService.extraerElementosInteractuables(webUrl);
	    // Controla si es nulo
	    if (elementos == null) {
	        return new ArrayList<>();
	    }
	    System.out.println("Elementos: " + elementos.size());
	    return elementos;
	}

	
	@PostMapping("/iniciar-prueba")
	public String inciarPrueba(@RequestParam String webUrl,
							   @RequestParam String prompt,
							   Model model) {
	  
		
		
	  System.out.println("Entrando en iniciar-prueba");
	  System.out.println("\nURL: " + webUrl + " ");
	  System.out.println("prompt: " + prompt);
	  
	// 1. Separar acciones por '|'
	    String[] acciones = prompt.split("\\|");
	    List<ResultadoAccion> resultados = new ArrayList<>();

	    // 2. Ejecutar cada acción y guardar el resultado
	    for (String accion : acciones) {
	        String accionTrim = accion.trim();
	        if (!accionTrim.isEmpty()) {
	            String resultado = seleniumService.interpretarYBuscar(webUrl, accionTrim)!=null ? seleniumService.interpretarYBuscar(webUrl, accionTrim):"";
	            boolean exito = !resultado.toLowerCase().contains("error");
	            resultados.add(new ResultadoAccion(accionTrim, exito, resultado));
	        }
	    }
	    
	    String mensaje = "";
	    for (ResultadoAccion resultado : resultados) {
	    	mensaje +=resultado.getAccion() + resultado.getMensaje();
		}

	    // 3. Enviar la lista de resultados a la vista
	    model.addAttribute("mensaje", mensaje);
	    model.addAttribute("resultados", resultados);
	    model.addAttribute("webUrl", webUrl);

	    System.out.println("Saliendo de iniciar-prueba() resultados: " + resultados);
	    return "test";
	}
	
}
