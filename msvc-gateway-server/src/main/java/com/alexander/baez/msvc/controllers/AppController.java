package com.alexander.baez.msvc.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AppController {

    //EndPonit para obtener el codigo de autorizacion
    @GetMapping(value = "/authorized")
    public Map<String,String> authorized (@RequestParam String code){//aca recibimos el codigo de autorizacion
        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        return map;
    }
    //EndPoint para iniciar session
    @PostMapping(value = "/login")//este nombre podría cambiar a "/login" para el inicio de sesión
    public Map<String,String> logout(@RequestBody String entity){
        return Collections.singletonMap("login", "ok");//ok inicio session correctamente
    }
}
