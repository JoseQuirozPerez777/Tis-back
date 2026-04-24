package com.teamsys.portafolios.services;

import com.teamsys.portafolios.dto.RedSocialRequestDTO;
import com.teamsys.portafolios.dto.RedSocialResponseDTO;
import com.teamsys.portafolios.entities.RedSocial;
import com.teamsys.portafolios.entities.Usuario;
import com.teamsys.portafolios.repositories.RedSocialRepository;
import com.teamsys.portafolios.utils.ValidadorDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RedSocialService {

    @Autowired
    private RedSocialRepository redSocialRepository;

    public List<RedSocialResponseDTO> obtenerRedesPorUsuario(Usuario usuario) {
        List<RedSocial> redes = redSocialRepository.findByUsuario(usuario);

        return redes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<RedSocialResponseDTO> agregarRedes(List<RedSocialRequestDTO> redesDTO, Usuario usuario) {
        List<RedSocial> redes = redesDTO.stream().map(dto -> {

            if (!ValidadorDatos.esUrlValida(dto.getUrlPerfil())) {
                throw new RuntimeException("Formato de URL no válido para la red social: " + dto.getNombreRed());
            }

            RedSocial red = new RedSocial();
            red.setUsuario(usuario);
            red.setNombreRed(dto.getNombreRed());
            red.setUrlPerfil(dto.getUrlPerfil());
            red.setEsPublico(dto.getEsPublico());

            return red;
        }).collect(Collectors.toList());

        List<RedSocial> guardadas = redSocialRepository.saveAll(redes);

        return guardadas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public RedSocialResponseDTO actualizarRedSocial(Long idRed, RedSocialRequestDTO dto, Usuario usuario) {
        RedSocial red = redSocialRepository.findByIdRedAndUsuario(idRed, usuario)
                .orElseThrow(() -> new RuntimeException("Red social no encontrada"));

        if (!ValidadorDatos.esUrlValida(dto.getUrlPerfil())) {
            throw new RuntimeException("Formato de URL no válido para la red social: " + dto.getNombreRed());
        }

        red.setNombreRed(dto.getNombreRed());
        red.setUrlPerfil(dto.getUrlPerfil());
        red.setEsPublico(dto.getEsPublico());

        RedSocial actualizada = redSocialRepository.save(red);

        return convertirADTO(actualizada);
    }

    public void eliminarRedSocial(Long idRed, Usuario usuario) {
        RedSocial red = redSocialRepository.findByIdRedAndUsuario(idRed, usuario)
                .orElseThrow(() -> new RuntimeException("Red social no encontrada"));

        redSocialRepository.delete(red);
    }

    private RedSocialResponseDTO convertirADTO(RedSocial red) {
        return new RedSocialResponseDTO(
                red.getIdRed(),
                red.getNombreRed(),
                red.getUrlPerfil(),
                red.isEsPublico()
        );
    }
}