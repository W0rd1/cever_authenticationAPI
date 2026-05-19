FROM postgres:latest

ENV POSTGRES_USER=cever
ENV POSTGRES_PASSWORD=ceverdb

EXPOSE 5432

VOLUME /var/lib/postgresql/data