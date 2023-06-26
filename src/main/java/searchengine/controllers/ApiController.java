package searchengine.controllers;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.SiteIndexingImpl;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;

    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<JSONObject> startIndexing() {
        return new ResponseEntity<>(new SiteIndexingImpl().startSitesIndexing(), HttpStatus.OK);
    }
//    public JSONObject startIndexing(){
//        JSONObject dataJson = new JSONObject();//создание json-объекта
//        if (indexingState == true){ //если индексация запущена
//            //добавление строк в объект json-линии
//            dataJson.put("result", false);
//            dataJson.put("error", "Индексация уже запущена");
//            return dataJson;}
//        dataJson.put("result", true);
//        return dataJson;
//    }

}



//Запуск полной индексации — GET /api/startIndexing
//Остановка текущей индексации — GET /api/stopIndexing
//Добавление или обновление отдельной страницы — POST /api/indexPage
//Статистика — GET /api/statistics
//Получение данных по поисковому запросу — GET /api/search
//
