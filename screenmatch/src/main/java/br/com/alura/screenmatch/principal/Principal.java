package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Principal {
    Scanner scanner = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "http://www.omdbapi.com//?t=";
    private final String API_KEY = "&apikey=1f608789";

    private List<DadosSerie> dadosSerie = new ArrayList<>();
    private List<DadosTemporada> temporadas;
    private List<Episodio> episodios;
    private List<DadosEpisodio> dadosEpisodiosList;

    private Optional<Episodio> episodioBuscado ;

    private SerieRepository repositorio;

    private List<Serie> series = new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu(){
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
            1 - Buscar séries na web
            2 - Buscar episódios
            3 - Listar séries buscadas
            4 - Buscar Séries por titulo
            5 - Buscar Séries por Ator(a)
            6 - Top 5 séries
            7 - Buscar série por categoria
            8 - Buscar séries por numero de temporadas e avaliacao
            9 - Buscar episódio por trecho
            10 - Buscar top episodios por Serie
            11 - Buscar Episódios a partir de uma data
            0 - Sair                                 
            """;

            System.out.println(menu);
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    buscarSeriesPorNumeroDeTemporadasEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarTop5Episodios();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarEpisodiosDepoisDeUmaData() {
        Optional<Serie> serieBusca = buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lançamento");
            var anoLancamento = scanner.nextInt();
            scanner.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            episodiosAno.forEach(e -> System.out.println(e.getTitulo() + " data de lançamento: " + e.getDtLancamento()));
        }

    }

    private void buscarTop5Episodios() {
        Optional<Serie> serie = buscarSeriePorTitulo();
        if(serie.isPresent()){
            Serie serieEncontrada = serie.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serieEncontrada);
            topEpisodios.forEach( e -> System.out.println(e.getTitulo() + " - " + e.getSerie().getTitulo() + " avaliação do episodio: " + e.getAvaliacao()));
        }
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite um trecho a ser buscado:");
        var trecho = scanner.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodioPorTrecho(trecho);
        episodiosEncontrados.forEach(System.out::println);
    }

    private void buscarSeriesPorNumeroDeTemporadasEAvaliacao() {
        System.out.println("Digite o numero máximo de temporadas:");
        var totalTemporadas = scanner.nextInt();
        System.out.println("Digite a avaliação minima aceitada:");
        var avaliacaoDesejada = scanner.nextDouble();
        List<Serie> seriesBuscadas = repositorio.seriePorTemporadaEAvaliacao(totalTemporadas, avaliacaoDesejada);
        seriesBuscadas.forEach( s -> System.out.println(s.getTitulo() + " numero de temporadas: " + s.getTotalTemporadas() + " avaliação:" + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar série de que Genero/Categoria?");
        var nomeGenero = scanner.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da Categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach( s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator para busca");
        var nomeAtor = scanner.nextLine();
        System.out.println("Avaliações a partir de qual valor?");
        var avaliacao = scanner.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Series em que " + nomeAtor + " trabalhou:");
        seriesEncontradas.forEach( s -> System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private Optional<Serie> buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = scanner.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);
        if(serieBuscada.isPresent()){
            System.out.println("Dados da série: " + serieBuscada.get());
            return serieBuscada;
        }else {
            System.out.println("Serie não encontrada!");
        }
        return Optional.empty();
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSerie.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = scanner.nextLine();
        var json = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = scanner.nextLine();

        Optional<Serie> serie = series.stream().
                filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){

            var serieEncontrada = serie.get();

            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(dadosTemporada -> dadosTemporada.Episodios().stream().map( e -> new Episodio(dadosTemporada.temporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);

            repositorio.save(serieEncontrada);

        }else {
            System.out.println("Série não encontrada :(");
        }


    }


}
