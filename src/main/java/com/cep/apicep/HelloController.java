package com.cep.apicep;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloController {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    @RequestMapping("/")
    public String hello() {
        return "<h1>Digite seu nome e CEP</h1>" +
                "<form action='/buscarEndereco' method='get'>" +
                "Nome: <input type='text' name='nome' required><br>" +
                "CEP: <input type='text' name='cep' required><br>" +
                "<input type='submit' value='Buscar Endereço'>" +
                "</form>";
    }

    @RequestMapping("/buscarEndereco")
    public String buscarEndereco(@RequestParam String nome, @RequestParam String cep) {
        // Utilizando a API ViaCEP para buscar o endereço com base no CEP
        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        RestTemplate restTemplate = new RestTemplate();
        Endereco endereco = restTemplate.getForObject(url, Endereco.class);

        if (endereco != null && endereco.getLogradouro() != null) {
            return "Olá " + nome + ", seu endereço é: " + endereco.getLogradouro() + ", " + 
                   endereco.getBairro() + ", " + endereco.getLocalidade() + " - " + endereco.getUf();
        } else {
            return "CEP inválido. Por favor, tente novamente.";
        }
    }

    // Classe para mapear o retorno da API ViaCEP
    static class Endereco {
        private String logradouro;
        private String bairro;
        private String localidade;
        private String uf;

        // Getters e Setters

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getLocalidade() {
            return localidade;
        }

        public void setLocalidade(String localidade) {
            this.localidade = localidade;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }
    }

    // Exemplo de operação de cálculo
    @Data
    static class Result {
        private final int left;
        private final int right;
        private final long answer;

        // Construtor personalizado
        public Result(int left, int right, long answer) {
            this.left = left;
            this.right = right;
            this.answer = answer;
        }
    }

    // SQL sample
    @RequestMapping("calc")
    Result calc(@RequestParam int left, @RequestParam int right) {
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("left", left)
                .addValue("right", right);
        return jdbcTemplate.queryForObject("SELECT :left + :right AS answer", source,
                (rs, rowNum) -> new Result(left, right, rs.getLong("answer")));
    }
}
