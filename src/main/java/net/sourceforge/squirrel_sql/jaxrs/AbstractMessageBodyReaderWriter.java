package net.sourceforge.squirrel_sql.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

/**
 * JSON serializer, with Jackson
 * 
 * Differently from JAXB / MOXy, here we serialize objects, non classes
 * 
 * @author LV 2019-2020
 * 
 * @see JacksonJsonProvider
 *
 */
public abstract class AbstractMessageBodyReaderWriter<T> implements MessageBodyWriter<T>, MessageBodyReader<T> {

    public final DateFormat jsf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // Can convert any datatype to JSON
        return MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream out) throws IOException, WebApplicationException {

        ObjectMapper mapper = createObjectMapper();
        Writer writer = new PrintWriter(out);
        mapper.writeValue(writer, t);
        writer.flush();
        writer.close();
    }

    /**
     * Create a ObjectMapper, with some defaults
     * 
     * @return
     */
    public ObjectMapper createObjectMapper() {

        SimpleModule module = new SimpleModule();
        module.addSerializer(IIdentifier.class, new IIdentifierSerializer());
        module.addDeserializer(IIdentifier.class, new IIdentifierDeserializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        // Pretty print
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Date format
        mapper.setDateFormat(jsf);

        // Honor XmlTransient annotations
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        mapper.setAnnotationIntrospector(introspector);

        // Avoid to fail if an object have no getters
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }

    @Override

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // Can convert any datatype to JSON
        return MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        ObjectMapper mapper = createObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(entityStream, type);
    }

}
