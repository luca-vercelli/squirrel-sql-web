package net.sourceforge.squirrel_sql.jaxrs;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.UidIdentifier;

/**
 * Create a IIdentifier from a String
 *
 */
public class IIdentifierDeserializer extends JsonDeserializer<IIdentifier> {

    @Override
    public IIdentifier deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        String stringId = node.asText();

        UidIdentifier id = new UidIdentifier();
        id.setString(stringId);
        return id;
    }

}
