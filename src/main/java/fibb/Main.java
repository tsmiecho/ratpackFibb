package fibb;

import ratpack.server.RatpackServer;

import java.net.InetAddress;

/**
 * @author Tomasz Smiechowicz
 */
public class Main {
    public static void main(String... args) throws Exception {
        RatpackServer.start(ratpackServerSpec -> ratpackServerSpec
                .serverConfig(serverConfigBuilder -> serverConfigBuilder
                        .development(false)
                        .port(8080)
                        .address(InetAddress.getByName("0.0.0.0"))//dlaczego tu jest tak - on powiedzial
                        .threads(4))
                .handlers(
                    chain -> chain
                            .prefix("fibb",
                                fibb -> fibb
                                        .get(":value", ctx -> {
                                            final String val = ctx.getAllPathTokens().get("value");
                                            ctx.render(String.valueOf(generate(Integer.valueOf(val))));
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
