package fr.eni.projetjee.TrocEncheres.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.time.LocalDate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fr.eni.projetjee.TrocEncheres.bll.ArticleVenduManagerException;
import fr.eni.projetjee.TrocEncheres.bll.IArticleVenduManager;
import fr.eni.projetjee.TrocEncheres.bll.IEnchereManager;
import fr.eni.projetjee.TrocEncheres.bll.IUtilisateurManager;
import fr.eni.projetjee.TrocEncheres.bll.SingletonArticleVenduManager;
import fr.eni.projetjee.TrocEncheres.bll.SingletonEnchereManager;
import fr.eni.projetjee.TrocEncheres.bll.SingletonUtilisateurManager;
import fr.eni.projetjee.TrocEncheres.bll.UtilisateurManagerException;
import fr.eni.projetjee.TrocEncheres.bo.ArticleVendu;
import fr.eni.projetjee.TrocEncheres.bo.Enchere;
import fr.eni.projetjee.TrocEncheres.bo.Utilisateur;
import fr.eni.projetjee.TrocEncheres.dal.DALException;

@WebServlet("/ServletDetailVente")
public class ServletDetailVente extends HttpServlet {

	IArticleVenduManager articleManager = SingletonArticleVenduManager.getInstance();
	IEnchereManager enchereManager = SingletonEnchereManager.getInstance();
	IUtilisateurManager utilisateurManager = SingletonUtilisateurManager.getInstance();
	
	private static final long serialVersionUID = 1L;

	public ServletDetailVente() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

		Integer id = Integer.parseInt(request.getParameter("id"));

		ArticleVendu article = null;

		try {

			article = articleManager.selectById(id);
			System.out.println(article);

		
		} catch (ArticleVenduManagerException e) {
			e.printStackTrace();
		}

		session.setAttribute("utilisateur", utilisateur);
		request.setAttribute("article", article);
		RequestDispatcher rd = request.getRequestDispatcher("./DetailVente.jsp");

		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

		
		// Récupérer l'article et le montant de l'enchere, date, utilisateur

		String montantSaisi = request.getParameter("proposition");
		
		ArticleVendu articleAModifier = (ArticleVendu) session.getAttribute("article");
		Integer montantEnchere = Integer.parseInt(montantSaisi);
		LocalDate dateJour = LocalDate.now();
		
		Enchere enchere = new Enchere (dateJour, montantEnchere, articleAModifier.getUtilisateur(), articleAModifier);
		
		// Set le prix de vente au montant saisir par l'utilisateur
		
		if (utilisateur.getCredit() < articleAModifier.getMiseAPrix() && utilisateur.getCredit() < montantEnchere) {
			request.setAttribute("erreur", "Identifiant ou mot de passe incorrect");
			RequestDispatcher rd = request.getRequestDispatcher("./DetailVente.jsp");
			rd.forward(request, response);
		}
		
		if (montantEnchere >= articleAModifier.getMiseAPrix() && montantEnchere >= articleAModifier.getPrixDeVente()) {
			
			articleAModifier.setPrixDeVente(montantEnchere);
			utilisateur.setCredit(utilisateur.getCredit() - montantEnchere);
			
			enchere.setUtilisateur(utilisateur);
			
		} else {
			//Erreur
		}
		
		Utilisateur acquereur = enchere.getUtilisateur();
		System.out.println("L'acquéreur est : " + acquereur);
		
		
		// Update l'article en BDD
		
		try {
			
			articleManager.updatePdv(articleAModifier);
			enchereManager.insertEnchere(enchere);
			
			try {
				
				System.out.println("Les credits sont à : " + utilisateur.getCredit());
				utilisateurManager.updateUtilisateur(utilisateur);
				
			} catch (UtilisateurManagerException e) {
				e.printStackTrace();
			}
			
		} catch (ArticleVenduManagerException e) {
			e.printStackTrace();
		} catch (DALException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		session.setAttribute("utilisateur", utilisateur);
		
		request.setAttribute("acquereur", acquereur);
		request.setAttribute("article", articleAModifier);
		
		RequestDispatcher rd = request.getRequestDispatcher("./DetailVente.jsp");
		rd.forward(request, response);

	}

}
