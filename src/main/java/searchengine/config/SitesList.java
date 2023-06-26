package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "indexing-settings")//в application.yaml есть раздел indexing-settings
public class SitesList {
    private List<Site> sites;//считывает в list все данные подраздела sites application.yaml
}
