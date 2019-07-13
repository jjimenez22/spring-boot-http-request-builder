package ve.jj.requestbuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

/**
 * Clase que envuelve un {@link ResponseEntity} y permite verificar su respuesta antes de obtenerla, siguiendo
 * convicciones de resilencia pautadas en la empresa.
 * <p>
 * Esta clase no se debe instanciar directamente, es obtenida a través de la realización de solicitudes con {@link RequestBuilder}.
 */
public class ResponseWrapper {
    private ResponseEntity<String> response;
    private String errorHeader;
    private ObjectMapper mapper;

    /**
     * @param response    {@link ResponseEntity} que será envuelto.
     * @param errorHeader Header que contendrá la bandera de error para la verificación.
     */
    ResponseWrapper(ResponseEntity<String> response, String errorHeader) {
        this.response = response;
        this.errorHeader = errorHeader;
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Verifica si la respuesta contiene un código de error (no debería ocurrir nunca) o si está seteada como true la
     * bandera de error en los headers.
     *
     * @return Si contiene o no error.
     */
    public Boolean hasError() {
        if (response.getStatusCodeValue() >= 300) {
            return true;
        }
        HttpHeaders headers = response.getHeaders();
        if (headers.containsKey(errorHeader)) {
            List<String> val = headers.get(errorHeader);
            if (val.size() > 0)
                return Boolean.valueOf(val.get(0));
        }
        return false;
    }

    public ResponseEntity<String> getResponse() {
        return response;
    }

    /**
     * Obtiene el cuerpo de la respuesta en forma de {@link Class} indicado.
     *
     * @param clazz La clase indicada.
     * @param <T>   El tipo de la clase el cual es inferido según el parametro de la clase.
     * @return Una instancia correspondiente con el cuerpo de la respuesta.
     * @throws RequestBuilderException Arroja esta excepción en caso de no lograr transformar el cuerpo en el tipo proporcionado.
     */
    public <T> T getBody(Class<T> clazz) throws RequestBuilderException {
        try {
            return mapper.readValue(response.getBody(), clazz);
        } catch (IOException e) {
            throw new RequestBuilderException(
                    "Ha ocurrido un error parseando la respuesta",
                    e,
                    RequestBuilderException.BuildErrorType.RESPONSE_READING_FAILED
            );
        }
    }

    /**
     * Obtiene el cuerpo de la respuesta en forma de {@link TypeReference} indicado.
     *
     * @param typeReference El tipo indicado.
     * @param <T>           Tipo inferido segun proporcionado.
     * @return Una instancia correspondiente con el cuerpo de la respuesta.
     * @throws RequestBuilderException Arroja esta excepción en caso de no lograr transformar el cuerpo en el tipo proporcionado.
     */
    public <T> T getBody(TypeReference<T> typeReference) throws RequestBuilderException {
        try {
            return mapper.readValue(response.getBody(), typeReference);
        } catch (IOException e) {
            throw new RequestBuilderException(
                    "Ha ocurrido un error parseando la respuesta",
                    e,
                    RequestBuilderException.BuildErrorType.RESPONSE_READING_FAILED
            );
        }
    }

    public <T> T extractEntity(Object type) throws FrontManageableException {
        try {
            if (hasError()) {
                throw new FrontManageableException(getError());
            }
            if (type instanceof Class) {
                return getBody((Class<T>) type);
            } else {
                if (type instanceof TypeReference) {
                    return getBody((TypeReference<T>) type);
                } else throw new FrontManageableException(NOT_TYPE_OR_CLASS);
            }
        } catch (RequestBuilderException e) {
            throw new FrontManageableException(e, UNKNOWN);
        }
    }

    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    /**
     * Obtiene el error proporcionado por backend.
     *
     * @return Enumerado indicanto la razón del error.
     * @throws RequestBuilderException Excepción arrojada en caso de que la respuesta no sea del tipo manejado.
     */
    public ResponseErrorEnum getError() throws RequestBuilderException {
        try {
            return mapper.readValue(response.getBody(), ResponseErrorEnum.class);
        } catch (IOException e) {
            throw new RequestBuilderException(
                    "La respuesta no cumple con el formato de errores",
                    e,
                    RequestBuilderException.BuildErrorType.ERROR_READING_FAILED
            );
        }
    }
}
