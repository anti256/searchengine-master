package searchengine.services;

import lombok.RequiredArgsConstructor;
import model.Page;
import model.StatusIndexing;
import org.springframework.stereotype.Service;

import java.util.Date;

import static searchengine.Application.session;



public class addTenDBrecords {

    public addTenDBrecords() {
        for (int i = 0; i < 10; i++) {
            model.Site defaultSite = new model.Site();
            defaultSite.setUrl("https://www.site" + i + ".ru");
            defaultSite.setName("site" + i + ".ру");
            defaultSite.setStatus(StatusIndexing.INDEXED);
            defaultSite.setStatusTime(new Date());
            session.persist(defaultSite);

            //System.out.println(defaultSite.getId());

            model.Page defaultPage1 = new model.Page();
            model.Page defaultPage2 = new model.Page();
            defaultPage1.setCode(200);
            defaultPage2.setCode(200);
            defaultPage1.setSiteId(defaultSite.getId());
            defaultPage2.setSiteId(defaultSite.getId());
            defaultPage1.setContent("anything");
            defaultPage2.setContent("anything");
            defaultPage1.setPath("/news" + i + "/01");
            defaultPage2.setPath("/news" + i + "/02");
            session.persist(defaultPage1);
            session.persist(defaultPage2);
        }

    }

}
