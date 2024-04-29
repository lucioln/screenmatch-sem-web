package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {
    Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "http://www.omdbapi.com//?t=";
    private final String API_KEY = "&apikey=1f608789";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para buscar:");
        var nomeSerie = scanner.nextLine();
        nomeSerie = nomeSerie.replace(" ", "+").trim();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie + API_KEY);
        DadosSerie dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for(int i=1; i <= dadosSerie.totalTemporadas(); i++){
            var jsonTemporada = consumoApi.obterDados(ENDERECO + nomeSerie + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = converteDados.obterDados(jsonTemporada, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        temporadas.forEach(t -> t.Episodios().forEach(e -> System.out.println(e.titulo())));
        List<DadosEpisodio> dadosEpisodiosList = temporadas.stream()
                .flatMap( t -> t.Episodios().stream())
                        .collect(Collectors.toList());

        System.out.println("Top 5 Episodios:");
        dadosEpisodiosList.stream()
                .filter( e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted( Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap( t -> t.Episodios().stream()
                        .map(d -> new Episodio(t.temporada(), d)))
                        .collect(Collectors.toList());
        episodios.forEach(System.out::println);

        System.out.println("A partir de que ano você deseja ver os episódios?");
        var ano = scanner.nextInt();
        scanner.nextLine();
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDtLancamento() != null && e.getDtLancamento().isAfter(dataBusca))
                .forEach( e -> System.out.println(
                        "Temporada: " + e.getTemporada() + " Episodio: " + e.getNumeroEpisodio() +
                                " Data de Lançamento: " + e.getDtLancamento().format(formatter)
                ));
    }
}
