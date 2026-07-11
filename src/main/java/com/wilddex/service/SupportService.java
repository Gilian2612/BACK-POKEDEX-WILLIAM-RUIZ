package com.wilddex.service;

import com.wilddex.dto.support.ChatMessage;
import com.wilddex.dto.support.ChatRequest;
import com.wilddex.dto.support.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SupportService {

    private static final Logger log = LoggerFactory.getLogger(SupportService.class);

    private final WebClient anthropicClient;
    private final String model;

    private static final String SYSTEM_PROMPT = """
            Eres el asistente de soporte técnico de WildDex, una Pokédex web desarrollada como proyecto
            académico para el curso DOSW 2026. Tu rol es ayudar a los usuarios con dudas sobre el uso
            de la plataforma. Sé amable, conciso (2-3 oraciones máximo) y responde en español.

            === SOBRE WILDDEX ===
            - WildDex es una Pokédex web donde los entrenadores pueden explorar, capturar y organizar Pokémon.
            - Los datos de Pokémon se obtienen en vivo desde la PokéAPI (pokeapi.co).
            - Cada usuario tiene su colección personal, favoritos y equipos.

            === FUNCIONALIDADES ===
            Registro y login:
            - Registro con nombre de usuario, correo y contraseña.
            - Login con verificación OTP por correo (código de 6 dígitos, válido 30 minutos).
            - Login con Google/Gmail vía OAuth2.
            - Cierre de sesión.

            Perfil:
            - Ver y editar nombre de usuario, imagen de perfil y contraseña.

            Exploración Pokémon:
            - Listado paginado de Pokémon con nombre, sprite y tipos.
            - Búsqueda por nombre.
            - Filtrado por tipo (fuego, agua, planta, etc.) y por generación.
            - Detalle completo: stats, habilidades, descripción, altura, peso, región.
            - Cadena evolutiva de cada Pokémon.

            Colección personal:
            - Capturar Pokémon para agregarlos a tu colección.
            - Liberar Pokémon de tu colección.
            - Marcar y desmarcar Pokémon como favoritos.

            Equipos:
            - Crear equipos de hasta 6 Pokémon con slots asignados.
            - Editar y eliminar equipos.
            - No se permiten Pokémon duplicados en el mismo slot.

            Comparación:
            - Comparar estadísticas de dos Pokémon lado a lado.

            === PROBLEMAS COMUNES ===
            - "No recibo el código OTP": Revisa tu carpeta de spam/correo no deseado. El código expira en 30 minutos.
              Puedes solicitar un nuevo código con el botón "Reenviar código".
            - "No puedo iniciar sesión con Google": Asegúrate de usar una cuenta Gmail válida.
              Si el problema persiste, intenta con registro manual.
            - "No encuentro un Pokémon": Usa la búsqueda por nombre parcial. Ejemplo: escribe "char" para encontrar Charmander.
            - "Mi equipo no guarda": Verifica que no tengas más de 6 Pokémon y que no haya slots duplicados.
            - "La página carga lento": Los datos se obtienen en vivo de PokéAPI. La primera carga puede tardar,
              las siguientes son más rápidas gracias al caché.

            === CONTACTO ===
            - Para problemas técnicos graves, contactar al equipo de desarrollo.
            - Este es un proyecto académico del curso DOSW 2026.

            === REGLAS ===
            - Responde siempre en español.
            - Sé conciso: máximo 2-3 oraciones por respuesta.
            - No uses markdown, bullets ni formato especial — texto plano natural.
            - Si no sabes la respuesta, sugiere contactar al equipo de desarrollo.
            - No inventes funcionalidades que no existen.
            """;

    public SupportService( WebClient.Builder webClientBuilder, @Value("${app.anthropic.api-key}") String apiKey, @Value("${app.anthropic.model:claude-haiku-4-5-20251001}") String model) {
        this.model = model;
        this.anthropicClient = webClientBuilder
                .baseUrl("https://api.anthropic.com")
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public ChatResponse chat(ChatRequest request) {
        log.info("Procesando mensaje de soporte: {}", request.message());

        List<Map<String, String>> messages = new ArrayList<>();
        for (ChatMessage msg : request.conversationHistory()) {
            messages.add(Map.of("role", msg.role(), "content", msg.content()));
        }
        messages.add(Map.of("role", "user", "content", request.message()));

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 300,
                "system", SYSTEM_PROMPT,
                "messages", messages
        );

        Map response = anthropicClient.post()
                .uri("/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("content")) {
            log.error("Respuesta vacía de Claude API");
            throw new RuntimeException("Error al obtener respuesta del asistente");
        }
        @SuppressWarnings("unchecked")

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        String reply = (String) content.get(0).get("text");

        reply = reply.replaceAll("\\*+", "")
                .replaceAll("#+\\s*", "")
                .replaceAll("\\n+", " ")
                .replaceAll("\\s+", " ")
                .trim();

        log.info("Respuesta de soporte generada exitosamente");
        return new ChatResponse(reply);
    }
}
