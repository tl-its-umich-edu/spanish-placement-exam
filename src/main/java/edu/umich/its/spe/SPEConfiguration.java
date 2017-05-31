package edu.umich.its.spe;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.umich.its.spe.PersistString;

@Configuration
public class SPEConfiguration {
	  @Bean(name = "persistString")
    public PersistString persistString() throws PersistStringException {
        return new PersistStringImpl("/my/path");
    }
}
//@Configuration 
//public class Config {
//
//    @Bean(name = "redapple")
//    public Apple redApple() {
//        return new Apple();
//    }
//
//    @Bean(name = "greeapple")
//    public Apple greenApple() {
//        retturn new Apple();
//    }
//
//    @Bean(name = "appleCook")
//    public Cook appleCook() {
//        return new Cook("iron Fruit", redApple());
//    }
//    ...
//}