package br.com.caelum.livraria.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import br.com.caelum.livraria.dao.DAO;
import br.com.caelum.livraria.modelo.Autor;
import br.com.caelum.livraria.modelo.Livro;

@ManagedBean
@ViewScoped
public class LivroBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Livro livro = new Livro();

	private Integer autorId;

	private List<Livro> livros = new ArrayList<Livro>();

	public boolean precoEhMenor(Object valorColuna, Object filtroDigitado, Locale locale) {

		String textoDigitado = (filtroDigitado == null) ? null : filtroDigitado.toString().trim();

		System.out.println("Filtrando pelo " + textoDigitado + ", Valor do elemento: " + valorColuna);

		if (textoDigitado == null || textoDigitado.equals(""))
			return true;

		if (valorColuna == null)
			return false;

		try {
			Double precoDigitado = Double.valueOf(textoDigitado);
			Double precoColuna = (Double) valorColuna;

			return precoColuna.compareTo(precoDigitado) < 0;

		} catch (NumberFormatException e) {
			return false;
		}
	}

	public List<Livro> getLivros() {
		DAO<Livro> dao = new DAO<Livro>(Livro.class);

		if (livros.isEmpty())
			livros = dao.listaTodos();

		return livros;
	}

	public void carregarLivroPelaId() {
		this.livro = new DAO<Livro>(Livro.class).buscaPorId(this.livro.getId());
	}

	public void gravarAutor() {
		Autor autor = new DAO<Autor>(Autor.class).buscaPorId(this.autorId);
		this.livro.adicionaAutor(autor);
		System.out.println("Escrito por: " + autor.getNome());
	}

	public void gravar() {
		System.out.println("Gravando livro " + this.livro.getTitulo());

		if (livro.getAutores().isEmpty()) {
			FacesContext.getCurrentInstance().addMessage("autor",
					new FacesMessage("Livro deve ter pelo menos um Autor."));
			return;
		}

		if (this.livro.getId() == null) {
			new DAO<Livro>(Livro.class).adiciona(this.livro);
		} else {
			new DAO<Livro>(Livro.class).atualiza(this.livro);
		}

		limparCacheDeLivros();
		this.livro = new Livro();
	}

	private void limparCacheDeLivros() {
		livros.clear();
	}

	public void remover(Livro livro) {
		System.out.println("Removendo livro");
		new DAO<Livro>(Livro.class).remove(livro);
		limparCacheDeLivros();
	}

	public void removerAutorDoLivro(Autor autor) {
		this.livro.removeAutor(autor);
	}

	public void carregar(Livro livro) {
		System.out.println("Carregando livro");
		this.livro = livro;
	}

	public String formAutor() {
		System.out.println("Chamanda do formulário do Autor.");
		return "autor?faces-redirect=true";
	}

	public void comecaComDigitoUm(FacesContext fc, UIComponent component, Object value) throws ValidatorException {

		String valor = value.toString();
		if (!valor.startsWith("1")) {
			throw new ValidatorException(new FacesMessage("ISBN deveria começar com 1"));
		}

	}

	public void setAutorId(Integer autorId) {
		this.autorId = autorId;
	}

	public Integer getAutorId() {
		return autorId;
	}

	public Livro getLivro() {
		return livro;
	}

	public List<Autor> getAutores() {
		return new DAO<Autor>(Autor.class).listaTodos();
	}

	public List<Autor> getAutoresDoLivro() {
		return this.livro.getAutores();
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}
}
