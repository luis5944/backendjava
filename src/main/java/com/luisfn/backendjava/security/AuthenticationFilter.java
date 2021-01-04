package com.luisfn.backendjava.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luisfn.backendjava.SpringApplicationContext;
import com.luisfn.backendjava.models.requests.UserLoginRequestModel;
import com.luisfn.backendjava.services.UserServiceInterface;
import com.luisfn.backendjava.shared.dto.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;


    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //Copias lo que viene de request a la clase UserLoginRequestModel
            UserLoginRequestModel userModel = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);

            //intentamos un login con el email y la password (lo ultimo es una lista de credenciales)
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userModel.getEmail(), userModel.getPassword(), new ArrayList<>()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        //Tenemos que hacer un token y le añadimos el prefix
        StringBuilder token = new StringBuilder();
        token.append(SecurityConstants.TOKEN_PREFIX);

        //Cogemos el email del resultado de auth
        String username = ((User) authentication.getPrincipal()).getUsername();

        //Creamos el jwtoken y se lo añadimos al token completo. Aquí necesitamos las constantes creadas
        token.append(Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_DATE))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
                .compact());


        //Añadir el header con el id publico del user
        UserServiceInterface userService = (UserServiceInterface) SpringApplicationContext.getBean("userService");
        UserDto user = userService.getUser(username);
        response.addHeader("UserId", user.getUserId());

        //Añadimos el header de autenticación
        response.addHeader(SecurityConstants.HEADER_STRING, token.toString());


    }
}
