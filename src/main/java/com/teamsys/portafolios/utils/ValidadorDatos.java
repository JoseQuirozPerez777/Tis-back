package com.teamsys.portafolios.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class ValidadorDatos {

    // Regex para correo electrónico estándar
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Regex para nombres: Inicia con Mayúscula, permite espacios, sin números
    private static final String NOMBRE_PATTERN = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(\\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)*$";

    /**
     * Valida la contraseña según requerimientos de seguridad:
     * - Mínimo 8, Máximo 20 caracteres.
     * - Al menos una mayúscula.
     * - Al menos una minúscula.
     * - Al menos un número.
     * - Al menos un carácter especial (@#$%^&+=!_).
     */
    public static boolean esPasswordSegura(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            return false;
        }

        boolean tieneMayuscula = Pattern.compile("[A-Z]").matcher(password).find();
        boolean tieneMinuscula = Pattern.compile("[a-z]").matcher(password).find();
        boolean tieneNumero = Pattern.compile("[0-9]").matcher(password).find();
        boolean tieneEspecial = Pattern.compile("[@#$%^&+=!_]").matcher(password).find();

        return tieneMayuscula && tieneMinuscula && tieneNumero && tieneEspecial;
    }

    /**
     * Valida el nombre:
     * - Inicia con mayúscula, sin números.
     * - Tamaño mínimo 3, máximo 50.
     */
    public static boolean esNombreValido(String nombre) {
        if (nombre == null || nombre.length() < 3 || nombre.length() > 50) {
            return false;
        }
        return Pattern.compile(NOMBRE_PATTERN).matcher(nombre).matches();
    }

    /**
     * Valida el correo:
     * - Formato estándar de email.
     * - Tamaño mínimo 3, máximo 100.
     */
    public static boolean esCorreoValido(String correo) {
        if (correo == null || correo.length() < 3 || correo.length() > 100) {
            return false;
        }
        return Pattern.compile(EMAIL_PATTERN).matcher(correo).matches();
    }
      /**
     * Valida una URL:
     * - No nula
     * - Longitud entre 10 y 255
     * - Debe iniciar con http:// o https://
     * - Debe tener host válido
     */
    public static boolean esUrlValida(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        url = url.trim();

        if (url.length() < 10 || url.length() > 255) {
            return false;
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }

        try {
            URI uri = new URI(url);

            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }

            String host = uri.getHost();

            // Debe tener al menos un punto: ejemplo linkedin.com, github.com
            return host.contains(".") && !host.startsWith(".") && !host.endsWith(".");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
