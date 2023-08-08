package searchengine.services.SiteIndexingUnderHood;
import model.Page;
import model.StatusIndexing;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static searchengine.Application.session;

public class UrlListFromSite {

    private String urlSite;//начальная ссылка поиска
    private ArrayList<String> urlReadyList = new ArrayList<>(); //список обработанных ссылок, который будет на выходе
    private volatile ArrayList<String> todoTaskList = new ArrayList<>();//список необработанных ссылок
    private model.Site site;

    public UrlListFromSite(model.Site site){//конструктор
        this.site = site;
        String urlString = site.getUrl();//.replace("https://", "http://");
        urlSite = urlString.replaceAll("www.","");//убираем из ссылки www     https://www.mtrele.ru -> https://mtrele.ru
        todoTaskList.add(urlSite);//добавляем ссылку в список на выполнение                                            https://mtrele.ru
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();//очередь
        while (!todoTaskList.isEmpty()) {//работаем пока список необработанных ссылок не пустой
            String defaultUrl = todoTaskList.get(0);
            String minusStart = defaultUrl.replaceAll(urlSite, "").equals("") ? "/":
                    defaultUrl.replaceAll(urlSite, "") ;
            if (!isExistInPageBDbyUrl(minusStart, site.getId()))
                    //!urlReadyList.contains(todoTaskList.get(0)))
            {//проверяем, что первой ссылки нет в списке обработанных ссылок
                //вытаскиваем первую в списке ссылку
                //строку-ссылку присваиваем переменной, чтоб далее программа не тратила время на открытие списка
                Transaction transaction = session.beginTransaction();
                try
                {
                    site.setStatus(StatusIndexing.INDEXING);
                    System.out.println("defaultUrl = todoTaskList.get(0) " + defaultUrl);
                    urlReadyList.add(defaultUrl);//заносим ссылку в список обработанных
                    //connect - подключение к html в инете, get - парсинг, создается документ,
                    // maxBodySize(0) - снимает ограничение на размер скачиваемых данных
                    Document doc = Jsoup.connect(defaultUrl.replace("https://", "http://")).maxBodySize(0).get();

                    model.Page defPage = new Page();
                    defPage.setSite1(site);
                    defPage.setCode(200);
                    defPage.setPath((defaultUrl.replaceAll(urlSite,  "").equals("")) ? "/" :
                            defaultUrl.replaceAll(urlSite,  ""));
                    defPage.setContent(doc.select("html").toString());
                    site.setStatusTime(new Date());
                    System.out.println("++++Заносится в базу " + defPage.getPath() + ", site - " + defPage.getSite1().getId());
                    session.persist(defPage);
                    session.update(site);
                    transaction.commit();

                    //из документа выбираются все элементы с тегами-адресом a[href]
                    //elements фактически является динамическим массивом нарезок hml-кода с данными по линиям
                    Elements elements = doc.select("a[href]");
                    System.out.println("elements.size = " + elements.size());
                    TaskUrlSite taskUrl = null;//создаем экземпляр задачи
//                    System.out.println("<<<<<<<<<<<<" + elements.toString());
//                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    todoTaskList.addAll(forkJoinPool.invoke(new TaskUrlSite(elements)));//заносим экземпляр задачи в пул
                    //todoTaskList.remove(defaultUrl);//удаляем отработанную ссылку из списка
                    /*Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    for (Thread th:threadSet
                         ) {
                        System.out.println("поток " + th.getName() + "  " + th.getId() + "  " + th.toString());
                    }*/
                }//конец try
                catch (IOException ex) {
                    System.out.println("ашипка!");
                    //ex.getStackTrace();
                    model.Page defPage = new Page();
                    defPage.setSite1(site);
                    defPage.setCode(405);
                    defPage.setPath((defaultUrl.replaceAll(urlSite,  "").equals("")) ? "/" :
                            defaultUrl.replaceAll(urlSite,  ""));
                    defPage.setContent(" ");
                    System.out.println("++++Заносится в базу " + defPage.getPath() + ", site - " + defPage.getSite1().getId());
                    session.persist(defPage);
                    site.setStatusTime(new Date());
                    site.setStatus(StatusIndexing.FAILED);
                    site.setLastError("Не удалось прочитать данные");
                    System.err.println(ex.toString());
                    session.update(site);
                    transaction.commit();
                }
            } /*else{
                System.out.println("url в базе - удаляем из todoTaskList");
                //todoTaskList.remove(0);//если ссылка есть в списке обработанных, она просто удаляется из списка
            }*/
        todoTaskList.remove(0);
        }
    }

    //геттер готового списка
    public Boolean getUrlReadyList() {
        return true;
    }

    //------------------------------------------------------------------------
    //класс-поток-task
    public class TaskUrlSite extends RecursiveTask<List<String>> {//на выходе будет список строк
        List<Element> elements;//список элементов, полученных при обработке ссылки из списка

        public TaskUrlSite(List<Element> elementList) throws IOException {//конструктор
            this.elements = elementList;
        }

        @Override
        protected List<String> compute() {//тело задачи, тип - то, что на выходе
            List<String> urlReadyToTodo = new ArrayList<>();//локальный список правильных обработанных ссылок

            System.out.println("<<<<<<<<<<<<" + elements.size() + "шт." + elements.toString());
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            if (elements.size() < 2){//если в локальном списке задач элементов меньше 2, т.е. для каждого элемента делаем свой поток
                for (Element el:elements) {//перебор элементов
                    String urlStr = el.absUrl("href");//.replace("https://", "http://");//из конкретного элемента вытаскиваем ссылку
                    if (todoTaskList.contains(urlStr)){continue;}
                    urlStr = urlSite.replaceFirst("https://", "http://");
                    System.err.println("~~~Начальный urlStr - " + urlStr);
                    //если элемент пустой либо состоит только из /, делаем из него начальный url с http
                    if ((urlStr.isEmpty()) || ((urlStr.charAt(0) == '/') && ((urlStr.length() < 2) || (urlStr.charAt(1) != '/')))) {
                        urlStr = urlSite.replaceFirst("https://", "http://");//.concat(urlStr);
                    }
                    System.out.println("~~~href - " + urlStr);
                    urlStr = urlStr.replaceAll("www.", "");//делаем ссылку с http и без www
                    System.out.println("~~~urlStr (-www.) - " + urlStr);
                    //добавляем в список только ссылки с данного сайта
                    //из ссылки с http и без www вычетаем начальную ссылку с http и без www
                    String minusUrl = urlStr.replaceAll(urlSite.replace("https://", "http://"), "");
                    System.out.println("~~~urlSite (замена https на http) - " + urlSite.replace("https://", "http://"));
                    System.out.println("~~~minusUrl (urlStr минус urlSite(http))- " + minusUrl);

                    if (minusUrl.length() == (urlStr.length() - urlSite.replace("https://", "http://").length())) {
                        //если ссылка с нужного сайта
                        //проверяем на наличие # - если есть, после замены от строки ничего не останется
                        String minusResh = urlStr.replaceAll("[^#]+[#]{1}[^#]*", "");
                        System.out.println("~~~minusResh (для проверки на внутреннюю ссылку #) - " + minusResh);
                        if (!minusResh.equals("")) {
                            System.out.println("~~~minusResh.equals(\"\") - " + minusResh.equals(""));
                            System.out.println("~~~!isExistInPageBDbyUrl(urlStr, site.getId()) - " + !isExistInPageBDbyUrl(urlStr, site.getId()));
                            System.out.println("~~~!todoTaskList.contains(urlStr) - " + !todoTaskList.contains(urlStr));
                            //проверяем на наличие полученной ссылки в БД и в списке необработанных ссылок
                            //сюда urlStr попадает без www и c http://
                            if (!isExistInPageBDbyUrl(minusUrl, site.getId())) {
                                urlReadyToTodo.add(urlStr);//добаляем ссылку в список необработанных
                                      System.out.println(urlReadyList.size() + "---" + urlStr + "\t\t" + Thread.currentThread().getName() + "=== " +
                                                todoTaskList.size());
                            }
                        }
                    }
                }//конец цикла
            } else {//если в локальном списке задач элементов больше заданного количества
                try {//создаем две подзадачи, каждому свою часть списка
                    int a1 = elements.size() / 2;
                    int b = elements.size() - 1;
                    TaskUrlSite firstPart = new TaskUrlSite(elements.subList(0, a1));
                    TaskUrlSite secondPart = new TaskUrlSite(elements.subList(a1, b + 1));
                    firstPart.fork();//добавляем задачи в пул
                    secondPart.fork();
                    urlReadyToTodo.addAll(secondPart.join());//запускаем задачи и добавляем их результаты в локальный список
                    urlReadyToTodo.addAll(firstPart.join());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//end of else

            return urlReadyToTodo;//возвращаем локальный список
        }//конец тела задачи

    }//конец класс-поток-task

    public Boolean isExistInPageBDbyUrl (String url, int id) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
            //connection.createStatement().execute("ALTER TABLE PAGE ADD UNIQUE INDEX (path(200))");
            String query = "SELECT * FROM PAGE WHERE PATH = \'" + url + "\' and site_id = " + id;
            System.out.println(query);
            ResultSet rs = connection.createStatement().executeQuery(query);
            if (!rs.isBeforeFirst()){
                connection.close();
                return false;
            }
            connection.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }        ;
    return true;
    }

}
