package br.com.tech.challenge.api;

import br.com.tech.challenge.domain.entidades.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.flyway.clean-disabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String ROTA_CLIENTES = "/clientes";
    private static final String ROTA_CLIENTES_CPF = "/clientes/{cpf}";

    @AfterAll
    static void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }


    @DisplayName("Deve salvar um cliente com sucesso")
    @Test
    void shouldSaveClienteSuccess() throws Exception {
        mockMvc.perform(post(ROTA_CLIENTES)
                        .content(mapper.writeValueAsString(setCliente()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @DisplayName("Deve salvar um cliente com sucesso apenas com cpf")
    @Test
    void shouldSaveClienteWithValidCpf() throws Exception {
        final String cpf = "66774316069";

        mockMvc.perform(post(ROTA_CLIENTES_CPF, cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @DisplayName("Não deve salvar um cliente com cpf inválido")
    @Test
    void shouldNotSaveClienteWithInvalidCpf() throws Exception {
        final String cpf = "123.456.789-0000";

        mockMvc.perform(post(ROTA_CLIENTES_CPF, cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    private Cliente setCliente() {
        return Cliente.builder()
                .id(1L)
                .nome("Anthony Samuel Joaquim Teixeira")
                .email("anthony.samuel.teixeira@said.adv.br")
                .cpf("143.025.400-95")
                .build();
    }

}
