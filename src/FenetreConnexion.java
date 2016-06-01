import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/**
 * <b>Classe représentant la fenêtre de connexion de l'application Digikode</b>
 * 
 * @authors Jean Clemenceau, Terry Grosso
 * @version 1.0
 *
 */
public class FenetreConnexion extends JFrame implements ActionListener {

	/**
	 * Serialisation de la classe
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Le pilote JDBC pour accèder à la base de données
	 */
	private String pilote = "com.mysql.jdbc.Driver";
	
	/**
	 * L'adresse du serveur pour la connexion à la base de données
	 */	
	private final String server = "jdbc:mysql://localhost/agenda";
	
	/**
	 * L'identifiant utilisé pour se connecter à la base de données
	 */
	private final String user = "root";
	
	/**
	 * Le mot de passe correspondant à l'identifiant utilisé pour se connecter à la base de données
	 */
	private final String pswd = "";
	
	/**
	 * Les panels utilisés dans la fenêtre de connexion
	 */
	private JPanel pan1, pan2;

	/**
	 * Les labels utilisés dans la fenêtre de connexion
	 */
	private JLabel titre, details, label_login, label_mdp, invis1, invis2;

	/**
	 * Le champ de saisie de l'identifiant pour l'utilisateur
	 */
	private JTextField login;

	/**
	 * Le champ de saisie de mot de passe pour l'utilisateur
	 */
	private JPasswordField mdp;

	/**
	 * Le bouton pour envoyer la requête de connexion
	 */
	private JButton submit;

	////////////////////////// MAIN ///////////////////////////////////

	public static void main(String[] args) throws IOException {
		JFrame digikode = new FenetreConnexion();
		digikode.setVisible(true);
	}

	/**
	 * Constructeur de la fenêtre
	 * @throws IOException
	 */

	public FenetreConnexion() throws IOException {

		setTitle("Connexion");
		setSize(400, 500);
		getContentPane().setLayout(new GridLayout(2, 1));
		setLocationRelativeTo(this.getParent());
		setBackground(Color.gray);
		setIconImage(ImageIO.read(new File("res/icone.png")));
		this.setResizable(false);

		invis1 = new JLabel("");
		invis1.setPreferredSize(new Dimension(200, 60));

		invis2 = new JLabel("");
		invis2.setPreferredSize(new Dimension(200, 30));

		titre = new JLabel("Digikode");
		titre.setPreferredSize(new Dimension(200, 50));
		titre.setFont(new Font("Verdana", 1, 40));

		details = new JLabel("  (Application M2L)");
		details.setPreferredSize(new Dimension(200, 50));
		details.setFont(new Font("Verdana", 2, 19));

		label_login = new JLabel("Identifiant : ");
		label_login.setPreferredSize(new Dimension(100, 50));

		label_mdp = new JLabel("Mot de passe : ");
		label_mdp.setPreferredSize(new Dimension(100, 50));

		login = new JTextField(16);
		login.setPreferredSize(new Dimension(100, 30));

		mdp = new JPasswordField(16);
		mdp.setPreferredSize(new Dimension(100, 30));

		/**
		 * Appuyer sur la touche Entrée dans la case mdp appelle le bouton "Se Connecter"
		 */
		mdp.addKeyListener(
				new KeyListener(){

					/*la méthode keyPressed est appelée lorsque l'on presse une touche*/   
					public void keyPressed(KeyEvent e){
						if (e.getKeyCode()== KeyEvent.VK_ENTER) {
							submit.doClick(0);
						}
					}

					/*les deux méthodes suivantes doivent être également écrites pour pouvoir réaliser l'interface KeyListener*/               
					public void keyReleased(KeyEvent e){}
					public void keyTyped(KeyEvent e){}

				}  
				);

		submit = new JButton("Se connecter");
		submit.setPreferredSize(new Dimension(200, 40));

		pan1 = new JPanel();
		pan1.setBackground(Color.LIGHT_GRAY);

		pan2 = new JPanel();


		this.add(pan1);
		this.add(pan2);
		pan1.add(invis1);
		pan1.add(titre);
		pan1.add(details);
		pan2.add(label_login);
		pan2.add(login);
		pan2.add(label_mdp);
		pan2.add(mdp);
		pan2.add(invis2);
		pan2.add(submit);
		submit.addActionListener(this);

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Retourne le mot de passe associé à l'identifiant correspondant
	 * @param login
	 * @return un mot de passe sous la forme d'un tableau de caractères
	 * 
	 */
	private char[] convertPassword(String login) {
		char[] password_empty = new char[0];
		try{
			Class.forName(pilote);

			Connection connexion = DriverManager.getConnection(server,user,pswd);

			Statement instruction = connexion.createStatement();

			ResultSet resultat = instruction.executeQuery("SELECT password FROM contact WHERE login ='"+login+"'");

			if(resultat.next())
			{
				String motDePasse = resultat.getString(1);
				char[] password = new char[motDePasse.length()];
				for (int i = 0; i < motDePasse.length(); i++) {
					password[i] = motDePasse.charAt(i);
				}
				return password;

			}
			else {
				JOptionPane.showMessageDialog(null,"Login incorrect ! ","Erreur",1);
			}		
			connexion.close();
		}
		catch (Exception e){
			System.out.println("echec pilote : "+e);
		}

		return password_empty;
	}

	/**
	 * 
	 * @param input		l'identifiant entré par l'utilisateur
	 * @param test		le mot de passe entré par l'utilisateur
	 * @return boolean, true si le mot de passe rentré est correct, false dans le cas contraire
	 */
	private static boolean isPasswordCorrect(char[] input, char[] test) {
		boolean isCorrect = true;

		if (input.length != test.length) {
			isCorrect = false;
		} else {
			isCorrect = Arrays.equals (input, test);
		}

		Arrays.fill(test,'0');

		return isCorrect;
	}

	/**
	 * Méthode gérant l'événement du click sur le bouton de connexion
	 */
	public void actionPerformed(ActionEvent e) {

		String login_test = login.getText();
		char[] password_test = mdp.getPassword();
		char[] actual_password = convertPassword(login_test);
		if(isPasswordCorrect(password_test, actual_password))
		{
			try {
				this.Connect(login_test);
			} catch (IOException e1) {
				e1.printStackTrace();
			}       
		}
		else
		{
			JOptionPane.showMessageDialog(null,"Mot de passe éronné ! ","",JOptionPane.PLAIN_MESSAGE);
		}

	}

	/**
	 * Envoie l'utilisateur vers la fenêtre suivante
	 * @param login
	 * @throws IOException 
	 */
	private void Connect(String login) throws IOException {
		JOptionPane.showMessageDialog(null,"Connexion réussie ! ","",JOptionPane.PLAIN_MESSAGE);
		this.dispose();
		new FenetreApplication(login);		
	}

}
