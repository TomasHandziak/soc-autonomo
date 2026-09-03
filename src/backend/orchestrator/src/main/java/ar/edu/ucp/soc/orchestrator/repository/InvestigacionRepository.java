package ar.edu.ucp.soc.orchestrator.repository;

import ar.edu.ucp.soc.orchestrator.model.Investigacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestigacionRepository extends JpaRepository<Investigacion, String> {

    List<Investigacion> findByEstado(Investigacion.Estado estado);

    List<Investigacion> findByAgenteName(String agenteName);

    List<Investigacion> findByAprobadaPorHumanoIsNull();
}