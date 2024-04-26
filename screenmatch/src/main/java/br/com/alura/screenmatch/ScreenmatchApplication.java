package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosEpsodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoApi consumoApi = new ConsumoApi();
		var jsonSerie = consumoApi.obterDados("http://www.omdbapi.com//?t=gilmore+girls&apikey=1f608789");
		System.out.println(jsonSerie);
		ConverteDados converteDados = new ConverteDados();
		DadosSerie dadosSerie = converteDados.obterDados(jsonSerie, DadosSerie.class);
		System.out.println(dadosSerie);

		var jsonEpsodio = consumoApi.obterDados("http://www.omdbapi.com//?t=gilmore+girls&season=1&episode=2&apikey=1f608789");
		DadosEpsodio dadosEpsodio = converteDados.obterDados(jsonEpsodio, DadosEpsodio.class);
		System.out.println(dadosEpsodio);

		var jsonTemporada = consumoApi.obterDados("http://www.omdbapi.com//?t=gilmore+girls&season=1&apikey=1f608789");
		DadosTemporada dadosTemporada = converteDados.obterDados(jsonTemporada, DadosTemporada.class);
		System.out.println(dadosTemporada);
	}
}
