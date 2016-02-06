create table news_item (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  title       varchar(255)                      not null,
  author      varchar(255)                      not null,
  content     varchar(255)                      not null,
  publishDate datetime                          not null
);