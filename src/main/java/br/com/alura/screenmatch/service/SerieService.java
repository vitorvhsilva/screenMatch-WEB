package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SerieService {

    private SerieRepository serieRepository;
    private ModelMapper modelMapper;

    public List<SerieDTO> converterDadosSerie(List<Serie> series) {
        return series.stream().map(s -> modelMapper.map(s, SerieDTO.class)).collect(Collectors.toList());
    }

    public List<EpisodioDTO> converterDadosEpisodio(List<Episodio> episodios) {
        return episodios.stream().map(e -> modelMapper.map(e, EpisodioDTO.class)).collect(Collectors.toList());
    }

    public List<SerieDTO> obterTodasAsSeries() {
        return converterDadosSerie(serieRepository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converterDadosSerie(serieRepository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converterDadosSerie(serieRepository.findTop5ByOrderByEpisodiosDataLancamentoDesc());
    }

    public SerieDTO obterSerie(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        return serie.map(value -> modelMapper.map(value, SerieDTO.class)).orElse(null);
    }

    public List<EpisodioDTO> obterTemporadasSerie(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return converterDadosEpisodio(s.getEpisodios());

        }
        return null;
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        return converterDadosEpisodio(serieRepository.episodiosPorTemporada(id, numero));
    }

    public List<SerieDTO> obterSeriesPorCategoria(String nome) {
        return converterDadosSerie(serieRepository.findByGenero(Categoria.fromPortugues(nome)));
    }

    public List<EpisodioDTO> obterMelhoresEpisodios(Long id) {
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()) {
            Serie s = serie.get();
            return converterDadosEpisodio(serieRepository.melhoresEpisodiosPorSerie(s));
        }
        return null;
    }
}
