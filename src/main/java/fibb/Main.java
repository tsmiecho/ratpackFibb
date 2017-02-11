package fibb;

import ratpack.exec.Promise;
import ratpack.http.client.HttpClient;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.RatpackServer;

import java.net.InetAddress;
import java.net.URI;
import java.time.Duration;

/**
 * @author Tomasz Smiechowicz
 */
public class Main {
    public static void main(String... args) throws Exception {
        HttpClient httpClient = HttpClient.of(httpClientSpec -> httpClientSpec.readTimeout(Duration.ofMinutes(2)));
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
                .serverConfig(serverConfigBuilder -> serverConfigBuilder
                        .development(false)
                        .port(5050)
                        .address(InetAddress.getByName("0.0.0.0"))//dlaczego tu jest tak - on powiedzial
                        .threads(4))
                .handlers(
                    chain -> chain
                            .prefix("fibb",
                                fibb -> fibb
                                        .get(":value", ctx -> {
                                            final Long n = Long.parseLong(ctx.getAllPathTokens().get("value"));
                                            if(n < 3){
                                                ctx.render("1");
                                            }
                                            else {
                                                final Promise<ReceivedResponse> receivedResponsePromiseFibbN_1 = httpClient.get(new URI("http://localhost:5050/fibb/" + (n - 1)));
                                                final Promise<Long> promisedFibbN_1 = receivedResponsePromiseFibbN_1.map(bodyResponse -> Long.parseLong(bodyResponse.getBody()
                                                                                                                                                                .getText()));
                                                final Promise<ReceivedResponse> receivedResponsePromiseFibbN_2 = httpClient.get(new URI("http://localhost:5050/fibb/" + (n - 2)));
                                                final Promise<Long> promisedFibbN_2 = receivedResponsePromiseFibbN_2.map(bodyResponse -> Long.parseLong(bodyResponse.getBody()
                                                                                                                                                                .getText()));
                                                promisedFibbN_1.then(fibbN_1 -> promisedFibbN_2.then(fibbN_2 -> ctx.render(String.valueOf(fibbN_1 + fibbN_2))));
                                            }
                                        }))
                            .prefix("hello",
                                hello -> hello
                                        .get(":value", ctx -> {
                                            final String val = ctx.getAllPathTokens().get("value");
                                            final String key = ctx.getRequest().getQueryParams().get("key");
                                            ctx.render("hello" + val + key);
                                        }))
                                        .post(ctx -> ctx.render("post")) //tutaj na razie jest 405
        ));
    }

    private static int generate(int index) {
        if(index < 0){
            throw new RuntimeException();
        }
        if(index == 0){
            return 0;
        }
        if(index == 1){
            return 1;
        }
        return generateSequenceElement(0, 1, index - 1);
    }

    static int generateSequenceElement(int lastValue, int currentValue, int howMany){
        if(howMany == 0){
            return currentValue;
        }
        return generateSequenceElement(currentValue, lastValue + currentValue, howMany - 1);
    }
}
