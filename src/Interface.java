import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Interface extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pilote = "com.mysql.jdbc.Driver";
	private JPanel pan1, pan2;
	private JLabel titre, details, label_login, label_mdp, invis1, invis2;
	private JTextField login;
	private JPasswordField mdp;
	private JButton submit;
	private Interface2 interface_index;
	
	public void Connect(String login, String mdp) {
		try{
			Class.forName(pilote);
	 
			Connection connexion = DriverManager.getConnection("jdbc:mysql://localhost/agenda","root","");
	 
			Statement instruction = connexion.createStatement();
	 
			ResultSet resultat = instruction.executeQuery("SELECT password FROM contact WHERE login ='"+login+"'");
			
			if(resultat.next())
			{
				String motDePasse = resultat.getString(1);
				
				if(motDePasse.equals(mdp))
				{
					JOptionPane.showMessageDialog(null,"Connexion réussie ! ","",JOptionPane.PLAIN_MESSAGE);
					this.dispose();
			        interface_index = new Interface2(login);		
					
				}
				else {
					JOptionPane.showMessageDialog(null,"Mot de passe incorrect ! ","Erreur",1);
				}
			}
			else {
				JOptionPane.showMessageDialog(null,"Login et/ou mot de passe incorrect(s) ! ","Erreur",1);
			}
		
			connexion.close();
		}
		catch (Exception e){
			System.out.println("echec pilote : "+e);
		}
	}
	
	public Interface() {
		setTitle("Connexion");
		setSize(400, 500);
		getContentPane().setLayout(new GridLayout(2, 1));
		setLocationRelativeTo(this.getParent());
		setBackground(Color.gray);
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
		submit = new JButton("Se connecter");
		submit.setPreferredSize(new Dimension(200, 40));
		
		pan1 = new JPanel();
		pan2 = new JPanel();
		pan1.setBackground(Color.LIGHT_GRAY);
		
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
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String login_test = login.getText();
        String password_test = mdp.getText();
    	System.out.println(login_test);
    	System.out.println(password_test);
		this.Connect(login_test, password_test);
	}

}
