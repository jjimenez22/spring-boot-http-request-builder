package ve.jj.requestbuilder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Clase para construir solicitudes sin cuerpo. Puede ser utilizada particularmente pero su finalidad es ser usada por
 * {@link RequestBuilder}.
 */
public class BodylessRequestBuilder {
    //    ********* ATTRIBUTES

    //    URI Builder
    private UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();

    private RequestBuilder container;

    //    URI auxiliar variables
    private String[] pathVars;
    private String basePath;
    private String scheme = "http";
    private boolean pathSet = false;

    private URI uri;
    protected HttpHeaders headers;
    protected HttpMethod method;
    protected MultiValueMap<String, String> params;

//    ********************* STATIC UTILITIES

    /**
     * Construye una {@link URI} codificada con los parámetros proporcionados.
     *
     * @param builder URI builder previamente configurada.
     * @param pathVars Variables de ruta a ser sustituidas en la ruta por orden de aparición.
     * @param params Parámetros de uri a ser anexados.
     * @return La uri codificada.
     */
    public static URI buildAndEncode(UriComponentsBuilder builder, String[] pathVars, MultiValueMap<String, String> params) {
        if (params != null && !params.isEmpty())
            builder.queryParams(params);
        if (pathVars != null && pathVars.length > 0)
            return builder.buildAndExpand((Object[]) pathVars).encode().toUri();
        return builder.build().encode().toUri();
    }

    /**
     *
     * @return Una instancia.
     */
    public static BodylessRequestBuilder newInstance() {
        return new BodylessRequestBuilder();
    }

    /**
     * Método a ser utilizado por {@link RequestBuilder} con una referencia a sí mismo para poder ejecutar la solicitud.
     *
     * @param container Padre.
     * @return Instancia con referencia a su padre.
     */
    static BodylessRequestBuilder newInstance(RequestBuilder container) {
        return new BodylessRequestBuilder(container);
    }

//    ************************* CONSTRUCTORS

    public BodylessRequestBuilder() {
        this.container = null;
        this.pathVars = null;
        this.basePath = null;
        this.uri = null;
        this.headers = null;
        this.method = null;
        this.params = null;
    }

    BodylessRequestBuilder(RequestBuilder container) {
        this.container = container;
        this.pathVars = null;
        this.basePath = null;
        this.uri = null;
        this.headers = null;
        this.method = null;
        this.params = null;
    }

    /**
     * Constructor parametrizado.
     *
     * @param uri Uri a la cual se le realizará la solicitud en caso de provenir de otra fuente.
     * @param headers Headers base.
     * @param params Parámetros de uri a ser sustituidos en orden de aparición.
     * @param method Método HTTP.
     */
    public BodylessRequestBuilder(URI uri, HttpHeaders headers, MultiValueMap<String, String> params, HttpMethod method) {
        this.container = null;
        this.pathVars = null;
        this.basePath = null;
        this.uri = uri;
        this.headers = headers;
        this.method = method;
        this.params = params;
    }

//    ***************** BUILDER METHODS:

    public BodylessRequestBuilder withHost(String host) {
        uriBuilder.host(host);
        return this;
    }

    public BodylessRequestBuilder withPort(Integer port) {
        uriBuilder.port(port);
        return this;
    }

    public BodylessRequestBuilder withPort(String port) {
        uriBuilder.port(port);
        return this;
    }

    /**
     * Se le anexa a la ruta base de haberla.
     *
     * @param path ruta.
     * @return La propia instancia.
     */
    public BodylessRequestBuilder withPath(String path) {
        if (basePath == null) {
            uriBuilder.path(path);
        } else {
            uriBuilder.path(basePath + path);
        }
        pathSet = true;
        return this;
    }

    public BodylessRequestBuilder withPathVars(String... pathVars) {
        this.pathVars = pathVars;
        return this;
    }

    /**
     * Se anexan a los ya existentes de haberlos.
     *
     * @param paramName
     * @param paramValues
     * @return
     */
    public BodylessRequestBuilder withParam(String paramName, Object... paramValues) {
        if (params == null)
            params = new LinkedMultiValueMap<>();

        if (paramValues != null && paramValues.length > 0) {
            for (Object paramValue : paramValues)
                if (paramValue != null) {
                    params.add(paramName, paramValue.toString());
                }
        }

        return this;
    }

    /**
     *  Se le anexan a los ya existentes de haberlos.
     *
     * @param headerName
     * @param headerValue
     * @return
     */
    public BodylessRequestBuilder withHeader(String headerName, String headerValue) {
        if (headers == null)
            headers = new HttpHeaders();
        headers.add(headerName, headerValue);
        return this;
    }

    public BodylessRequestBuilder withHttpMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Construye la solicitud.
     *
     * @return La solicitud construida.
     */
    public RequestEntity build() {
        return new RequestEntity(headers, method, getUri());
    }

    /**
     * Le delega a su padre la ejecución de la solicitud.
     * Está presente para poder mantener un mismo flujo de llamados.
     *
     * @return La respuesta envuelta tal como proviene del padre.
     * @throws RequestBuilderException Excepción proveniente del padre.
     */
    public ResponseWrapper perform() throws RequestBuilderException {
        return container.perform();
    }

//   ************************** GETTERS AND SETTERS:

    public String getBasePath() {
        return basePath;
    }

    /**
     * Ruta base, de estar presente se le anexará la ruta agregada.
     *
     * @param basePath La ruta base.
     * @return La propia instancia.
     */
    public BodylessRequestBuilder setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    /**
     * Pendiente estudiar más esto, hasta donde se entiende es el protocolo, por defecto {@code "http"}
     * @return El esquema.
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Pendiente estudiar más esto, hasta donde se entiende es el protocolo, por defecto {@code "http"}
     *
     * @param scheme El esquema.
     * @return
     */
    public BodylessRequestBuilder setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Construye un objeto {@link URI} con los parámetros seteados, o sólo la retorna en caso de ya haber sido generada.
     *
     * @return La uri construida.
     */
    public URI getUri() {
        if (uri != null) {
            return uri;
        }
        uriBuilder.scheme(scheme);
        if (!pathSet && basePath != null) {
            uriBuilder.path(basePath);
        }
        return buildAndEncode(uriBuilder, pathVars, params);
    }

    public BodylessRequestBuilder setUri(URI uri) {
        this.uri = uri;
        return this;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * Sobreescribe los ya existentes.
     * @param headers
     * @return
     */
    public BodylessRequestBuilder setHeaders(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public BodylessRequestBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public MultiValueMap<String, String> getParams() {
        return params;
    }

    /**
     * Sobreescribe los ya existentes.
     * @param params
     * @return
     */
    public BodylessRequestBuilder setParams(MultiValueMap<String, String> params) {
        this.params = params;
        return this;
    }

    /**
     * El builder de la URI.
     * @return
     */
    public UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
    }
}
