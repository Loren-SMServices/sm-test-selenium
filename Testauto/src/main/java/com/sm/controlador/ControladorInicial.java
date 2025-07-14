package com.sm.controlador;

import java.util.ArrayList;
import java.util.HashMap;
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
	public Map<String, Object> paginaTestAjax(@RequestParam String webUrl) {
	    System.out.println("Entrando en test/ajax");
	    Map<String, Object> resultado = seleniumService.extraerElementosInteractuables(webUrl);
	    if (resultado == null) {
	        resultado = new HashMap<>();
	        resultado.put("urlFinal", webUrl);
	        resultado.put("elementos", new ArrayList<>());
	    }
	    List<String> lista = (List<String>) resultado.get("elementos");
	    System.out.println("Elementos interactuables: " + lista.size());
	    return resultado;
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
	            String resultado = seleniumService.interpretarYBuscar(webUrl, accionTrim, acciones);
	            boolean exito = !resultado.toLowerCase().contains("error");
	            resultados.add(new ResultadoAccion(accionTrim, exito, resultado));
	        }
	    }
	    
	    String urlFinal = seleniumService.getWebDriver().getCurrentUrl();
	    String mensaje = "";
	    for (ResultadoAccion resultado : resultados) {
	    	mensaje +=resultado.getAccion() + " " + resultado.getMensaje() + "\n" ;
		}

	    // 3. Enviar la lista de resultados a la vista
	    model.addAttribute("mensaje", mensaje);
	    model.addAttribute("resultados", resultados);
	    if (webUrl != null && urlFinal != null && webUrl.trim().equals(urlFinal.trim())) {
	    	model.addAttribute("webUrl", webUrl);
	    }else {
	    	model.addAttribute("webUrl", urlFinal);
	    }

	    System.out.println("Saliendo de iniciar-prueba() resultados: " + resultados.size());
	    return "test";
	}
	
}
