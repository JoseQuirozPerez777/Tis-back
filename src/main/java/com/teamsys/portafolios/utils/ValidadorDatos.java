package com.teamsys.portafolios.utils;

import java.util.regex.Pattern;

public class ValidadorDatos {

    // Regex para correo electrónico estándar
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Regex para nombres: Inicia con Mayúscula, permite espacios, sin números
    private static final String NOMBRE_PATTERN = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)*$";

    public static boolean esCorreoValido(String correo) {
        return correo != null && Pattern.compile(EMAIL_PATTERN).matcher(correo).matches();
    }

    public static boolean esPasswordSegura(String password) {
        // Mínimo 8 caracteres (puedes añadir más reglas aquí)
        return password != null && password.length() >= 8;
    }

    public static boolean esNombreValido(String nombre) {
        return nombre != null && Pattern.compile(NOMBRE_PATTERN).matcher(nombre).matches();
    }
}