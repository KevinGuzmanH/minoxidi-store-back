package com.kevin.minoxidilback.service;

import com.kevin.minoxidilback.entity.Orden;
import com.kevin.minoxidilback.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class OrdenService {

    @Autowired
    OrdenRepository ordenRepository;

    public void registerNewOrder(Orden newOrder){
        ordenRepository.save(newOrder);
    }
}
