package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=1b95b0fc";
    private SerieRepository serieRepository;
    private List<Serie> series;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
        this.series = serieRepository.findAll();
    }


    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Mostrar séries buscadas
                    4 - Buscar série por titulo
                    5 - Buscar séries por ator
                    6 - Buscar as melhores séries
                    7 - Buscar por categoria
                    8 - Buscar séries por avaliação e número de temporadas
                    9 - Buscar episódios por trecho
                    10 - Melhores episódio da série
                    11 - Buscar episodios a partir de uma data
                   \s
                    0 - Sair                                \s
                   \s""";

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarMelhoresSeries();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    buscarMelhoresEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosAposData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        serieRepository.save(serie);
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        String nomeSerie = leitura.nextLine();
        String json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Escolha uma serie pelo nome: ");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isEmpty()) {
            System.out.println("Serie nao encontrada");
            return;
        }

        Serie serieEncontrada = serie.get();

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
            String json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        List<Episodio> episodios = temporadas.stream()
                .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e)))
                .collect(Collectors.toList());

        serieEncontrada.setEpisodios(episodios);
        serieRepository.save(serieEncontrada);
        serieEncontrada.getEpisodios().forEach(System.out::println);
    }

    private void mostrarSeriesBuscadas() {
        series = serieRepository.findAll();

        series
                .stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        mostrarSeriesBuscadas();
        System.out.println("Escolha uma serie pelo nome: ");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isEmpty()) {
            System.out.println("Serie nao encontrada!");
            return;
        }

        System.out.println("Dados da série: " + serieBuscada.get());


    }

    private void buscarSeriesPorAtor() {
        System.out.println("Fale o nome do ator: ");
        String ator = leitura.nextLine();

        System.out.println("A partir de qual avaliação: ");
        Double avaliacao = leitura.nextDouble();leitura.nextLine();

        List<Serie> seriesEncontradas = serieRepository.
                findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(ator,avaliacao);

        System.out.println("Series em que o " + ator + " trabalhou");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + ", avaliacao:" + s.getAvaliacao()));
    }

    private void buscarMelhoresSeries() {
        List<Serie> melhoresSeries = serieRepository.findTop5ByOrderByAvaliacaoDesc();
        melhoresSeries.forEach(s -> System.out.println(s.getTitulo() + ", avaliacao:" + s.getAvaliacao()));
    }


    private void buscarSeriesPorCategoria() {
        System.out.println("Qual categoria de série?");
        String genero = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(genero);

        List<Serie> seriePorCategoria = serieRepository.findByGenero(categoria);
        seriePorCategoria.forEach(s -> System.out.println(s.getTitulo() + ", categoria:" + s.getGenero()));
    }

    private void filtrarSeriesPorTemporadaEAvaliacao() {
        System.out.println("Até quantas temporadas?");
        Integer numeroTemp = leitura.nextInt();leitura.nextLine();

        System.out.println("A partir de qual avaliação?");
        Double avaliacao = leitura.nextDouble();leitura.nextLine();

        List<Serie> seriesEncontradas = serieRepository.seriesPorTemporadaEAvaliacao(numeroTemp, avaliacao);

        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo()
                + ", temporadas: " + s.getTotalTemporadas() + ", avaliacao: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do episódio para busca: ");
        String trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = serieRepository.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(System.out::println);
    }

    private void buscarMelhoresEpisodiosPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Qual série?");
        String serie = leitura.nextLine();

        Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(serie);

        if (serieBuscada.isEmpty()) {
            System.out.println("Serie nao encontrada!");
            return;
        }

        List<Episodio> melhoresEpisodios = serieRepository.melhoresEpisodiosPorSerie(serieBuscada.get());

        melhoresEpisodios.forEach(System.out::println);
    }


    private void buscarEpisodiosAposData() {
        mostrarSeriesBuscadas();
        System.out.println("Qual série?");
        String serie = leitura.nextLine();
        System.out.println("Digite o ano limite");
        Integer ano = leitura.nextInt();leitura.nextLine();

        Optional<Serie> serieBuscada = serieRepository.findByTituloContainingIgnoreCase(serie);

        if (serieBuscada.isEmpty()) {
            System.out.println("Serie nao encontrada!");
            return;
        }

        List<Episodio> episodiosAno = serieRepository.episodiosPorSerieAno(serieBuscada.get(), ano);

        episodiosAno.forEach(System.out::println);
    }

}