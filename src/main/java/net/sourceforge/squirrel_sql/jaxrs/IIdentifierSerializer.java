package net.sourceforge.squirrel_sql.jaxrs;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

/**
 * Write a IIdserializer as a String
 */
public class IIdentifierSerializer extends JsonSerializer<IIdentifier> {

    @Override
    public void serialize(IIdentifier value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
        gen.writeString(value.toString());
    }

}
