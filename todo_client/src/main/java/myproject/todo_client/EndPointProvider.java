package myproject.todo_client;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jakarta.rs.json.JacksonXmlBindJsonProvider;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import java.util.Collections;
import java.util.HashMap;

public class EndPointProvider
{
    public static <T> T getClient(Class<T> tclass){
        return JAXRSClientFactory.create("http://0.0.0.0:8080", tclass,
                Collections.singletonList(new JacksonXmlBindJsonProvider().disable(SerializationFeature.WRAP_ROOT_VALUE)
                        .disable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)),
                new HashMap<>(), true);
    }
}
