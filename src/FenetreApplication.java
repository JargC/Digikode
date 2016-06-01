import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 * <b>Classe représentant la fenêtre principale de l'application Digikode</b>
 * @authors Jean Clemenceau, Terry Grosso
 * @version 1.0
 */
public class FenetreApplication extends JFrame implements ActionListener{
	
	/**
	 * La fonte pour le label titre
	 */
	
	private Font font = new Font("Lucida Console", Font.PLAIN, 17);

	/**
	 * Menu Fenêtre Application
	 */

	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu = new JMenu("Menu");

	private JMenuItem item1 = new JMenuItem("Mes informations");
	private JMenuItem item2 = new JMenuItem("Aide");
	private JMenuItem item3 = new JMenuItem("A propos");
	


	/**
	 * Serialisation de la classe
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Le pilote JDBC pour accèder à la base de données
	 */
	private String pilote = "com.mysql.jdbc.Driver";
	
	/**
	 * Les logs de connexion à la bdd
	 */

	private final String server = "jdbc:mysql://localhost/agenda";
	private final String user = "root";
	private final String pswd = "";

	private JPanel pan = new JPanel(new BorderLayout());

	///// Squelette de la table //////

	Object[][] data = {};  // les données de la table, vides à cet instant

	private String[] headers ={"Id","Date","Sport","De","à","Salle","Digicode",""}; // entêtes des colonnes

	DefaultTableModel model2 = new  DefaultTableModel(data,headers);	 // modélisation de l'ensemble de données contenues par la table    		

	@SuppressWarnings("serial")
	private JTable table = new JTable(model2){
		public boolean isCellEditable(int row, int col) {		// on surcharge la méthode isCellEditable afin
			if(col>=0&&col<7)									// d'empecher les modifications utilisateurs sur la table (sauf pour la colonne du bouton, autrement
				return false;										// elle serait inutilisable)
			else return true;
		}
	};; // affectation du modele à la table

	//////////////////////////////////

	/**
	 * Constructeur de la fenêtre
	 * @param login
	 * @throws IOException
	 */
	public FenetreApplication(String login) throws IOException {
		

		/**
		 * Mise en place d'un Menu
		 */
		//On initialise nos menus      
		this.menu.add(item1);
		this.menu.add(item2);
		this.menu.add(item3);

		item1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				JOptionPane.showMessageDialog(null, getInfosCompte(login),"Mes informations",JOptionPane.PLAIN_MESSAGE);
				
			}        
		});

		item2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1) {

				Object[] message = {
						"Vous êtes sur la fenêtre contenant la liste de vos réservations en cours de validité.",
						"Veuillez cliquer sur le bouton Générer pour modifier aléatoirement le digicode correspondant. \n\n",
						"Si vous souhaitez changer de mot de passe, veuillez contacter l'administrateur."
				};
				JOptionPane.showMessageDialog(null, message,"Aide",JOptionPane.PLAIN_MESSAGE);

			}        
		});

		item3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg1) {

				Object[] message = {
						"Digikode v1.0",
						"Application créée par \n\n",
						"Jean CLEMENCEAU & Terry GROSSO",
						"BTS SIO SLAM - ECE TECH"
				};
				JOptionPane.showMessageDialog(null, message,"À propos",JOptionPane.PLAIN_MESSAGE);

			}        
		});

		//L'ordre d'ajout va déterminer l'ordre d'apparition dans le menu de gauche à droite
		//Le premier ajouté sera tout à gauche de la barre de menu et inversement pour le dernier
		
		this.menuBar.add(menu);
		this.setJMenuBar(menuBar);

		this.setTitle(login);
		this.setSize(700,350);
		setIconImage(ImageIO.read(new File("res/icone.png")));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);

		pan.setLayout(new BorderLayout());

		JLabel label = new JLabel("Digikode - Générateur de digicodes pour la M2L");
		label.setFont(font);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);

		pan.add(label, BorderLayout.NORTH); 

		setContentPane(pan);

		createReservationsTable(login);

		this.setVisible(true);   

	}

	/**
	 * Méthode permettant de récupérer la date du jour
	 * @return la date du jour sous le meme format que celui de la base de données
	 */
	private String getDateJour(){

		String format = "yyyy-MM-dd";  // Le format souhaité pour la date

		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(format); // L'objet de formatage
		java.util.Date date = new java.util.Date(); // On récupère la date du jour

		String dateJour = formater.format(date); // On la formate

		return dateJour;

	}
	
	
	/**
	 * Méthode permettant de récupérer les informations du compte pour l'onglet correspondant dans le JMenu
	 * @param login
	 * @return les informations du compte contenues dans la BDD sous la forme d'un object
	 */
	private Object[] getInfosCompte(String login){
				
		Object[] message = null;
		
		String req = "SELECT nom, prenom, telephone, email, adresse, ville, codepostal, sport, login, credits, nombre_reservations "
				+ "FROM contact "
				+ "WHERE login = '"+login+"'";   
		
		ResultSet res;

		try{
			Class.forName(pilote).newInstance();
			Connection con = DriverManager.getConnection(server,user,pswd);
			Statement st = con.createStatement();
			res = st.executeQuery(req);
			
			if(res.next()){
				message = new Object []{
					"Nom : " + res.getString(1),// 
					"Prénom : " + res.getString(2),// 
					"Téléphone : " + res.getString(3),//
					"E-mail : " + res.getString(4),//
					"Adresse : " + res.getString(5),//
					"Ville : " + res.getString(6),//
					"Code postal : " + res.getString(7),//
					"Sport préféré : " + res.getString(8),//
					"Pseudonyme : " + res.getString(9),//
					"Crédits : " + res.getString(10),//
					" Nombre de réservations total : " + res.getString(11),//
				};
			}
			
		}

		catch(Exception e1){
			System.out.println(e1);
		}
		
		return message;
		
	}


	/**
	 * Méthode créant un digicode aléatoire, le mettant à jour dans la base de données ainsi que dans le tableau de l'application
	 * @param id
	 * @param modelRow
	 */
	public void createDigicode(String id, int modelRow) {

		String possibilites = "AB0123456789";		// les symboles utilisables sur  l'interface de digicode
		String digicode = "";
		Random r = new Random();					// objet pour générer la part d'aleatoire dans la construction de nos digicodes

		for (int i = 0; i < 5; i++) {				// i correspond à la longueur du digicode souhaitée
			int number = r.nextInt(11);			// on prend un entier compris entre 0 et 11
			digicode += possibilites.charAt(number);
		}    	

		String req = "Update reservation set digicode ='" + digicode + "' WHERE id ='" + id + "'";   


		try{
			Class.forName(pilote).newInstance();
			Connection con = DriverManager.getConnection(server,user,pswd);
			Statement st = con.createStatement();
			st.executeUpdate(req);
			table.getModel().setValueAt(digicode, modelRow, 6);			// on met à jour la valeur dans le tableau pour un affichage immédiat
		}

		catch(Exception e1){
			System.out.println(e1);
		}

	}


	/**
	 * Méthode créant le tableau récapitulatif des réservations de la personne
	 * @param login
	 */
	@SuppressWarnings("serial")
	private void createReservationsTable(String login) {  

		/////////////////////// Récupération des données ///////////////////////////////////

		String req = "SELECT * FROM reservation WHERE login = '"+login+"' AND date >= '"+getDateJour()+"'"; // On ne récupère que les reservations à venir

		ResultSet res;

		try{

			Class.forName(pilote).newInstance();
			Connection con = DriverManager.getConnection(server,user,pswd);
			Statement st = con.createStatement();
			res = st.executeQuery(req); 

			if(res.next())
			{
				////////////////////////////////////// Creation de la table ///////////////////////////////////////    			



				table.getTableHeader().setResizingAllowed(false); // on interdit le redimensionnement des colonnes
				table.getTableHeader().setReorderingAllowed(false); // on interdit le réagencement des colonnes

				JScrollPane pane = new JScrollPane(table); 				
				pane.setAutoscrolls(true);
				pan.add(pane, BorderLayout.CENTER);


				////////////////////////////////////// Ajout des données dans la table ///////////////////////////

				do{

					model2.addRow(new Object []{
							res.getString(1),// 
							res.getString(2),// 
							res.getString(3),//
							res.getString(4),//
							res.getString(5),//
							res.getString(6),
							res.getString(7),//
							"Générer"
					});
				}

				while(res.next());

				Action generer = new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {							// action se réalise lors d'un click sur un bouton "générer"
						table = (JTable) e.getSource();
						int modelRow = Integer.valueOf(e.getActionCommand());				// on recupere la ligne sujette
						String id = table.getModel().getValueAt(modelRow, 0).toString();	// on recupere l'id correspondant au futur traitement
						createDigicode(id, modelRow);										// on genere le digicode
					}
				};


				ButtonColumn bc = new ButtonColumn(table, generer, 7); // on ajoute la colonne de boutons
				bc.setMnemonic(KeyEvent.VK_D);

			}

			else System.out.println("Pas de réservations");    		

		}

		catch(Exception e1){
			System.out.println(e1);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
}