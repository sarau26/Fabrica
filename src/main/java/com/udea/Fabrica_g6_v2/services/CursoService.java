package com.udea.Fabrica_g6_v2.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.udea.Fabrica_g6_v2.dto.CursoDto;
import com.udea.Fabrica_g6_v2.dto.MateriaProgramaVersionDto;
import com.udea.Fabrica_g6_v2.models.Curso;
import com.udea.Fabrica_g6_v2.models.Materia;
import com.udea.Fabrica_g6_v2.repositories.CursoRepository;
import com.udea.Fabrica_g6_v2.repositories.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {
    @Autowired
    CursoRepository cursoRepository;

    @Autowired
    MateriaRepository materiaRepository;

    public List<Curso> findAll(){
        return cursoRepository.findAll();
    }

    public ResponseEntity<Curso> save(Curso curso){
        //Validar que no existe
        MateriaProgramaVersionDto dto =
                new MateriaProgramaVersionDto(curso.getMateria().getIdMateria(),
                        curso.getProgramaAcademico(), curso.getVersionPensum());
        if(getId(dto).hasBody())
            return ResponseEntity.status(409).build();

        //Guardar
        Curso result = cursoRepository.save(curso);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Curso> findById(Integer id){
        //usamos siempre un optional para no tratar con excepciones de alcance por id, debido a un id incorrecto
        Optional<Curso> optionalCurso = cursoRepository.findById(id);
        if(optionalCurso.isPresent())
            //si existe lo devuelvo
            return  ResponseEntity.ok(optionalCurso.get());
        else
            //si no existe debolvemos un 404 con un response entity
            return ResponseEntity.notFound().build();
    }

    //borrar un elemento de la DB
    public ResponseEntity<Curso> delete(MateriaProgramaVersionDto dto){
        //si el Id NO existe (NUMEO MUY GRANDE)
        ResponseEntity<Integer> id =  getId(dto);
        if (!id.hasBody()) {
            return ResponseEntity.notFound().build();
        }
        cursoRepository.deleteById(id.getBody());
        return ResponseEntity.accepted().build();
    }

    //actualizar un elemento existente
    public ResponseEntity<Curso> update(CursoDto inDto){
        //Encontrar id
        MateriaProgramaVersionDto dto =
                new MateriaProgramaVersionDto(inDto.getMateria().getIdMateria(),
                        inDto.getProgramaAcademico(), inDto.getVersionPensum());
        ResponseEntity<Integer> id = getId(dto);
        if(!id.hasBody())
            return ResponseEntity.notFound().build();

        //Dto a curso
        Curso newCurso = new Curso();
        newCurso.setCodigoCurso(id.getBody());


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        Materia materia = materiaRepository.getById(inDto.getMateria().getIdMateria());
        newCurso.setMateria(materia);
        newCurso.fillFromDto(inDto);

        //Guardar
        Curso result = cursoRepository.save(newCurso);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Integer> getId(MateriaProgramaVersionDto dto){
        if(dto.hasNull()) return ResponseEntity.badRequest().build();
        Materia materia = materiaRepository.getById(dto.getIdMateria());
        Optional<Curso> curso = cursoRepository.findByMateriaAndProgramaAcademicoAndVersionPensum
                (materia, dto.getProgramaAcademico(), dto.getVersionPensum());
        if(!curso.isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(curso.get().getCodigoCurso());
    }
}
