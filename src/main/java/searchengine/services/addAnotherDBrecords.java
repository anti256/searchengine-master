package searchengine.services;

import model.StatusIndexing;
import searchengine.config.Site;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static searchengine.Application.session;



public class addAnotherDBrecords {

    public addAnotherDBrecords(List<Site> inputArray) {
        for (int i = 0; i < inputArray.size(); i++) {
            model.Site defaultSite = new model.Site();
            defaultSite.setUrl(inputArray.get(i).getUrl());
            defaultSite.setName(inputArray.get(i).getName());
            defaultSite.setStatus(StatusIndexing.INDEXED);
            defaultSite.setStatusTime(new Date());
            System.out.println(defaultSite.getUrl());
            System.out.println(defaultSite.getName());
            session.persist(defaultSite);
        }



        for (int i = 0; i < 10; i++) {
            model.Site defaultSite = new model.Site();
            defaultSite.setUrl("https://www.site" + i + ".ru");
            defaultSite.setName("site" + i + ".ру");
            defaultSite.setStatus(StatusIndexing.INDEXED);
            defaultSite.setStatusTime(new Date());
            session.persist(defaultSite);

            //System.out.println(defaultSite.getId());

            /*model.Page defaultPage1 = new model.Page();
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
            session.persist(defaultPage2);*/
        }

        List<model.Site> fromDBsites = new ArrayList<>();
        fromDBsites = session.createQuery("from Site", model.Site.class).list();
        for (int i = 0; i < fromDBsites.size(); i++) {
            model.Page defaultPage1 = new model.Page();
            model.Page defaultPage2 = new model.Page();
            defaultPage1.setCode(200);
            defaultPage2.setCode(200);
            defaultPage1.setSiteId(fromDBsites.get(i).getId());
            defaultPage2.setSiteId(fromDBsites.get(i).getId());
            defaultPage1.setContent("anything");
            defaultPage2.setContent("anything");
            defaultPage1.setPath("/news" + i + "/01");
            defaultPage2.setPath("/news" + i + "/02");
            session.persist(defaultPage1);
            session.persist(defaultPage2);
            System.out.println("fromDBsites - " + fromDBsites.get(i).getName());
        }


    }

}
