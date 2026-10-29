package com.aluguel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoAluguelTest {

    @Autowired MockMvc mvc;

    private String json(String corpo) { return corpo.replace('\'', '"'); }

    private String token(String url, String corpo) throws Exception {
        String res = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json(corpo)))
                .andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(res, "$.token");
    }

    @Test
    void semLoginRetorna401() throws Exception {
        mvc.perform(get("/api/pedidos")).andExpect(status().isUnauthorized());
    }

    @Test
    void fluxoCompletoClienteBanco() throws Exception {
        String cliente = token("/api/auth/registro/cliente", """
            {'login':'maria','senha':'123456','dados':{'nome':'Maria','rg':'MG111','cpf':'111','endereco':'Rua A',
             'profissao':'Engenheira','rendimentos':[{'entidadeEmpregadora':'ACME','valor':5000}]}}""");
        String banco = token("/api/auth/registro/agente", """
            {'login':'banco1','senha':'123456','nome':'Banco X','cnpj':'00.000.000/0001-00','tipo':'BANCO'}""");

        // login funciona e senha errada falha
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("{'login':'maria','senha':'errada'}"))).andExpect(status().isBadRequest());

        // cliente nao pode cadastrar automovel; banco pode
        String carro = """
            {'matricula':'M1','ano':2022,'marca':'Fiat','modelo':'Argo','placa':'ABC1D23','tipoProprietario':'BANCO'}""";
        mvc.perform(post("/api/automoveis").header("Authorization", "Bearer " + cliente)
                .contentType(MediaType.APPLICATION_JSON).content(json(carro))).andExpect(status().isForbidden());
        String res = mvc.perform(post("/api/automoveis").header("Authorization", "Bearer " + banco)
                .contentType(MediaType.APPLICATION_JSON).content(json(carro)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int carroId = JsonPath.read(res, "$.id");

        // cliente cria pedido
        String pedido = "{'automovelId':" + carroId + ",'dataInicio':'2099-01-10','dataFim':'2099-01-20','valorTotal':1500}";
        res = mvc.perform(post("/api/pedidos").header("Authorization", "Bearer " + cliente)
                .contentType(MediaType.APPLICATION_JSON).content(json(pedido)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("EM_ANALISE"))
                .andReturn().getResponse().getContentAsString();
        int pedidoId = JsonPath.read(res, "$.id");

        // conflito de datas
        mvc.perform(post("/api/pedidos").header("Authorization", "Bearer " + cliente)
                .contentType(MediaType.APPLICATION_JSON).content(json(pedido))).andExpect(status().isBadRequest());

        // cliente nao avalia; banco aprova com credito
        mvc.perform(post("/api/pedidos/" + pedidoId + "/avaliacao").header("Authorization", "Bearer " + cliente)
                .contentType(MediaType.APPLICATION_JSON).content(json("{'aprovado':true}")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/pedidos/" + pedidoId + "/avaliacao").header("Authorization", "Bearer " + banco)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("{'aprovado':true,'parecer':'ok','valorFinanciado':1000,'taxaJurosMensal':1.5}")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APROVADO"))
                .andExpect(jsonPath("$.bancoCredito").value("Banco X"));

        // banco executa contrato; cliente ve status
        mvc.perform(post("/api/pedidos/" + pedidoId + "/contrato").header("Authorization", "Bearer " + banco))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CONTRATADO"));
        mvc.perform(get("/api/pedidos").header("Authorization", "Bearer " + cliente))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("CONTRATADO"));

        // pedido contratado nao pode ser cancelado
        mvc.perform(post("/api/pedidos/" + pedidoId + "/cancelar").header("Authorization", "Bearer " + cliente))
                .andExpect(status().isBadRequest());
    }

    @Test
    void maximoTresRendimentos() throws Exception {
        mvc.perform(post("/api/auth/registro/cliente").contentType(MediaType.APPLICATION_JSON).content(json("""
            {'login':'joao','senha':'123456','dados':{'nome':'Joao','rg':'R2','cpf':'222','endereco':'Rua B',
             'rendimentos':[{'entidadeEmpregadora':'A','valor':1},{'entidadeEmpregadora':'B','valor':1},
             {'entidadeEmpregadora':'C','valor':1},{'entidadeEmpregadora':'D','valor':1}]}}""")))
                .andExpect(status().isBadRequest());
    }
}
