/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanborja.gac.persistence;

import com.sanborja.gac.model.api.MotivoQuery;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Marlon Cordova
 */
@Repository
@Transactional
public class MotivoRepository {
    
    @Autowired
    SessionFactory session;
	
    
        
    @SuppressWarnings("unchecked")
    public List<MotivoQuery> query() {

        SQLQuery query = session.getCurrentSession().createSQLQuery(QueryNames.MotivoQuery);
        List<Object[]> rows = query.list();
        List<MotivoQuery> list = new ArrayList<MotivoQuery>();
                

        for(Object[] row : rows){	

            MotivoQuery motivo = new MotivoQuery();
            try {
                    motivo.
                            setId(Integer.parseInt(row[0].toString())).
                            setCodigo(row[1].toString()).					
                            setNombre(row[2].toString()).
                            setEstado(row[3].toString());

            } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            list.add(motivo);
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    public List<MotivoQuery> query(int id) {

        SQLQuery query = (SQLQuery)session.getCurrentSession().createSQLQuery(QueryNames.MotivoQueryFindById).
                            setParameter("id", id);
        List<Object[]> rows = query.list();
        List<MotivoQuery> list = new ArrayList<MotivoQuery>();
          list.add(0, new MotivoQuery(0,"NOCODE","Ninguno","Activo"));
        for(Object[] row : rows){	

            MotivoQuery motivo = new MotivoQuery();
            try {
                    motivo.
                            setId(Integer.parseInt(row[0].toString())).
                            setCodigo(row[1].toString()).					
                            setNombre(row[2].toString()).
                            setEstado(row[3].toString());

            } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            list.add(motivo);
        }
        return list;
    }
}
