package model;

import javax.persistence.*;

@Entity
@Table(name = "page")//, indexes = @Index(columnList = "path", columnLength = 255))
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(name = "site_id", nullable = false)
    private int siteId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String path;

    @Column(nullable = false)
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSite_id() {
        return siteId;
    }

    public void setSite_id(int site_id) {
        this.siteId = site_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
/*
page — проиндексированные страницы сайта

●	id INT NOT NULL AUTO_INCREMENT;
●	site_id INT NOT NULL — ID веб-сайта из таблицы site;
●	path TEXT NOT NULL — адрес страницы от корня сайта (должен начинаться со слэша, например: /news/372189/);
●	code INT NOT NULL — код HTTP-ответа, полученный при запросе страницы (например, 200, 404, 500 или другие);
●	content MEDIUMTEXT NOT NULL — контент страницы (HTML-код).

По полю path должен быть установлен индекс, чтобы поиск по нему был быстрым, когда в нём будет много ссылок. Индексы
рассмотрены в курсе «Язык запросов SQL».


●	Создайте в проекте папку model и в ней — классы, которые будут соответствовать таблицам site и page в базе данных.
Структура таблиц описана в технической спецификации. Создайте их по правилам, которые вы изучали в модуле курса
«Работа с MySQL в Java». В частности, не забудьте про аннотации @Entity, @Id, @GeneratedValue, @ManyToOne и @JoinColumn.
 Для enum-поля создайте отдельный Enum (его можно поместить в ту же папку model). Различные типы текстовых полей
 обозначайте аннотацией @Column:

@Column(columnDefinition = "ENUM('INDEXING', 'INDEXED')")
@Column(columnDefinition = "VARCHAR(255)")
@Column(columnDefinition = "TEXT")
@Column(columnDefinition = "MEDIUMTEXT")
 */