import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        JavaxServletWebApplication jswa = JavaxServletWebApplication.buildApplication(this.getServletContext());
        WebApplicationTemplateResolver resolver = new WebApplicationTemplateResolver(jswa);

        //FileTemplateResolver resolver = new FileTemplateResolver();
        //resolver.setPrefix("D:/new/java/goit/javaDev/hw9-cookie/templates/");

        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String timezone = req.getParameter("timezone");

        if (timezone == null) {
            if(req.getCookies() != null) {
                timezone = Arrays.stream(req.getCookies())
                        .filter(it -> it.getName().equals("lastTimezone"))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse("UTC");
            } else{
                timezone = "UTC";
            }
        } else {
            timezone = timezone.replace(" ", "+");
            resp.addCookie(new Cookie("lastTimezone", timezone));
        }

        String dateTime = LocalDateTime.now(ZoneId.of(timezone)).atZone(ZoneId.of(timezone))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));

        resp.setContentType("text/html; charset=utf-8");

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", dateTime)
        );

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
