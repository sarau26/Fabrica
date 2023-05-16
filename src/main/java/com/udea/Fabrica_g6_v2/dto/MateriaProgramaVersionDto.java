package com.udea.Fabrica_g6_v2.dto;

import lombok.Data;

@Data
public class MateriaProgramaVersionDto {
    private Integer idMateria;
    private String programaAcademico;
    private Integer versionPensum;

    public MateriaProgramaVersionDto(Integer idMateria, String programa, Integer version) {
        this.idMateria = idMateria;
        this.programaAcademico = programa;
        this.versionPensum = version;
    }

    public boolean hasNull() {
        return idMateria == null || programaAcademico == null || versionPensum == null;
    }
}
