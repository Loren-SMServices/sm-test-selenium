package com.sm.servicios;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
public class SeleniumService {
	
	private final WebDriver webDriver;

    public SeleniumService(WebDriver webDriver) {
        System.out.println("[SeleniumService] Constructor: WebDriver inyectado.");
        this.webDriver = webDriver;
    }

    public void realizarPrueba() {
        System.out.println("[SeleniumService] Iniciando prueba simple en https://www.ejemplo.com");
        webDriver.get("https://www.ejemplo.com");
        // ...acciones Selenium...
        System.out.println("[SeleniumService] Prueba simple finalizada.");
    }
	
	@PreDestroy
    public void cerrarDriver() {
        System.out.println("[SeleniumService] Cerrando WebDriver...");
        if (webDriver != null) {
            webDriver.quit();
            System.out.println("[SeleniumService] WebDriver cerrado correctamente.");
        }
    }
	
	public String ejecutarPrueba(String webUrl, String prompt) {
        System.out.println("[SeleniumService] Ejecutando prueba con URL: " + webUrl);
        System.out.println("[SeleniumService] Prompt recibido: " + prompt);
        try {
        	webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            System.out.println("[SeleniumService] Navegando a la URL...");
            webDriver.get(webUrl);

            if (prompt.toLowerCase().contains("escribe")) {
                System.out.println("[SeleniumService] Acción detectada: ESCRIBIR");
                webDriver.findElement(By.id("APjFqb")).sendKeys("LOREN");
                System.out.println("[SeleniumService] Texto escrito en el campo con id 'APjFqb'.");
            }
            System.out.println("[SeleniumService] Prueba ejecutada correctamente.");
            return "Prueba ejecutada correctamente";
        } catch (Exception e) {
            System.out.println("[SeleniumService] Error durante la prueba: " + e.getMessage());
            return "Error durante la prueba: " + e.getMessage();
        } finally {
//            webDriver.quit();
        }
    }
	
	public String interpretarYBuscar(String webUrl, String prompt) {
	    System.out.println("[SeleniumService] interpretarYBuscar() - URL: " + webUrl + " | Prompt: " + prompt);
	    try {
	        webDriver.get(webUrl);
	        System.out.println("[SeleniumService] Página cargada.");

	        String accion = "";
	        String objetivo = "";

	        if (prompt.toLowerCase().contains("clic")) {
	            accion = "clic";
	            objetivo = extraerTextoObjetivo(prompt, "clic en el");
	            System.out.println("[SeleniumService] Acción: CLIC | Objetivo: " + objetivo);

	            // Buscar botones y enlaces
	            WebElement elemento = buscarElementoPorTexto(objetivo, accion);
	            if (elemento != null) {
	                System.out.println("[SeleniumService] Elemento encontrado: " + elemento.toString());
	                elemento.click();
	                System.out.println("[SeleniumService] Acción CLIC ejecutada.");
	            } else {
	                System.out.println("[SeleniumService] No se encontró un elemento que coincida con: " + objetivo);
	                throw new RuntimeException("No se encontró un elemento que coincida con: " + objetivo);
	            }

	        } else if (prompt.toLowerCase().contains("escribe")) {
	            accion = "escribe";
	            objetivo = extraerTextoObjetivo(prompt, "escribe en el");
	            System.out.println("[SeleniumService] Acción: ESCRIBIR | Objetivo: " + objetivo);

	            // Buscar inputs y textareas
	            WebElement elemento = buscarElementoPorTexto(objetivo, accion);
	            if (elemento != null) {
	                String textoAEscribir = extraerTextoAEscribir(prompt);
	                System.out.println("[SeleniumService] Texto a escribir: " + textoAEscribir);
	                elemento.sendKeys(textoAEscribir);
	                System.out.println("[SeleniumService] Acción ESCRIBIR ejecutada.");
	            } else {
	                System.out.println("[SeleniumService] No se encontró un elemento que coincida con: " + objetivo);
	                throw new RuntimeException("No se encontró un elemento que coincida con: " + objetivo);
	            }

	        } else {
	            System.out.println("[SeleniumService] No se detectó una acción válida en el prompt.");
	            throw new RuntimeException("No se detectó una acción válida en el prompt.");
	        }

	        System.out.println("[SeleniumService] Prueba ejecutada correctamente.");
	        return "Prueba ejecutada correctamente";
	    } catch (Exception e) {
	        System.out.println("[SeleniumService] Error durante la prueba: " + e.getMessage());
	        return "Error durante la prueba: " + e.getMessage();
	    }finally {
//	    	 webDriver.quit();
		}
	    // No cierres el driver aquí si es singleton
	}


	private WebElement buscarElementoPorTexto(String objetivo, String accion) {
	    System.out.println("[SeleniumService] buscarElementoPorTexto() acción: " + accion + " | Objetivo: " + objetivo);
	    try {
			List<WebElement> elementos;

			if ("clic".equals(accion)) {
			    // Botones y enlaces
			    elementos = webDriver.findElements(By.xpath("//button | //a | //input[@type='button' or @type='submit']"));
			} else if ("escribe".equals(accion)) {
			    // Inputs y textareas
			    elementos = webDriver.findElements(By.xpath("//input[not(@type='button') and not(@type='submit') and not(@type='reset')] | //textarea"));
			} else {
			    elementos = new ArrayList<>();
			}

			for (WebElement el : elementos) {
				if (!el.isDisplayed()) continue;
			        String texto = (el.getText() + " " +
			                        el.getDomAttribute("value") + " " +
			                        el.getDomAttribute("aria-label") + " " +
			                        el.getDomAttribute("placeholder") + " " +
			                        el.getDomAttribute("name") + " " +
			                        el.getDomAttribute("id") + " " +
			                        el.getDomAttribute("type")).toLowerCase();

			    System.out.println("[SeleniumService] Analizando elemento: " + texto);

			    if (texto.contains(objetivo.toLowerCase())) {
			        System.out.println("[SeleniumService] Coincidencia encontrada: " + texto);
			        return el;
			    }
			}
			
		} catch (Exception e) {
			System.out.println("Error en " + e.getMessage());
		}finally {
//			webDriver.quit();
		}
	    System.out.println("[SeleniumService] No se encontró ningún elemento que coincida con el objetivo.");
		return null;
	}

	
	private String extraerTextoAEscribir(String prompt) {
	    System.out.println("[SeleniumService] extraerTextoAEscribir() prompt: " + prompt);
	    try {
			Pattern patternComillasDobles = Pattern.compile("escribe\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
			Matcher matcherDobles = patternComillasDobles.matcher(prompt);
			if (matcherDobles.find()) {
			    System.out.println("[SeleniumService] Texto a escribir encontrado entre comillas dobles: " + matcherDobles.group(1));
			    return matcherDobles.group(1);
			}

			Pattern patternComillasSimples = Pattern.compile("escribe\\s*'([^']+)'", Pattern.CASE_INSENSITIVE);
			Matcher matcherSimples = patternComillasSimples.matcher(prompt);
			if (matcherSimples.find()) {
			    System.out.println("[SeleniumService] Texto a escribir encontrado entre comillas simples: " + matcherSimples.group(1));
			    return matcherSimples.group(1);
			}

			Pattern patternSinComillas = Pattern.compile("escribe\\s+([\\wáéíóúüñ]+)", Pattern.CASE_INSENSITIVE);
			Matcher matcherSinComillas = patternSinComillas.matcher(prompt);
			if (matcherSinComillas.find()) {
			    System.out.println("[SeleniumService] Texto a escribir encontrado sin comillas: " + matcherSinComillas.group(1));
			    return matcherSinComillas.group(1);
			}
			
			Pattern patternSinComillasClick = Pattern.compile("clic(?: en (?:el|la|los|las)?)?\\s*([\\wáéíóúüñ ]+)", Pattern.CASE_INSENSITIVE);
			Matcher matcherSinComillasClick = patternSinComillasClick.matcher(prompt);
			if (matcherSinComillasClick.find()) {
			    return matcherSinComillasClick.group(1).trim();
			}

			System.out.println("[SeleniumService] No se encontró texto a escribir en el prompt.");
			return "";
		} catch (Exception e) {
			System.out.println("Error en " + e.getMessage());
		}finally {
//			webDriver.quit();
		}
		return null;
	}

	private String extraerTextoObjetivo(String prompt, String tipo) {
	    System.out.println("[SeleniumService] extraerTextoObjetivo() prompt: " + prompt + " | Tipo: " + tipo);
	    try {
			Pattern patternComillasDobles = Pattern.compile(tipo + ".*?\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
			Matcher matcherDobles = patternComillasDobles.matcher(prompt);
			if (matcherDobles.find()) {
			    System.out.println("[SeleniumService] Objetivo encontrado entre comillas dobles: " + matcherDobles.group(1));
			    return matcherDobles.group(1);
			}

			Pattern patternComillasSimples = Pattern.compile(tipo + ".*?'([^']+)'", Pattern.CASE_INSENSITIVE);
			Matcher matcherSimples = patternComillasSimples.matcher(prompt);
			if (matcherSimples.find()) {
			    System.out.println("[SeleniumService] Objetivo encontrado entre comillas simples: " + matcherSimples.group(1));
			    return matcherSimples.group(1);
			}

			Pattern patternSinComillas = Pattern.compile(tipo + " (?:el|la|los|las)?\\s*([\\wáéíóúüñ]+)", Pattern.CASE_INSENSITIVE);
			Matcher matcherSinComillas = patternSinComillas.matcher(prompt);
			if (matcherSinComillas.find()) {
			    System.out.println("[SeleniumService] Objetivo encontrado sin comillas: " + matcherSinComillas.group(1));
			    return matcherSinComillas.group(1);
			}
			
			Pattern patternSinComillasClick = Pattern.compile("clic(?: en (?:el|la|los|las)?)?\\s*([\\wáéíóúüñ ]+)", Pattern.CASE_INSENSITIVE);
			Matcher matcherSinComillasClick = patternSinComillasClick.matcher(prompt);
			if (matcherSinComillasClick.find()) {
			    return matcherSinComillasClick.group(1).trim();
			}

			System.out.println("[SeleniumService] No se encontró objetivo en el prompt.");
			return "";
		} catch (Exception e) {
			 System.out.println("Error en " + e.getMessage());
		}finally {
//			 webDriver.quit();
		}
		return null;
	}
	
	public List<Map<String, String>> extraerElementosInteractuables(String url) {
		System.out.println("[SeleniumService] Entrando en extraerElementosInteractuables() url: " + url );
	    try {
			List<Map<String, String>> elementos = new ArrayList<>();
			webDriver.get(url);

			// Botones y enlaces
			List<WebElement> botones = webDriver.findElements(By.xpath("//button|//a|//input|//select|//textarea"));
			for (WebElement el : botones) {
				if (!el.isDisplayed()) continue;
			        Map<String, String> info = new HashMap<>();
			        info.put("tag", el.getTagName());
			        info.put("type", el.getDomAttribute("type"));
			        info.put("id", el.getDomAttribute("id"));
			        info.put("name", el.getDomAttribute("name"));
			        info.put("text", el.getText());
			        info.put("placeholder", el.getDomAttribute("placeholder"));
			        elementos.add(info);
			}
			
			return elementos;
		} catch (Exception e) {
			System.out.println("Error en " + e.getMessage());
		}finally {
//			 webDriver.quit();
		}
		return null;
	}

}
