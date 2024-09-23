package br.insper.prova.projetos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    // Método para cadastrar um novo projeto
    public Projeto cadastrarProjeto(Projeto projeto) {
        return projetoRepository.save(projeto);
    }

    // Método para listar todos os projetos com filtro opcional por status
    public List<Projeto> listarProjetos(String status) {
        List<Projeto> projetos = projetoRepository.findAll();
        Stream<Projeto> stream = projetos.stream();

        if (status != null) {
            stream = stream.filter(p -> p.getStatus().equalsIgnoreCase(status));
        }

        return stream.collect(Collectors.toList());
    }

    // Método para buscar um projeto por id
    public Optional<Projeto> buscarProjetoPorId(Integer id) {
        return projetoRepository.findById(id);
    }

    // Método para adicionar uma pessoa ao projeto
    public Projeto adicionarPessoaNoProjeto(Projeto projeto, String cpfPessoa) {
        List<String> pessoas = projeto.getPessoas();
        if (pessoas == null) {
            pessoas = new ArrayList<>();
        }
        pessoas.add(cpfPessoa);
        projeto.setPessoas(pessoas);
        return projetoRepository.save(projeto);
    }

    // Método para listar todos os projetos sem filtros
    public List<Projeto> listarTodosProjetos() {
        return projetoRepository.findAll();
    }
}
