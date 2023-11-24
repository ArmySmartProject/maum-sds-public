package maum.brain.sds.analysis.config;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//import java.security.AuthProvider;

@Configuration
//@EnableWebSecurity
//@EnableGlobalAuthentication
@ComponentScan(basePackages = {"maum.brain.sds.analysis.*"})
public class SpringSecurityConfig
//        extends WebSecurityConfigurerAdapter
{

//    @Autowired
//    AuthProvider authProvider;
//
//    @Autowired
//    AuthFailureHan

}
