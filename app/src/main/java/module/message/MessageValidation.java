package module.message;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;

public class MessageValidation {
    private static final Logger logger = Logger.getLogger(MessageValidation.class.getName());

    private static JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(VersionFlag.V201909);

    private MessageValidation(){

    }

    public static boolean isJsonRequestMessageValid(Object jsonRequestMessageObject) {
        try (InputStream schemaStream = inputStreamFromClasspath(jsonRequestMessageObject.getClass().getSimpleName() + ".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.convertValue(jsonRequestMessageObject, JsonNode.class);
            JsonSchema schema = schemaFactory.getSchema(schemaStream);
            Set<ValidationMessage> validationResult = schema.validate(json);
            if (validationResult.isEmpty()) {
                return true;
            } else {
                validationResult.forEach(vm -> logger.trace(String.format("(%s) Invalid message field: %s", jsonRequestMessageObject.getClass().getSimpleName(), vm.getMessage())));
            }
        } catch (IOException jmv1) {
            logger.info(String.format("Fail to validate message: %s", jmv1.getMessage()));
        } catch (IllegalArgumentException jmv2) {
            logger.info(String.format("Fail to read input: %s", jmv2.getMessage()));
        }
        return false;
    }

    private static InputStream inputStreamFromClasspath(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
}
