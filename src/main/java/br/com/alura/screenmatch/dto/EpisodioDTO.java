package br.com.alura.screenmatch.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EpisodioDTO {

    private Long id;
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

}
