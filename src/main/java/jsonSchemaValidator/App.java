package jsonSchemaValidator;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

public class App 
{
	public static void main( String[] args ) throws ProcessingException, IOException
	{

		/* Ejemplo 1 */
		JsonSchemaValidator wrapperWithURL = new JsonSchemaValidator();	

		URL url = new URL("http://localhost:81/persona.schema");

		List<JsonNode> a = wrapperWithURL.validate(url, "/persona.json");

		System.out.println("--- Ejemplo 1: Obteniendo Schema de una URL ---");
		System.out.println(wrapperWithURL);
		
		
		/* Ejemplo 2 */
		JsonSchemaValidator wrapperFromLocalResources = new JsonSchemaValidator();	

		wrapperFromLocalResources.validate("/personaLocal.schema", "/persona.json");

		System.out.println("\n--- Ejemplo 2: Obteniendo Schema de un recurso local ---");
		System.out.println(wrapperFromLocalResources);
		
	}
}
