create table news_item (
  id bigint primary key not null,
  title varchar(255) not null,
  author varchar(255) not null,
  content varchar(255) not null,
  publishDate datetime not null
);