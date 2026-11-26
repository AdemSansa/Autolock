package tn.esprit.autoloc.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeResponseDTO;
import tn.esprit.autoloc.web.dto.VehiculeResumeDTO;

import java.util.List;

/**
 * MapStruct génère VehiculeMapperImpl (@Component) à la compilation.
 * L'identifiant de l'entité s'appelle idVehicule : on le renomme en "id" dans les DTO.
 */
@Mapper(componentModel = "spring")
public interface VehiculeMapper {

    // 1. Entité -> DTO détaillé
    @Mapping(target = "id", source = "idVehicule")
    @Mapping(target = "agenceNom", source = "agence.nom")
    @Mapping(target = "equipements", source = "equipements")
    VehiculeResponseDTO toDto(Vehicule vehicule);

    // 2. Entité -> DTO résumé
    @Mapping(target = "id", source = "idVehicule")
    VehiculeResumeDTO toResumeDto(Vehicule vehicule);

    List<VehiculeResumeDTO> toResumeDtoList(List<Vehicule> vehicules);

    // 3. DTO -> Entité
    @Mapping(target = "idVehicule", ignore = true)
    @Mapping(target = "statut", constant = "DISPONIBLE")
    @Mapping(target = "agence", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "maintenances", ignore = true)
    Vehicule toEntity(VehiculeRequestDTO dto);

    // 3 bis. DTO -> entité existante (PUT) : on garde id, statut, agence et collections
    @Mapping(target = "idVehicule", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "agence", ignore = true)
    @Mapping(target = "equipements", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "maintenances", ignore = true)
    void updateEntity(VehiculeRequestDTO dto, @MappingTarget Vehicule vehicule);

    // 4. Conversion d'un élément de la collection d'équipements
    default String toLibelle(Equipement equipement) {
        return equipement.getLibelle();
    }
}
