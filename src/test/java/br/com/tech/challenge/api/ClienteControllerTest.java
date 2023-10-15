package br.com.tech.challenge.api;

import br.com.tech.challenge.domain.entidades.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private static final String ROTA_CLIENTES = "/clientes";
    private static final String ROTA_CLIENTES_CPF = "/clientes/{cpf}";


    @DisplayName("Deve salvar um cliente com sucesso")
    @Test
    void saveClienteSuccess() throws Exception {
        mockMvc.perform(post(ROTA_CLIENTES)
                        .content(mapper.writeValueAsString(cliente()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @DisplayName("Deve salvar um cliente com sucesso apenas com cpf")
    @Test
    public void saveClienteWithValidCpf() throws Exception {
        String cpf = "66774316069";

        mockMvc.perform(post(ROTA_CLIENTES_CPF, cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void saveClienteWithInvalidCpf() throws Exception {
        String cpf = "123.456.789-0000";

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .post("/clientes/{cpf}", cpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    private Cliente cliente() {
        return Cliente.builder()
                .id(1L)
                .nome("Anthony Samuel Joaquim Teixeira")
                .email("anthony.samuel.teixeira@said.adv.br")
                .cpf("143.025.400-95")
                .build();
    }

}
