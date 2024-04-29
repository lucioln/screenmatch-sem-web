package br.com.alura.screenmatch;

import br.com.alura.screenmatch.principal.Principal;
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

		Principal principal = new Principal();
		principal.exibeMenu();
		/*var jsonEpsodio = consumoApi.obterDados("http://www.omdbapi.com//?t=gilmore+girls&season=1&episode=2&apikey=1f608789");
		DadosEpsodio dadosEpsodio = converteDados.obterDados(jsonEpsodio, DadosEpsodio.class);
		System.out.println(dadosEpsodio);

		var jsonTemporada = consumoApi.obterDados("http://www.omdbapi.com//?t=gilmore+girls&season=1&apikey=1f608789");
		DadosTemporada dadosTemporada = converteDados.obterDados(jsonTemporada, DadosTemporada.class);
		System.out.println(dadosTemporada);*/
	}
}
