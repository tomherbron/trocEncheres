package fr.eni.projetjee.TrocEncheres.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.eni.projetjee.TrocEncheres.bll.ArticleVenduManagerException;
import fr.eni.projetjee.TrocEncheres.bll.IArticleVenduManager;
import fr.eni.projetjee.TrocEncheres.bll.SingletonArticleVenduManager;
import fr.eni.projetjee.TrocEncheres.bo.ArticleVendu;
import fr.eni.projetjee.TrocEncheres.dal.DALException;

/**
 * Servlet implementation class ServletDetailVente
 */
@WebServlet("/ServletDetailVente")
public class ServletDetailVente extends HttpServlet {

	IArticleVenduManager articleManager = SingletonArticleVenduManager.getInstance();
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServletDetailVente() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Integer id = Integer.parseInt(request.getParameter("id"));

		ArticleVendu article = null;

		try {

			article = articleManager.selectById(id);
			System.out.println(article);

		} catch (DALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArticleVenduManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		request.setAttribute("article", article);
		RequestDispatcher rd = request.getRequestDispatcher("./DetailVente.jsp");

		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();

		String montantSaisi = request.getParameter("proposition");
		ArticleVendu articleAModifier = (ArticleVendu) session.getAttribute("article");
		System.out.println( "l'article à modifier est : " + articleAModifier);
		Integer montantEnchere = Integer.parseInt(montantSaisi);
		System.out.println("le montant à update ets : " + montantEnchere);
		
		

	}

}
