/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanborja.gac.services;

import com.sanborja.gac.model.Persona;
import com.sanborja.gac.model.Solicitante;
import com.sanborja.gac.model.Solicitud;
import com.sanborja.gac.model.TipoSolicitud;
import com.sanborja.gac.model.api.CheckStatus;
import com.sanborja.gac.model.api.Data;
import com.sanborja.gac.model.api.SolicitudFindByIdOutput;
import com.sanborja.gac.model.api.SolicitudInput;
import com.sanborja.gac.model.api.Status;
import com.sanborja.gac.model.api.SolicitudQuery;
import com.sanborja.gac.model.api.TipoSolicitudInput;
import com.sanborja.gac.persistence.SolicitudRepository;
import com.sanborja.gac.persistence.TipoDocumentoRepository;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marlon Cordova
 */
@Service
public class SolicitudService {
    @Autowired
    SolicitudRepository solicitudRepository;
 
    EmailValidator emailValidator;
    
    public SolicitudService(){
        emailValidator= new EmailValidator();
    }
    
    public Data<SolicitudQuery> query() {
        Data <SolicitudQuery> data = new Data<SolicitudQuery>();
        List<SolicitudQuery> list;
        list = solicitudRepository.query();

        if(list!=null) {
            
            if(!list.isEmpty()) {
                data.setApistatus(Status.Ok);
            }else{
                data.setApistatus(Status.Error);
                data.setApimessage("No hay datos");
            }
            
        }else{
            data.setApistatus(Status.Error);
            data.setApimessage("No hay datos");
        }

        data.setData(list);
        return data;
    }
    
     public CheckStatus create(SolicitudInput input) {
        CheckStatus checkStatus= new CheckStatus();
 	

        String error="";
        if(input==null){
            error+="ingresar el tipo de la solicitud";
        }else{
            
            if(input.getIdTipoDocumento()==0){
                error+="<li>Debe seleccionar un tipo de documento.</li>";
            }
            if(input.getNumeroDocumento().trim().isEmpty()){
                error+="<li>Debe ingresar su número de documento valido.</li>";
            }else{
                if(input.getIdTipoDocumento()==1){
                    if(input.getNumeroDocumento().trim().length()!=8){
                        error+="<li>Debe ingresar el dni de 8 números.</li>";
                    }
                }
            }
            
            if(input.getNombre().trim().isEmpty()){
                error+="<li>Debe ingresar su nombre.</li>";
            }
            
            if(input.getApellido().trim().isEmpty()){
                error+="<li>Debe ingresar sus apellidos.</li>";
            }
                        
            if(input.getTelefono().trim().isEmpty()){
                error+="<li>Debe ingresar su teléfono.</li>";
            }
                        
            if(input.getCorreo().trim().isEmpty()){
                error+="<li>Debe ingresar su correo.</li>";
            }else{
               if(!emailValidator.validate(input.getCorreo())){
                   error+="<li>Debe ingresar un correo valido. Ejemplo: usuario@outlook.com</li>";
               }
            }
            
            if(input.getDireccion().trim().isEmpty()){
                error+="<li>Debe ingresar su dirección.</li>";
            }
            
            if(input.getIdTipoSolicitud()==0){
                error+="<li>Debe seleccionar un tipo de solicitud valido.</li>";
            }
            
            if(input.getIdTipoSolicitud()==0){
                error+="<li>Debe seleccionar un motivo valido.</li>";
            }
            
            if(input.getDescripcion().trim().isEmpty()){
                error+="<li>Debe ingresar su descripción.</li>";
            }            
        }        
        
        if(error.trim().length()==0){

            Persona persona = new Persona();
            persona.setIdPersona(0).
                    setNombre(input.getNombre()).
                    setApellido(input.getApellido()).
                    setIdTipoDocumento(input.getIdTipoDocumento()).
                    setNumeroDocumento(input.getNumeroDocumento())
                    ;

            Solicitante solicitante = new Solicitante();
            solicitante.setIdPersona(0).
                    setCorreo(input.getCorreo()).
                    setTelefono(input.getTelefono()).
                    setDireccion(input.getDireccion()).
                    setPersona(persona).
                    setEstado(1);

            Solicitud solicitud = new Solicitud();
            solicitud.setIdSolicitudQR(0).
                    setIdTipoSolicitud(input.getIdTipoSolicitud()).
                    setIdMotivoQR(input.getIdMotivo()).
                    setDescripcion(input.getDescripcion()).
                    setImagen("").
                    setSolicitante(solicitante).
                    setEstado(1);


            checkStatus = solicitudRepository.create(solicitud);
                
            //enviar correo
            try {
                SendMailService mailService= new SendMailService();
                mailService.Send(input.getCorreo());
            } catch (IOException ex) {
                Logger.getLogger(SolicitudService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(SolicitudService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }else{
            checkStatus.setApistatus(Status.Error);
            checkStatus.setApimessage(error);
        }
        
        return checkStatus;
    }
     
    public SolicitudFindByIdOutput findById(int id) {
        SolicitudFindByIdOutput moneda = new SolicitudFindByIdOutput();		 
        moneda = solicitudRepository.findById(id);

        if(moneda!=null) {
            if(moneda.getId()!=0) {
                    moneda.setApistatus(Status.Ok);

            }else{
                    moneda = new SolicitudFindByIdOutput();
                    moneda.setApistatus(Status.Error);
                    moneda.setApimessage("No hay datos");
            }
        }else{
                moneda = new SolicitudFindByIdOutput();
                moneda.setApistatus(Status.Error);
                moneda.setApimessage("No hay datos");
        }

        return moneda;
    }
     
     
    public CheckStatus delete(Integer id) {
        CheckStatus checkStatus= new CheckStatus();
        checkStatus = solicitudRepository.delete(id);
        return checkStatus;
    }
    
}
