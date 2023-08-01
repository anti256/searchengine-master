package searchengine.controllers;

import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.SiteIndexing;
import searchengine.services.SiteIndexingImpl;
import searchengine.services.StatisticsService;
import searchengine.services.StopIndexing;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.sql.SQLIntegrityConstraintViolationException;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final SiteIndexing siteIndexing;

    private final StopIndexing stopIndexing;

    public ApiController(StatisticsService statisticsService, SiteIndexing siteIndexing, StopIndexing stopIndexing) {
        this.statisticsService = statisticsService;
        this.siteIndexing = siteIndexing;
        this.stopIndexing = stopIndexing;
    }


    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<JSONObject> startIndexing() throws HeuristicRollbackException,
            SystemException, HeuristicMixedException, RollbackException, SQLIntegrityConstraintViolationException {
        return new ResponseEntity<>(siteIndexing.startSitesIndexing(),HttpStatus.OK);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<JSONObject> stopIndexing() {
        return new ResponseEntity<>(stopIndexing.stopSitesIndexing(),HttpStatus.OK);
    }


}



//Запуск полной индексации — GET /api/startIndexing
//Остановка текущей индексации — GET /api/stopIndexing
//Добавление или обновление отдельной страницы — POST /api/indexPage
//Статистика — GET /api/statistics
//Получение данных по поисковому запросу — GET /api/search
//
