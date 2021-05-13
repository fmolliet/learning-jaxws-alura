package ws;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import models.item.Filtro;
import models.item.Filtros;
import models.item.Item;
import models.item.ItemDao;
import models.item.ItemValidador;
import models.item.ListaItens;
import models.usuario.AutorizacaoException;
import models.usuario.TokenDao;
import models.usuario.TokenUsuario;

@WebService
/* 
 *  na mensagem SOAP, qual método/procedure será executado podemos usar o estilo? Document
 * RPC + EnCODED -> tem nome do metodo e tem os tipos xsd
 * 
 * @SOAPBinding(style=Style.RPC) para utilizar RPC
 * 
 *  Para usar BARE alterar o parameterStyle de Wrapper para BARE 
 * 
 *  Na codificação encoded a mensagem SOAP também trafega os tipos que não é o caso no exemplo da pergunta.
 * 
 *  No USE Não é mais recomendado o uso de Encoded 
 *  estilo de codificação Encoded traz a facilidade de leitura e como consequência alguns problemas de compatibilidade,
 *   validação e desempenho. Segundo a WS-I (Web Services Interoperability Organization) 
 *   deve-se usar apenas os estilos Document/Literal e RPC/Literal. E o JAX-WS foi projetado para seguir essa limitação.
 */
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public class EstoqueWS {

	private ItemDao dao = new ItemDao();

	@WebMethod(operationName = "todosOsItens")
	@WebResult(name = "item")
	public ListaItens getItens(@WebParam(name = "filtros") Filtros filtros) {
		System.out.println("Chamando getItens()");
		List<Filtro> lista = filtros.getLista();
		List<Item> itensResultado = dao.todosItens(lista);
		return new ListaItens(itensResultado);
	}

	@WebMethod(action = "CadastrarItem",  operationName = "CadastrarItem")
	@WebResult(name = "item")
	public Item cadastrarItem(@WebParam(name = "tokenUsuario", header = true) TokenUsuario token,
			@WebParam(name = "item") Item item) throws AutorizacaoException {

		System.out.println("Cadastrando " + item + ", " + token);

		boolean valido = new TokenDao().ehValido(token);

		if (!valido) {
			throw new AutorizacaoException("Token invalido");
		}

		new ItemValidador(item).validate();
		
		this.dao.cadastrar(item);
		
		return item;
	}
	
	@WebMethod(operationName = "CadastrarItem2")
	@WebResult(name = "item")
	public Item cadastrarItem2(@WebParam(name = "tokenUsuario", header = true) TokenUsuario token,
			@WebParam(name = "item") Item item) throws AutorizacaoException {
		return item;
	}
}