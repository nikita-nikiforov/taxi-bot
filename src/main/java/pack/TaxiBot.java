package pack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
public class TaxiBot extends SpringBootServletInitializer
        implements WebApplicationInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(TaxiBot.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TaxiBot.class, args);
        System.out.println("Hello!");
    }


}
