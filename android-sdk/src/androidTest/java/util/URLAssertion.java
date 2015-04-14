package util;

import android.net.Uri;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class URLAssertion extends AbstractAssert<URLAssertion, URL> {

    public URLAssertion(URL actual) {
        super(actual, URLAssertion.class);
    }

    public static URLAssertion assertThat(URL actual){
        return new URLAssertion(actual);
    }

    public static URLAssertion assertThat(String urlString){
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Assertions.fail("The provided String is not a valid URL");
        }
        return new URLAssertion(url);
    }

    public URLAssertion hasGetParameter(String key, String value){
        isNotNull();

        Assertions.assertThat(actual.getQuery())
                .overridingErrorMessage("No query found. %s",actual.toString())
                .isNotNull();


        Map<String, String> parameters = getParametersAsMap(actual);
        Assertions.assertThat(parameters.keySet().size())
                .overridingErrorMessage("no parameters found at all. %s wa also not set", actual.toString())
                .isGreaterThan(0);

        Assertions.assertThat(parameters.get(key))
                .overridingErrorMessage("The key was not found in the query string")
                .isNotNull();

        Assertions.assertThat(parameters.get(key))
                .overridingErrorMessage("Specified query value does not match \"%s\" dont´t match \"%s\"", parameters.get(key), value)
                .isEqualTo(value);




        return this;
    }

    private Map<String, String> getParametersAsMap(URL actual) {
        Map<String, String> value = new HashMap<String, String>();
        if (actual.getQuery() != null) {
            String[] parameters = actual.getQuery().split("&");
            for (String parameter : parameters) {
                String parameterKey = parameter.split("=")[0];
                String parameterValue = parameter.split("=")[1];
                value.put(parameterKey, parameterValue);
            }
        }
        return value;
    }

    public URLAssertion isHTTPS(){
        isNotNull();

        Assertions.assertThat(actual.getProtocol()).isEqualTo("https")
                .overridingErrorMessage("URL is not https, it is  '%s'", actual.getProtocol());

        return this;
    }

    public URLAssertion isAtPath(String path) {
        Assertions.assertThat(actual.getPath())
                //.overridingErrorMessage("path does not match. Path is '%s', should be '%s'", actual.getPath(), path)
                .isEqualToIgnoringCase(path);
        return this;
    }

    public URLAssertion isAtHost(String host) {
        Assertions.assertThat(actual.getHost())
                .overridingErrorMessage("host does not match. Host is '%s', should be '%s',",actual.getHost(), host)
                .isEqualToIgnoringCase(host);
        return this;
    }

    public URLAssertion hasNoGetParameter(String parameter) {
        String value = getParametersAsMap(actual).get(parameter);
        Assertions.assertThat(value)
                .overridingErrorMessage("The query parameter is set to " + value )
                .isNull();
        return this;
    }

    public URLAssertion hasGetParameterThatStartsWith(String parameter, String value) {
        String parameterContent = getParametersAsMap(actual).get(parameter);
        Assertions.assertThat(value).isNotNull()
                .startsWith(value)
                .overridingErrorMessage("The parameter does not start with \"%s\", it is \"%s\" instead ", value, parameterContent);
        return this;
    }

    public URLAssertion hasGetParameterThatContains(String parameter, String value) {
        String parameterContent = getParametersAsMap(actual).get(parameter);
        Assertions.assertThat(value).isNotNull()
                .contains(value)
                .overridingErrorMessage("The parameter does not contain with \"%s\", it is \"%s\" instead ", value, parameterContent);
        return this;
    }

    public URLAssertion pathBeginsWith(String path) throws UnsupportedEncodingException {
        Assertions.assertThat(actual.getPath())
                .overridingErrorMessage("path does does not start with \"%s\" it is \"%s\"", path, actual.getPath())
                .startsWith(path);
        return this;
    }

    public URLAssertion pathEndsWith(String path) throws UnsupportedEncodingException {
        Assertions.assertThat(actual.getPath())
                .overridingErrorMessage("path does does not end with \"%s\" it is \"%s\"", path, actual.getPath())
                .endsWith(path);
        return this;
    }
    public URLAssertion pathContains(String path) throws UnsupportedEncodingException {
        path  = Uri.encode(path);
        Assertions.assertThat(actual.getPath())
                .overridingErrorMessage("path does does not contain \"%s\" it is \"%s\"", path, actual.getPath())
                .contains(path);
        return this;
    }
}