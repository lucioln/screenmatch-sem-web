package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

        System.out.println("Top 10 Episodios:");
        dadosEpisodiosList.stream()
                .filter( e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted( Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(10)
                .map(e -> e.titulo().toUpperCase())
                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap( t -> t.Episodios().stream()
                        .map(d -> new Episodio(t.temporada(), d)))
                        .collect(Collectors.toList());
        episodios.forEach(System.out::println);

    /*    System.out.println("Digite um titulo de episodio para buscar:");
        var trechoDoTitulo = scanner.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoDoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio Encontrado:");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
            System.out.println("Titulo: " + episodioBuscado.get().getTitulo());
            System.out.println("Numero do Episodio: " + episodioBuscado.get().getNumeroEpisodio());
        }else {
            System.out.println("Não foi encontrado");
        }*/


       /* System.out.println("A partir de que ano você deseja ver os episódios?");
        var ano = scanner.nextInt();
        scanner.nextLine();
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(e -> e.getDtLancamento() != null && e.getDtLancamento().isAfter(dataBusca))
                .forEach( e -> System.out.println(
                        "Temporada: " + e.getTemporada() + " Episodio: " + e.getNumeroEpisodio() +
                                " Data de Lançamento: " + e.getDtLancamento().format(formatter)
                ));*/

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao()>0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor avaliação: " + est.getMax());
        System.out.println("Pior avaliação: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }
}
