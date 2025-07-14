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
    
    public WebDriver getWebDriver() {
		return this.webDriver;
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
            // webDriver.quit();
        }
    }
	
	public String interpretarYBuscar(String webUrl, String prompt, String[] acciones) {
	    System.out.println("[SeleniumService] interpretarYBuscar() - URL: " + webUrl + " | Prompt: " + prompt);
	    try {
	    	int tamañoAcciones = acciones.length;
	    	if (tamañoAcciones == 1) {
	    		webDriver.get(webUrl);
	    	}
	        System.out.println("[SeleniumService] Página cargada.");

	        String accion = "";
	        String objetivo = "";

	        if (prompt.toLowerCase().contains("clic")) {
	            accion = "clic";
	            objetivo = extraerTextoObjetivo(prompt, "clic en el");
	            System.out.println("[SeleniumService] Acción: CLIC | Objetivo: " + objetivo);

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

	            WebElement elemento = buscarElementoPorTexto(objetivo, accion);
	            if (elemento != null) {
	                String textoAEscribir = extraerTextoAEscribir(prompt);
	                System.out.println("[SeleniumService] Texto a escribir: " + textoAEscribir);
	                elemento.clear(); // Limpia el campo antes de escribir
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
	    }
	}

	// Mejora: ahora busca el objetivo tras "en el campo ..." o variantes
	private String extraerTextoObjetivo(String prompt, String tipo) {
	    System.out.println("[SeleniumService] extraerTextoObjetivo() prompt: " + prompt + " | Tipo: " + tipo);
	    try {
	        // Busca frases como "en el campo Email de usuario"
	        Pattern patternCampo = Pattern.compile("en el campo ([\\wáéíóúüñÁÉÍÓÚÜÑ ]+)", Pattern.CASE_INSENSITIVE);
	        Matcher matcherCampo = patternCampo.matcher(prompt);
	        if (matcherCampo.find()) {
	            String objetivo = matcherCampo.group(1).trim();
	            System.out.println("[SeleniumService] Objetivo encontrado por 'en el campo': " + objetivo);
	            return objetivo;
	        }
	        // Mantén aquí el resto de patrones si quieres compatibilidad
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

	        Pattern patternSinComillas = Pattern.compile(tipo + " (?:el|la|los|las)?\\s*([\\wáéíóúüñ ]+)", Pattern.CASE_INSENSITIVE);
	        Matcher matcherSinComillas = patternSinComillas.matcher(prompt);
	        if (matcherSinComillas.find()) {
	            System.out.println("[SeleniumService] Objetivo encontrado sin comillas: " + matcherSinComillas.group(1));
	            return matcherSinComillas.group(1);
	        }
	        System.out.println("[SeleniumService] No se encontró objetivo en el prompt.");
	        return "";
	    } catch (Exception e) {
	        System.out.println("Error en " + e.getMessage());
	    }
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
		}
		return null;
	}

	// Mejora: Coincidencia exacta y contiene para mayor robustez
	private WebElement buscarElementoPorTexto(String objetivo, String accion) {
	    System.out.println("[SeleniumService] buscarElementoPorTexto() acción: " + accion + " | Objetivo: " + objetivo);
	    try {
			List<WebElement> elementos;

			if ("clic".equals(accion)) {
			    elementos = webDriver.findElements(By.xpath("//button | //a | //input[@type='button' or @type='submit']"));
			} else if ("escribe".equals(accion)) {
			    elementos = webDriver.findElements(By.xpath("//input[not(@type='button') and not(@type='submit') and not(@type='reset')] | //textarea"));
			} else {
			    elementos = new ArrayList<>();
			}

			// Coincidencia exacta primero
			for (WebElement el : elementos) {
				if (!el.isDisplayed()) continue;
				String texto = (el.getAttribute("placeholder") + " " +
				                el.getAttribute("aria-label") + " " +
				                el.getAttribute("name") + " " +
				                el.getAttribute("id") + " " +
				                el.getText()).toLowerCase();

				if (texto.trim().equals(objetivo.toLowerCase().trim())) {
				    System.out.println("[SeleniumService] Coincidencia EXACTA encontrada: " + texto);
				    return el;
				}
			}
			// Si no hay coincidencia exacta, buscar por contiene
			for (WebElement el : elementos) {
				if (!el.isDisplayed()) continue;
				String texto = (el.getAttribute("placeholder") + " " +
				                el.getAttribute("aria-label") + " " +
				                el.getAttribute("name") + " " +
				                el.getAttribute("id") + " " +
				                el.getText()).toLowerCase();

				if (texto.contains(objetivo.toLowerCase().trim())) {
				    System.out.println("[SeleniumService] Coincidencia encontrada: " + texto);
				    return el;
				}
			}
		} catch (Exception e) {
			System.out.println("Error en " + e.getMessage());
		}
	    System.out.println("[SeleniumService] No se encontró ningún elemento que coincida con el objetivo.");
		return null;
	}
	
	public Map<String, Object> extraerElementosInteractuables(String url) {
	    Map<String, Object> resultado = new HashMap<>();
	    try {
	        String urlActual = webDriver.getCurrentUrl();
	        if (urlActual == null || !urlActual.equals(url)) {
	            webDriver.get(url);
	        }
	        String urlFinal = webDriver.getCurrentUrl();

	        List<Map<String, String>> elementos = new ArrayList<>();
	        List<WebElement> botones = webDriver.findElements(By.xpath("//button|//a|//input|//select|//textarea"));
	        for (WebElement el : botones) {
	            if (!el.isDisplayed()) continue;
	            Map<String, String> info = new HashMap<>();
	            info.put("tag", el.getTagName());
	            info.put("type", el.getDomAttribute("type"));
	            info.put("id", el.getDomAttribute("id"));
	            info.put("name", el.getDomAttribute("name"));
	            info.put("text", el.getText());
	            info.put("placeholder", el.getAttribute("placeholder"));
	            elementos.add(info);
	        }
	        resultado.put("urlFinal", urlFinal);
	        resultado.put("elementos", elementos);
	        return resultado;
	    } catch (Exception e) {
	        System.out.println("Error en " + e.getMessage());
	    }
	    return null;
	}
}
