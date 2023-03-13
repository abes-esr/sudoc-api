package fr.abes.convergence.sudocws.controller;

import fr.abes.cbs.exception.CBSException;
import fr.abes.convergence.sudocws.dto.ResultWebDto;
import fr.abes.convergence.sudocws.dto.SearchDatWebDto;
import fr.abes.convergence.sudocws.service.SudocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class SudocController {
    @Autowired
    private SudocService service;
    @PostMapping(value = "/dat2ppn", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultWebDto datToPpn(@Valid @RequestBody SearchDatWebDto request) {
        if (request.getTitre() == null) {
            throw new IllegalArgumentException("Le titre ne peut pas être null");
        }
        ResultWebDto result = new ResultWebDto();
        try {
            result.addPpns(service.getPpnFromDat(request.getDate(), request.getAuteur(), request.getTitre()));
        } catch (CBSException ex) {
            result.addErreur(ex.getMessage());
        }
        return result;
    }
}
