package searchengine.services;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class UrlListFromSite {

    private String urlSite;//начальная ссылка поиска
    private ArrayList<String> urlReadyList = new ArrayList<>(); //список обработанных ссылок, который будет на выходе
    private volatile ArrayList<String> todoTaskList = new ArrayList<>();//список необработанных ссылок

    public UrlListFromSite(String urlString){//конструктор
        urlSite = urlString.replaceAll("www.","");//убираем из ссылки www
        todoTaskList.add(urlSite);//добавляем ссылку в список на выполнение
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();//очередь
        while (!todoTaskList.isEmpty()) {//работаем пока список необработанных ссылок не пустой
            if (!urlReadyList.contains(todoTaskList.get(0))){//проверяем, что первой ссылки нет в списке обработанных ссылок
                try {
                    //вытаскиваем первую в списке ссылку
                    //строку-ссылку присваиваем переменной, чтоб далее программа не тратила время на открытие списка
                    String defaultUrl = todoTaskList.get(0);
                    urlReadyList.add(defaultUrl);//заносим ссылку в список обработанных
                    //connect - подключение к html в инете, get - парсинг, создается документ,
                    // maxBodySize(0) - снимает ограничение на размер скачиваемых данных
                    Document doc = Jsoup.connect(defaultUrl).maxBodySize(0).get();
                    //из документа выбираются все элементы с тегами-адресом a[href]
                    //elements фактически является динамическим массивом нарезок hml-кода с данными по линиям
                    Elements elements = doc.select("a[href]");
                    TaskUrlSite taskUrl = null;//создаем экземпляр задачи
                    todoTaskList.addAll(forkJoinPool.invoke(new TaskUrlSite(elements)));//заносим экземпляр задачи в пул
                    todoTaskList.remove(defaultUrl);//удаляем отработанную ссылку из списка
                }//конец try
                catch (IOException ex) {
                    ex.getStackTrace();
                }
            } else{
                todoTaskList.remove(0);//если ссылка есть в списке обработанных, она просто удаляется из списка
            }
        }
    }

    //геттер готового списка
    public ArrayList<String> getUrlReadyList() {
        return urlReadyList;
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
            if (elements.size() < 4){//если в локальном списке задач элементов меньше 50
                for (Element el:elements) {//перебор элементов
                    String urlStr = el.absUrl("href");//из конкретного элемента вытаскиваем ссылку
                    urlStr = urlStr.replaceAll("www.", "");//убираем www из ссылки
                    //добавляем в список только ссылки с данного сайта
                    String minusUrl = urlStr.replaceAll(urlSite, "");
                    if (minusUrl.length() == (urlStr.length() - urlSite.length())) {//если ссылка с нужного сайта
                        //проверяем на наличие #
                        String minusResh = urlStr.replaceAll("[^#]+[#]{1}[^#]*", "");
                        if (!minusResh.equals("")) {
                            //проверяем на наличие полученной ссылки в списках обработанных и необработанных ссылок
                            if ((!urlReadyList.contains(urlStr)) && (!todoTaskList.contains(urlStr))) {
                                urlReadyToTodo.add(urlStr);//добаляем ссылку в список необработанных
//                                      System.out.println(urlReadyList.size() + "---" + urlStr + "\t\t" + Thread.currentThread().getName() + "=== " +
//                                                todoTaskList.size());
                            }
                        }
                    }
                }//конец цикла
            } else {//если в локальном списке задач элементов больше заданного количества
                try {//создаем две подзадачи, каждому свою часть списка
                    int a1 = elements.size() / 2;
                    int b = elements.size() - 1;
                    TaskUrlSite firstPart = new TaskUrlSite(elements.subList(0, a1));
                    TaskUrlSite secondPart = new TaskUrlSite(elements.subList(a1 + 1, b));
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

}
