import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class TestThymeleaf {
    public static void main(String[] args) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(new StringTemplateResolver());
        Context ctx = new Context();
        ctx.setVariable("val", -5L);
        System.out.println(engine.process("<span th:text=\"${#numbers.formatInteger(val, 0, 'COMMA')}\"></span>", ctx));
        System.out.println(engine.process("<span th:text=\"${#numbers.formatInteger(val, 1, 'COMMA')}\"></span>", ctx));
    }
}
