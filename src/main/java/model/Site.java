package model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "site")
@Setter
@Getter
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private StatusIndexing status;

    @Column(name = "status_time", nullable = false)
    private Date statusTime;

    @Column(columnDefinition = "TEXT", name = "last_error")
    private String lastError;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String url;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    @OneToMany(mappedBy = "site1", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Page> pages;

}
/*
site — информация о сайтах и статусах их индексации

●	id INT NOT NULL AUTO_INCREMENT;
●	status ENUM('INDEXING', 'INDEXED', 'FAILED') NOT NULL — текущий статус полной индексации сайта, отражающий
готовность поискового движка осуществлять поиск по сайту — индексация или переиндексация в процессе, сайт
полностью проиндексирован (готов к поиску) либо его не удалось проиндексировать (сайт не готов к поиску и не будет
до устранения ошибок и перезапуска индексации);
●	status_time DATETIME NOT NULL — дата и время статуса (в случае статуса INDEXING дата и время должны обновляться
регулярно при добавлении каждой новой страницы в индекс);
●	last_error TEXT — текст ошибки индексации или NULL, если её не было;
●	url VARCHAR(255) NOT NULL — адрес главной страницы сайта;
●	name VARCHAR(255) NOT NULL — имя сайта.


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