package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.*;
import br.com.alura.literalura.model.*;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.CatalogoService;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    private final Scanner leitura = new Scanner(System.in);
    private final String ENDERECO_BASE = "https://gutendex.com/books/";

    private final ConsumoApi consumo;
    private final ConverteDados conversor;
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;
    private final CatalogoService catalogoService;

    // Construtor que injeta as dependências necessárias
    public Principal(ConsumoApi consumo, ConverteDados conversor, LivroRepository livroRepository, AutorRepository autorRepository, CatalogoService catalogoService) {
        this.consumo = consumo;
        this.conversor = conversor;
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
        this.catalogoService = catalogoService;
    }

    // Método principal que exibe o menu e gerencia as opções
    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                
                *********************************************
                *     Olá, seja bem vindo ao Literalura     *
                *********************************************
                
                Escolha uma opção:
                
                1 - Buscar livro pelo título
                2 - Listar livros registrados
                3 - Listar autores registrados
                4 - Listar autores vivos em determinado ano
                5 - Listar livros em determinado idioma
                6 - Extra, buscar livros por autor (API)
                7 - Extra, buscar livros por autor (Banco de Dados)
                        
                0 - Sair                                 
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1: buscarLivroWeb(); break;
                case 2: listarLivrosRegistrados(); break;
                case 3: listarAutoresRegistrados(); break;
                case 4: listarAutoresVivosPorAno(); break;
                case 5: listarLivrosPorIdioma(); break;
                case 6: buscarLivrosPorAutorWeb(); break;
                case 7: buscarLivrosPorAutorNoBanco(); break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    // Busca um livro na API pelo título e salva no banco de dados
    private void buscarLivroWeb() {
        System.out.println("Digite o título do livro que deseja buscar:");
        var nomeLivro = leitura.nextLine();

        // Verifica se o livro já existe no banco
        Optional<Livro> livroExistente = livroRepository.findByTituloContainingIgnoreCase(nomeLivro);
        if (livroExistente.isPresent()) {
            System.out.println("\nEste livro já está cadastrado no nosso banco de dados:");
            System.out.println(livroExistente.get());
            return;
        }

        try {
            // Faz a busca na API
            var nomeLivroCodificado = URLEncoder.encode(nomeLivro, StandardCharsets.UTF_8);
            String url = ENDERECO_BASE + "?search=" + nomeLivroCodificado;
            var json = consumo.obterDados(url);
            DadosBusca dadosBusca = conversor.obterDados(json, DadosBusca.class);

            // Filtra os resultados para encontrar o livro exato
            Optional<DadosLivro> dadosLivroOpt = dadosBusca.resultados().stream()
                    .filter(l -> l.titulo().equalsIgnoreCase(nomeLivro))
                    .findFirst();

            if (dadosLivroOpt.isPresent()) {
                DadosLivro dadosLivro = dadosLivroOpt.get();
                Autor autor;

                // Verifica se há informações de autor
                if (!dadosLivro.autores().isEmpty()) {
                    DadosAutor dadosAutor = dadosLivro.autores().get(0);
                    Optional<Autor> autorExistente = autorRepository.findByNomeContainingIgnoreCase(dadosAutor.nome());
                    autor = autorExistente.orElseGet(() -> {
                        // Cria novo autor se não existir
                        Autor novoAutor = new Autor();
                        novoAutor.setNome(dadosAutor.nome());
                        novoAutor.setAnoNascimento(dadosAutor.anoNascimento());
                        novoAutor.setAnoFalecimento(dadosAutor.anoFalecimento());
                        return novoAutor;
                    });
                } else {
                    // Usa autor "Desconhecido" se não houver informação
                    autor = autorRepository.findByNomeContainingIgnoreCase("Desconhecido")
                            .orElseGet(() -> {
                                Autor autorDesconhecido = new Autor();
                                autorDesconhecido.setNome("Desconhecido");
                                return autorDesconhecido;
                            });
                }

                // Cria e salva o novo livro
                Livro novoLivro = new Livro();
                novoLivro.setTitulo(dadosLivro.titulo());
                novoLivro.setIdioma(dadosLivro.idiomas().get(0));
                novoLivro.setNumeroDownloads(dadosLivro.numeroDownloads());
                novoLivro.getAutores().add(autor);
                livroRepository.save(novoLivro);

                System.out.println("\nLivro salvo com sucesso!");
                System.out.println("\nNovo livro cadastrado: " + novoLivro);

            } else {
                System.out.println("\nNão encontramos este livro. Por favor, verifique a ortografia ou tente um título diferente.");
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro durante a busca: " + e.getMessage());
        }
    }

    // Busca livros na API pelo nome do autor
    private void buscarLivrosPorAutorWeb() {
        System.out.println("Digite o nome do autor que deseja buscar:");
        var nomeAutor = leitura.nextLine();

        try {
            // Faz a busca na API
            var nomeAutorCodificado = URLEncoder.encode(nomeAutor, StandardCharsets.UTF_8);
            String url = ENDERECO_BASE + "?search=" + nomeAutorCodificado;
            var json = consumo.obterDados(url);
            DadosBusca dadosBusca = conversor.obterDados(json, DadosBusca.class);

            if (dadosBusca.resultados().isEmpty()) {
                System.out.println("\nNenhum livro encontrado para o autor '" + nomeAutor + "'.");
                return;
            }

            // Exibe os resultados encontrados
            System.out.println("\n--- LIVROS ENCONTRADOS PARA O AUTOR: " + nomeAutor + " ---");
            dadosBusca.resultados().forEach(livro -> {
                System.out.println("Título: " + livro.titulo());
                System.out.println("Idioma: " + livro.idiomas().get(0));
                System.out.println("Downloads: " + livro.numeroDownloads());
                if (!livro.autores().isEmpty()) {
                    System.out.println("Autor: " + livro.autores().get(0).nome());
                }
                System.out.println("----------------------");
            });

        } catch (Exception e) {
            System.out.println("Ocorreu um erro durante a busca: " + e.getMessage());
        }
    }

    // Busca livros no banco de dados pelo nome do autor
    private void buscarLivrosPorAutorNoBanco() {
        System.out.println("Digite o nome do autor que deseja buscar:");
        var nomeAutor = leitura.nextLine();

        // Busca os livros usando o serviço
        List<Livro> livrosDoAutor = catalogoService.listarLivrosPorAutor(nomeAutor);

        if (livrosDoAutor.isEmpty()) {
            System.out.println("\nNenhum livro encontrado para o autor '" + nomeAutor + "' no banco de dados.");
        } else {
            // Exibe os resultados encontrados
            System.out.println("\n--- LIVROS DO AUTOR " + nomeAutor + " NO BANCO DE DADOS ---");
            livrosDoAutor.forEach(livro -> {
                System.out.println("Título: " + livro.getTitulo());
                System.out.println("Idioma: " + livro.getIdioma());
                System.out.println("Downloads: " + livro.getNumeroDownloads());
                System.out.println("----------------------");
            });
        }
    }

    // Lista todos os livros registrados no banco de dados
    private void listarLivrosRegistrados() {
        System.out.println("\n--- LIVROS REGISTRADOS ---");
        List<Livro> livros = catalogoService.listarLivrosRegistrados();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        } else {
            livros.forEach(System.out::println);
        }
    }

    // Lista todos os autores registrados no banco de dados
    private void listarAutoresRegistrados() {
        System.out.println("\n--- AUTORES REGISTRADOS ---");
        List<Autor> autores = catalogoService.listarAutoresRegistrados();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor cadastrado.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    // Lista autores que estavam vivos em um determinado ano
    private void listarAutoresVivosPorAno() {
        System.out.println("Digite o ano para a pesquisa:");
        var ano = leitura.nextInt();
        leitura.nextLine();

        List<Autor> autoresVivos = catalogoService.listarAutoresVivosPorAno(ano);

        if (autoresVivos.isEmpty()) {
            System.out.println("\nNenhum autor vivo encontrado para o ano de " + ano + ".");
        } else {
            System.out.println("\n--- AUTORES VIVOS EM " + ano + " ---");
            autoresVivos.forEach(System.out::println);
        }
    }

    // Lista livros filtrados por idioma
    private void listarLivrosPorIdioma() {
        System.out.println("""
            Digite o idioma para a busca:
            es - espanhol
            en - inglês
            fr - francês
            pt - português
            """);
        var idioma = leitura.nextLine();

        List<Livro> livrosPorIdioma = catalogoService.listarLivrosPorIdioma(idioma);

        if (livrosPorIdioma.isEmpty()) {
            System.out.println("\nNenhum livro encontrado para o idioma '" + idioma + "'.");
        } else {
            System.out.println("\n--- LIVROS NO IDIOMA: " + idioma + " ---");
            livrosPorIdioma.forEach(System.out::println);
        }
    }
}