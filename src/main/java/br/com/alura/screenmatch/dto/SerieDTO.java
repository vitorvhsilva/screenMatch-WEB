package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class SerieDTO {
    private Long id;

    private String titulo;

    private Integer totalTemporadas;

    private Double avaliacao;

    private Categoria genero;

    private String atores;

    private String poster;

    private String sinopse;

}
