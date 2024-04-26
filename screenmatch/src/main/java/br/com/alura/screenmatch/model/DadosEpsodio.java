package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpsodio(
                        @JsonAlias("Title") String titulo,
                        @JsonAlias("Episode") Integer numero,
                        @JsonAlias("imdbRating") String avaliacao,
                        @JsonAlias("Released") String dtLancamento
                        ) {
}
