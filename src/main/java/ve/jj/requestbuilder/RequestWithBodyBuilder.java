package ve.jj.requestbuilder;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * Clase para construir solicitudes con cuerpo. Puede ser utilizada particularmente pero su finalidad es ser usada por
 * {@link RequestBuilder}.
 */
public class RequestWithBodyBuilder extends BodylessRequestBuilder {

//    ********* ATTRIBUTES

    private Object body;

    //    ********************* STATIC UTILITIES

    public static RequestWithBodyBuilder newInstance() {
        return new RequestWithBodyBuilder();
    }

    public static RequestWithBodyBuilder newInstance(RequestBuilder container) {
        return new RequestWithBodyBuilder(container);
    }

    //    ************************* CONSTRUCTORS

    public RequestWithBodyBuilder() {
        super();
        body = null;
    }

    public RequestWithBodyBuilder(RequestBuilder container) {
        super(container);
        body = null;
    }

    public RequestWithBodyBuilder(URI uri, HttpHeaders headers, MultiValueMap<String, String> params, HttpMethod method, Object body) {
        super(uri, headers, params, method);
        this.body = body;
    }

//    ******************* BUILDER METHODS

    public RequestWithBodyBuilder withBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public RequestWithBodyBuilder withHost(String host) {
        super.withHost(host);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withPort(Integer port) {
        super.withPort(port);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withPort(String port) {
        super.withPort(port);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withPath(String path) {
        super.withPath(path);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withPathVars(String... pathVars) {
        super.withPathVars(pathVars);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withParam(String paramName, Object... paramValues) {
        super.withParam(paramName, paramValues);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withHeader(String headerName, String headerValue) {
        super.withHeader(headerName, headerValue);
        return this;
    }

    @Override
    public RequestWithBodyBuilder withHttpMethod(HttpMethod method) {
        super.withHttpMethod(method);
        return this;
    }

    @Override
    public RequestEntity build() {
        return new RequestEntity<>(body, headers, method, getUri());
    }
//    ********************** GETTER AND SETTER


    public Object getBody() {
        return body;
    }

    public RequestWithBodyBuilder setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public RequestWithBodyBuilder setBasePath(String basePath) {
        super.setBasePath(basePath);
        return this;
    }

    @Override
    public RequestWithBodyBuilder setUri(URI uri) {
        super.setUri(uri);
        return this;
    }

    @Override
    public RequestWithBodyBuilder setHeaders(HttpHeaders headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public RequestWithBodyBuilder setMethod(HttpMethod method) {
        super.setMethod(method);
        return this;
    }

    @Override
    public RequestWithBodyBuilder setParams(MultiValueMap<String, String> params) {
        super.setParams(params);
        return this;
    }
}
