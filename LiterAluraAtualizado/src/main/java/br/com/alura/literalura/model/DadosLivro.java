package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivro(@JsonAlias("book_id") Long id,
                         @JsonAlias("title") String titulo,
                         @JsonAlias("year") Integer ano,
                         @JsonAlias("authors") List<DadosAutor> autores,
                         @JsonAlias("languages") List<String> idiomas,
                         @JsonAlias("download_count") Double numeroDownloads) {
}
