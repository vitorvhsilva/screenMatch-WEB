package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.SerieService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("series")
public class SerieController {

    private SerieService serieService;

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return serieService.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5Series() {
        return serieService.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos() {
        return serieService.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterSerie(@PathVariable Long id) {
        return serieService.obterSerie(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTemporadasSerie(@PathVariable Long id) {
        return serieService.obterTemporadasSerie(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    public List<EpisodioDTO> obterTemporadasPorNumero(@PathVariable Long id, @PathVariable Long numero) {
        return serieService.obterTemporadasPorNumero(id, numero);
    }

    @GetMapping("/categoria/{nome}")
    public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String nome){
        return serieService.obterSeriesPorCategoria(nome);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> obterMelhoresEpisodios(@PathVariable Long id) {
        return serieService.obterMelhoresEpisodios(id);
    }

}
