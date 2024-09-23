package br.insper.prova.projetoService;

import br.insper.prova.projetos.Projeto;
import br.insper.prova.projetos.ProjetoRepository;
import br.insper.prova.projetos.ProjetoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ProjetoServiceTests {

    @InjectMocks
    private ProjetoService projetoService;

    @Mock
    private ProjetoRepository projetoRepository;

    @Test
    public void testCadastrarProjeto() {
        Projeto projeto = new Projeto();
        projeto.setNome("Projeto 1");
        projeto.setStatus("Em andamento");

        Mockito.when(projetoRepository.save(Mockito.any(Projeto.class))).thenReturn(projeto);

        Projeto projetoSalvo = projetoService.cadastrarProjeto(projeto);

        Assertions.assertNotNull(projetoSalvo);
        Assertions.assertEquals("Projeto 1", projetoSalvo.getNome());
        Assertions.assertEquals("Em andamento", projetoSalvo.getStatus());
    }

    @Test
    public void testListarProjetosComStatus() {
        List<Projeto> projetos = new ArrayList<>();
        Projeto projeto = new Projeto();
        projeto.setNome("Projeto 1");
        projeto.setStatus("Finalizado");

        projetos.add(projeto);

        Mockito.when(projetoRepository.findAll()).thenReturn(projetos);

        List<Projeto> projetosFiltrados = projetoService.listarProjetos("Finalizado");

        Assertions.assertNotNull(projetosFiltrados);
        Assertions.assertEquals(1, projetosFiltrados.size());
        Assertions.assertEquals("Projeto 1", projetosFiltrados.get(0).getNome());
        Assertions.assertEquals("Finalizado", projetosFiltrados.get(0).getStatus());
    }

    @Test
    public void testListarProjetosSemStatus() {
        List<Projeto> projetos = new ArrayList<>();
        Projeto projeto1 = new Projeto();
        projeto1.setNome("Projeto 1");
        projeto1.setStatus("Finalizado");

        Projeto projeto2 = new Projeto();
        projeto2.setNome("Projeto 2");
        projeto2.setStatus("Em andamento");

        projetos.add(projeto1);
        projetos.add(projeto2);

        Mockito.when(projetoRepository.findAll()).thenReturn(projetos);

        List<Projeto> todosProjetos = projetoService.listarProjetos(null);

        Assertions.assertNotNull(todosProjetos);
        Assertions.assertEquals(2, todosProjetos.size());
    }

    @Test
    public void testBuscarProjetoPorIdQuandoExiste() {
        Projeto projeto = new Projeto();
        projeto.setId(1);
        projeto.setNome("Projeto 1");

        Mockito.when(projetoRepository.findById(1)).thenReturn(Optional.of(projeto));

        Optional<Projeto> projetoEncontrado = projetoService.buscarProjetoPorId(1);

        Assertions.assertTrue(projetoEncontrado.isPresent());
        Assertions.assertEquals("Projeto 1", projetoEncontrado.get().getNome());
    }

    @Test
    public void testBuscarProjetoPorIdQuandoNaoExiste() {
        Mockito.when(projetoRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Projeto> projetoEncontrado = projetoService.buscarProjetoPorId(1);

        Assertions.assertFalse(projetoEncontrado.isPresent());
    }

    @Test
    public void testAdicionarPessoaNoProjeto() {
        Projeto projeto = new Projeto();
        projeto.setId(1);
        projeto.setNome("Projeto 1");
        projeto.setPessoas(new ArrayList<>());

        Mockito.when(projetoRepository.save(Mockito.any(Projeto.class))).thenReturn(projeto);

        Projeto projetoAtualizado = projetoService.adicionarPessoaNoProjeto(projeto, "12345678900");

        Assertions.assertNotNull(projetoAtualizado);
        Assertions.assertEquals(1, projetoAtualizado.getPessoas().size());
        Assertions.assertTrue(projetoAtualizado.getPessoas().contains("12345678900"));
    }
}
