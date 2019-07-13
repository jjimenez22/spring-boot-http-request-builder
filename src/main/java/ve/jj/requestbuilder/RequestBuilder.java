package ve.jj.requestbuilder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * Clase para construir y realizar solicitudes REST.
 */
public class RequestBuilder {

    //    ********* ATTRIBUTES

    private String host; //host por defecto
    private String path; // path por defecto
    private Integer port; // puerto por defecto en entero
    private String sPort; // puerto por defecto en String
    private HttpHeaders headers; // headers por defecto
    private MultiValueMap<String, String> params; // parametros parametros de uri por defecto

    private BodylessRequestBuilder bodilessBuilder; // constructor de solicitudes sin cuerpo
    private RequestWithBodyBuilder fullBuilder; // constructor de solicitudes con cuerpo

    private RestTemplate rest; // ejecutor de solicitudes

    private String errorHeader = "ERROR"; // header con bandera de error proporcionado por backend.

//    ************************* CONSTRUCTORS

    /**
     * Constructor sin parametros
     */
    public RequestBuilder() {
        host = null;
        path = null;
        port = null;
        sPort = null;
        headers = null;
        params = null;
        bodilessBuilder = null;
        fullBuilder = null;
        rest = new RestTemplate();
    }

    /**
     * Constructor parametrizdo con el puerto {@link Integer}. En caso de necesitarse solo algunos campos por defecto se deben
     * pasar {@code null}. En caso de ser {@code null} el puerto se debe castear el {@code null} a {@link Integer} para romper la ambigüedad de
     * constructores, Ejemplo: {@code (Integer) null}.
     * <p>
     * Estos valores por defectos serán usados para todas las solicitudes construidas con la misma instancia.
     *
     * @param defaultHost     Host por defecto (representa lo que va antes del primer slash '/' excluyendo el protocolo).
     *                        Si se setea este campo no será necesario setear host más adelante, si se setea host más
     *                        adelante este sobreescibirá al host por defecto.
     *                        Ejemplo: "localhost", "10.1.27.100", "www.google.com".
     * @param defaultBasePath Ruta base relativa al host, si se setea este campo más adelante el path será agregado a esta.
     *                        Ejemplo: "/api/status".
     * @param defaultPort     Puerto por defecto. Si se setea este campo no será necesario setear puerto más adelante, si se
     *                        setea puerto más adelante este sobreescibirá al puerto por defecto.
     *                        Ejemplo: 8080.
     * @param defaultHeaders  Headers por defecto.
     * @param defaultParams   Parametros de uri por defecto (representan lo que viene despues del signo de interrogación
     *                        Ejemplo: ?var1=val1&var2=val2).
     */
    public RequestBuilder(String defaultHost, String defaultBasePath, Integer defaultPort, HttpHeaders defaultHeaders, MultiValueMap<String, String> defaultParams) {
        this.host = defaultHost;
        this.path = defaultBasePath;
        this.port = defaultPort;
        this.sPort = null;
        this.headers = defaultHeaders;
        this.params = defaultParams;
        bodilessBuilder = null;
        fullBuilder = null;
        rest = new RestTemplate();
        initResttemplate();
    }

    /**
     * Inicializador de Rest template, para codifiacion con UTF-8
     */
    public void initResttemplate() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(true);
        for (int i = 0; i < rest.getMessageConverters().size(); i++) {
            if (rest.getMessageConverters().get(i) instanceof StringHttpMessageConverter) {
                rest.getMessageConverters().remove(i);
                rest.getMessageConverters().add(i, stringHttpMessageConverter);
                break;
            }
        }
    }

    /**
     * Constructor parametrizdo con el puerto {@link String}. En caso de necesitarse solo algunos campos por defecto se deben
     * pasar {@code null}. En caso de ser {@code null} el puerto se debe castear el {@code null} a Integer para romper la ambigüedad de
     * constructores, Ejemplo: {@code (String) null}.
     * <p>
     * Estos valores por defectos serán usados para todas las solicitudes construidas con la misma instancia.
     *
     * @param defaultHost     Host por defecto (representa lo que va antes del primer slash '/' excluyendo el protocolo).
     *                        Si se setea este campo no será necesario setear host más adelante, si se setea host más
     *                        adelante este sobreescibirá al host por defecto.
     *                        Ejemplo: "localhost", "10.1.27.100", "www.google.com".
     * @param defaultBasePath Ruta base relativa al host, si se setea este campo más adelante el path será agregado a esta.
     *                        Ejemplo: "/api/status".
     * @param defaultPort     Puerto por defecto. Si se setea este campo no será necesario setear puerto más adelante, si se
     *                        setea puerto más adelante este sobreescibirá al puerto por defecto.
     *                        Ejemplo: "8080".
     * @param defaultHeaders  Headers por defecto.
     * @param defaultParams   Parametros de uri por defecto (representan lo que viene despues del signo de interrogación
     *                        Ejemplo: ?var1=val1&var2=val2).
     */
    public RequestBuilder(String defaultHost, String defaultBasePath, String defaultPort, HttpHeaders defaultHeaders, MultiValueMap<String, String> defaultParams) {
        this.host = defaultHost;
        this.path = defaultBasePath;
        this.sPort = defaultPort;
        this.port = null;
        this.headers = defaultHeaders;
        this.params = defaultParams;
        bodilessBuilder = null;
        fullBuilder = null;
        rest = new RestTemplate();
        initResttemplate();
    }

    //    ***************** BUILDER METHODS:

    /**
     * Setea los valores por defecto en el constructor de la solicitud.
     */
    private void setDefaults() {
        BodylessRequestBuilder builder;
        if (fullBuilder == null) {
            builder = bodilessBuilder;
        } else
            builder = fullBuilder;

        if (host != null)
            builder.withHost(host);
        if (path != null )
            builder.setBasePath(path);
        if (sPort != null) {
            builder.withPort(sPort);
        } else if (port != null)
            builder.withPort(port);
        if (headers != null)
            builder.setHeaders(headers);
        if (params != null)
            builder.setParams(params);

        initResttemplate();
    }

    /**
     * Instancia un constructor con body.
     */
    private void withBody() {
        bodilessBuilder = null;
        fullBuilder = RequestWithBodyBuilder.newInstance(this);
        setDefaults();
    }

    /**
     * Instancia un constructor sin body.
     */
    private void withoutBody() {
        fullBuilder = null;
        bodilessBuilder = BodylessRequestBuilder.newInstance(this);
        setDefaults();
    }

    /**
     * Inicia la construcción de una solicitud GET.
     *
     * @return Constructor de solicitudes sin body con los campos por defecto seteados de antemano y el metodo http GET.
     */
    public BodylessRequestBuilder get() {
        withoutBody();
        return bodilessBuilder.withHttpMethod(HttpMethod.GET);
    }

    /**
     * Inicia la construcción de una solicitud DELETE.
     *
     * @return Constructor de solicitudes sin body con los campos por defecto seteados de antemano y el metodo http DELETE.
     */
    public BodylessRequestBuilder delete() {
        withoutBody();
        return bodilessBuilder.withHttpMethod(HttpMethod.DELETE);
    }

    /**
     * Inicia la construcción de una solicitud HEAD.
     *
     * @return Constructor de solicitudes sin body con los campos por defecto seteados de antemano y el metodo http HEAD.
     */
    public BodylessRequestBuilder head() {
        withoutBody();
        return bodilessBuilder.withHttpMethod(HttpMethod.HEAD);
    }

    /**
     * Inicia la construcción de una solicitud OPTIONS.
     *
     * @return Constructor de solicitudes sin body con los campos por defecto seteados de antemano y el metodo http OPTIONS.
     */
    public BodylessRequestBuilder options() {
        withoutBody();
        return bodilessBuilder.withHttpMethod(HttpMethod.OPTIONS);
    }

    /**
     * Inicia la construcción de una solicitud POST.
     *
     * @return Constructor de solicitudes con body con los campos por defecto seteados de antemano y el metodo http POST.
     */
    public RequestWithBodyBuilder post() {
        withBody();
        return fullBuilder.withHttpMethod(HttpMethod.POST);
    }

    /**
     * Inicia la construcción de una solicitud PUT.
     *
     * @return Constructor de solicitudes con body con los campos por defecto seteados de antemano y el metodo http PUT.
     */
    public RequestWithBodyBuilder put() {
        withBody();
        return fullBuilder.withHttpMethod(HttpMethod.PUT);
    }

    /**
     * Inicia la construcción de una solicitud PATCH.
     *
     * @return Constructor de solicitudes con body con los campos por defecto seteados de antemano y el metodo http PATCH.
     */
    public RequestWithBodyBuilder patch() {
        withBody();
        return fullBuilder.withHttpMethod(HttpMethod.PATCH);
    }

    /**
     * Debería verificar si la solicitud está lista para realizarse, más pruebas son necesarias para asegurar que todas
     * las validaciones pertinentes están contempladas.
     * Por ahora sólo se asegura de que alguno de los builders esté instanciado.
     *
     * @return Si esta listo para realizar la solicitud o no.
     */
    public Boolean isReadyToPerform() {
        return bodilessBuilder != null || fullBuilder != null;
    }

    /**
     * Construye y ejecuta la solicitud, y recibe y envuelve la respuesta.
     *
     * @return La respuesta de la solicitud envuelta en un {@link ResponseWrapper}
     * @throws RequestBuilderException Arroja esta excepción en caso de que el RestTemplate falle al realizar la solicitud
     *                                 o falle la validación de {@code isReadyToPerform()}
     */
    ResponseWrapper perform() throws RequestBuilderException {
        if (isReadyToPerform()) {
            RequestEntity request = (bodilessBuilder == null ? fullBuilder.build() : bodilessBuilder.build());
            try {
                return new ResponseWrapper(rest.exchange(request, String.class), errorHeader);
            } catch (RestClientException e) {
                String requestURI = (bodilessBuilder == null ? fullBuilder : bodilessBuilder).getUri().toString();
                throw new RequestBuilderException(
                        "Ha ocurrido un error realizando la solicitud a: " + requestURI, e,
                        RequestBuilderException.BuildErrorType.REQUEST_FAILED
                );
            }
        } else {
            throw new RequestBuilderException(
                    "Se intenta realizar la Solicitud HTTP sin estar lista",
                    RequestBuilderException.BuildErrorType.NO_REQUEST_BUILT
            );
        }
    }

//   ************************** GETTERS AND SETTERS:

    public String getDefaultHost() {
        return host;
    }

    public RequestBuilder setDefaultHost(String host) {
        this.host = host;
        return this;
    }

    public String getDefaultBasePath() {
        return path;
    }

    public RequestBuilder setDefaultBasePath(String path) {
        this.path = path;
        return this;
    }

    public Integer getDefaultPortInteger() {
        return port;
    }

    public RequestBuilder setDefaultPort(Integer port) {
        this.port = port;
        this.sPort = port.toString();
        return this;
    }

    public String getDefaultPortString() {
        return sPort;
    }

    public RequestBuilder setDefaultPort(String port) {
        this.sPort = sPort;
        this.port = port != null ? Integer.valueOf(port) : null;
        return this;
    }

    public HttpHeaders getDefaultHeaders() {
        return headers;
    }

    public RequestBuilder setDefaultHeaders(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public RequestBuilder addDefaultHeader(String headerName, String headerValue) {
        if (headers == null)
            headers = new HttpHeaders();
        headers.add(headerName, headerValue);
        return this;
    }

    public MultiValueMap<String, String> getDefaultParams() {
        return params;
    }

    public RequestBuilder setDefaultParams(MultiValueMap<String, String> params) {
        this.params = params;
        return this;
    }

    public RequestBuilder addDefaultParam(String paramName, String paramValue) {
        if (params == null)
            params = new LinkedMultiValueMap<>();
        params.add(paramName, paramValue);
        return this;
    }

    public RestTemplate getRestTemplate() {
        return rest;
    }

    public String getErrorHeader() {
        return errorHeader;
    }
}
