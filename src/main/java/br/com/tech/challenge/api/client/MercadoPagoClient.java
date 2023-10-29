package br.com.tech.challenge.api.client;

import br.com.tech.challenge.api.client.factory.ClientHttpFactory;
import br.com.tech.challenge.api.client.factory.RetryTemplateFactory;
import br.com.tech.challenge.api.exception.MercadoPagoAPIException;
import br.com.tech.challenge.domain.dto.external.MercadoPagoRequestDTO;
import br.com.tech.challenge.domain.dto.external.MercadoPagoResponseDTO;
import br.com.tech.challenge.domain.enums.MercadoPagoAPI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MercadoPagoClient implements QRClient {

    private final ObjectMapper objectMapper;

    @Override
    public MercadoPagoResponseDTO generateQRCode(MercadoPagoRequestDTO dto) {
        RetryTemplate retryTemplate = RetryTemplateFactory.retryTemplate();
        RestTemplate restTemplate = new RestTemplate(ClientHttpFactory.clientHttpRequestFactory());

        try {
            ResponseEntity<MercadoPagoResponseDTO> responseEntity = retryTemplate
                    .execute(retryContext -> restTemplate.postForEntity(
                            MercadoPagoAPI.MERCADO_PAGO_URL.text().concat(MercadoPagoAPI.getQRCodeUrl()),
                            httpEntity(dto),
                            MercadoPagoResponseDTO.class
                    ));

            return responseEntity.getBody();
        } catch (RestClientException | JsonProcessingException exception) {
            throw new MercadoPagoAPIException("Ocorreu um erro ao gerar QR Code: " + exception.getMessage());
        }
    }

    @Override
    public void payQRCode(MercadoPagoRequestDTO dto) {
        RetryTemplate retryTemplate = RetryTemplateFactory.retryTemplate();
        RestTemplate restTemplate = new RestTemplate(ClientHttpFactory.clientHttpRequestFactory());

        try {
            retryTemplate.execute(retryContext -> restTemplate.exchange(
                    MercadoPagoAPI.MERCADO_PAGO_URL.text().concat(MercadoPagoAPI.getQRCodeUrl()),
                    HttpMethod.PUT,
                    httpEntity(dto),
                    Void.class
            ));
        } catch (RestClientException | JsonProcessingException exception) {
            throw new MercadoPagoAPIException("Ocorreu um erro ao realizar pagamento: " + exception.getMessage());
        }
    }

    private HttpEntity<String> httpEntity(MercadoPagoRequestDTO dto) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", MercadoPagoAPI.ACCESS_TOKEN.text());

        return new HttpEntity<>(objectMapper.writeValueAsString(dto), headers);
    }

}
