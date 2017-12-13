package jsonSchemaValidator;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;
import com.google.common.collect.Lists;

public class JsonSchemaValidator {
	
	private String propertiesFile = "validation.properties";
	private List<JsonNode> errors = Lists.newArrayList();
	
	public JsonSchemaValidator()
	{
	}
	
	public JsonSchemaValidator(String _propertiesFile)
	{
		propertiesFile = _propertiesFile;
	}
	
	private JsonSchemaFactory doFactory()
	{
		JsonSchemaFactory factory = null;
		
		Locale locale = Locale.getDefault();

		if (locale.getLanguage().equals(new Locale("ES").getLanguage())) {		 

			/* Cargar archivo properties con mensajes personalizados */

			final MessageBundle bundle = PropertiesBundle.forPath(propertiesFile);

			/* Crear la configuración de validación personalizada */
			final ValidationConfiguration cfg = ValidationConfiguration.newBuilder()
					.setValidationMessages(bundle).freeze();

			factory = JsonSchemaFactory.newBuilder()
					.setValidationConfiguration(cfg).freeze();
		}
		else {
			factory = JsonSchemaFactory.byDefault();
		}
		
		return factory;
	}
	
	private void convertMessageToJson(final List<ProcessingMessage> _messages)
	{	
		if (!_messages.isEmpty()) {
			for (final ProcessingMessage message: _messages)
			{
				JsonNode node = message.asJson();
				
				String text = node.get("message") != null ? node.get("message").textValue().replaceAll("\"","") : "";
				
				JsonNode fieldNode = node.get("instance") != null 
						? node.get("instance").get("pointer")
						: null;
				
				String field = fieldNode != null ? fieldNode.textValue().replaceAll("\"","") : "";
				
				ObjectNode jsonError = JacksonUtils.nodeFactory().objectNode();
				
				jsonError.put("text", text);

				if (!field.isEmpty()) 
				{
					jsonError.put("field", field.substring(1));
				}
				
				errors.add(jsonError);
			}
		}
	}
	
	
	/*
	 * Valida el JSON a partir de un Schema y un JSON 
	 * @param _schema debe ser un JsonNode
	 * @param _json debe ser un JsonNode
	 */
	public List<JsonNode> validate(JsonNode _schema, JsonNode _json) 
	{
		JsonSchemaFactory factory = doFactory();
		
		JsonSchema schema = null;
		
		if (factory != null)
		{
			try {
				schema = factory.getJsonSchema(_schema);
			} catch (ProcessingException e) {
				System.err.println();
			}
		}

		ProcessingReport report = null;
		
		if (schema != null)
		{
			try {
				report = schema.validate(_json, true);
			} catch (ProcessingException e) {
				System.err.println();
			}
		}

		List<ProcessingMessage> messages = Lists.newArrayList();
		
		if (report != null)
		{
			messages = Lists.newArrayList(report);
		}
		
		convertMessageToJson(messages); 
		
		return errors;
	}
	

	/*
	 * Valida el JSON a partir de un Schema y un JSON que se encuentren en URL
	 * @param _schema debe ser una URL válida
	 * @param _json debe ser una URL válida
	 */
	public List<JsonNode> validate(URL _schema, URL _json)
	{		
		JsonNode schema = null;
		JsonNode json = null;
		
		try {
			schema = JsonLoader.fromURL(_schema);
			json = JsonLoader.fromURL(_json);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(schema, json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema y un JSON que se encuentren en recursos locales
	 * @param _schema debe comenzar con '/'
	 * @param _json debe comenzar con '/'
	 */
	public List<JsonNode> validate(String _schema, String _json)
	{		
		JsonNode schema = null;
		JsonNode json = null;
		
		try {
			schema = JsonLoader.fromResource(_schema);
			json = JsonLoader.fromResource(_json);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(schema, json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema en una URLy un JSON local
	 * @param _schema debe ser una URL válida 
	 * @param _json debe comenzar con '/'
	 */
	public List<JsonNode> validate(URL _schema, String _json)
	{		
		JsonNode schema = null;
		JsonNode json = null;
		
		try {
			schema = JsonLoader.fromURL(_schema);
			json = JsonLoader.fromResource(_json);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(schema, json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema local y un JSON en una URL
	 * @param _schema debe comenzar con '/' 
	 * @param _json debe ser una URL válida
	 */
	public List<JsonNode> validate(String _schema, URL _json) 
	{		
		JsonNode schema = null;
		JsonNode json = null;
		
		try {
			schema = JsonLoader.fromResource(_schema);
			json = JsonLoader.fromURL(_json);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(schema, json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema local y un JSON en una URL
	 * @param _schema debe ser un objeto de tipo JsonNode 
	 * @param _json debe ser una URL válida
	 */
	public List<JsonNode> validate(JsonNode _schema, URL _json) 
	{		
		JsonNode json = null;
		
		try {
			json = JsonLoader.fromURL(_json);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(_schema, json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema local y un JSON local
	 * @param _schema debe ser un objeto de tipo JsonNode 
	 * @param _json debe comenzar con '/'
	 */
	public List<JsonNode> validate(JsonNode _schema, String _json) 
	{		
		JsonNode json = null;
		
		try {
			json = JsonLoader.fromResource(_json);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(_schema, json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema en una URL y un JSON local
	 * @param _schema debe ser una URL válida
	 * @param _json debe ser un objeto de tipo JsonNode
	 */
	public List<JsonNode> validate(URL _schema, JsonNode _json) 
	{		
		JsonNode schema = null;
		
		try {
			schema = JsonLoader.fromURL(_schema);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(schema, _json);
	}
	
	/*
	 * Valida el JSON a partir de un Schema local y un JSON local
	 * @param _schema debe comenzar con '/'  
	 * @param _json  debe ser un objeto de tipo JsonNode
	 */
	public List<JsonNode> validate(String _schema, JsonNode _json) 
	{		
		JsonNode schema = null;
		
		try {
			schema = JsonLoader.fromResource(_schema);
		} catch (IOException e) {
			System.err.println();
		}
		
		return validate(schema, _json);
	}
	
	@Override
	public final String toString()
	{
		ObjectMapper mapper = new ObjectMapper();
		
		String pretty = null;
		
		try {
			 pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errors);
		} catch (JsonProcessingException e) {
			System.err.println();
		}
		
		return pretty;
	}
	
}
