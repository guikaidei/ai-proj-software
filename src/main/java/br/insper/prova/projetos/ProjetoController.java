package br.insper.prova.projetos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projeto")
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    private final String usuarioUrl = "http://184.72.80.215:8080/usuario/";

    @PostMapping
    public ResponseEntity<String> cadastrarProjeto(@RequestParam String nome,
                                                   @RequestParam String descricao,
                                                   @RequestParam String status,
                                                   @RequestParam String cpfGerente) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> gerenteResponse = restTemplate.getForEntity(usuarioUrl + cpfGerente, String.class);

        if (gerenteResponse.getStatusCode().is2xxSuccessful()) {
            // Cadastrar projeto
            Projeto novoProjeto = new Projeto();
            novoProjeto.setNome(nome);
            novoProjeto.setDescricao(descricao);
            novoProjeto.setStatus(status);
            List<String> pessoas = novoProjeto.getPessoas();
            if (pessoas == null) {
                novoProjeto.setPessoas(List.of(cpfGerente)); // Cria a lista com o CPF do gerente
            } else {
                pessoas.add(cpfGerente);
                novoProjeto.setPessoas(pessoas); // Atualiza a lista de pessoas com o gerente
            }
            projetoService.cadastrarProjeto(novoProjeto);
            return ResponseEntity.ok("Projeto cadastrado com sucesso!");
        } else {
            return ResponseEntity.status(404).body("Gerente não encontrado.");
        }
    }

    // 2. Listagem de Projetos com filtro opcional de status
    @GetMapping
    public List<Projeto> listarProjetos(@RequestParam(required = false) String status) {
        return projetoService.listarProjetos(status);
    }

    // 3. Adicionar pessoas no projeto
    @PostMapping("/{id}/adicionar-pessoa")
    public ResponseEntity<String> adicionarPessoa(@PathVariable Integer id,
                                                  @RequestParam String cpfPessoa) {
        // Verificar se a pessoa existe (via rota externa)
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> pessoaResponse = restTemplate.getForEntity(usuarioUrl + cpfPessoa, String.class);

        if (pessoaResponse.getStatusCode().is2xxSuccessful()) {
            Optional<Projeto> projetoOpt = projetoService.buscarProjetoPorId(id);
            if (projetoOpt.isPresent()) {
                Projeto projeto = projetoOpt.get();
                if (!projeto.getStatus().equalsIgnoreCase("FINALIZADO")) {
                    projetoService.adicionarPessoaNoProjeto(projeto, cpfPessoa);
                    return ResponseEntity.ok("Pessoa adicionada ao projeto com sucesso!");
                } else {
                    return ResponseEntity.status(400).body("Não é possível adicionar pessoas a um projeto finalizado.");
                }
            } else {
                return ResponseEntity.status(404).body("Projeto não encontrado.");
            }
        } else {
            return ResponseEntity.status(404).body("Pessoa não encontrada.");
        }
    }

}
